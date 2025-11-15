package project.process.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;
import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import project.process.grpc.ProcessProto;
import project.process.grpc.ProcessServiceGrpc;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * gRPC wrapper around the existing ProcessAPI implementation
 */
public class ProcessServiceImpl
    extends
      ProcessServiceGrpc.ProcessServiceImplBase {

  private final ProcessApi processApi;

  public ProcessServiceImpl(ProcessApi processApi) {
    this.processApi = processApi;
  }

  @Override
  public void load(ProcessProto.LoadRequest request,
      StreamObserver<ProcessProto.LoadResponse> responseObserver) {
    try {
      if (request == null || request.getResource() == null) {
        ProcessProto.LoadResponse resp = ProcessProto.LoadResponse.newBuilder()
            .setStatus(ProcessProto.ApiStatus.INVALID_REQUEST)
            .setMessage("LoadRequest or resource is null").build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        return;
      }

      Resource<?> resource = convertProtoResource(request.getResource());
      Delimiter delim = convertDelimiter(request.getDelimiter());

      LoadRequest loadReq = new LoadRequest(resource, delim);
      LoadResponse loadResp = processApi.load(loadReq);

      if (loadResp == null) {
        throw new IllegalStateException(
            "processApi.load returned null for resource="
                + (request.getResource() != null
                    ? request.getResource().toString()
                    : "null"));
      }

      ProcessProto.LoadResponse.Builder builder = ProcessProto.LoadResponse
          .newBuilder().setStatus(convertStatus(loadResp.getStatus()))
          .setMessage(loadResp.getMessage());

      if (loadResp.getPayload() != null) {
        for (Object o : loadResp.getPayload()) {
          if (o instanceof Integer) {
            builder.addData((Integer) o);
          } else if (o instanceof String) {
            try {
              builder.addData(Integer.parseInt((String) o));
            } catch (NumberFormatException e) {
              // skip invalid strings
            }
          }
        }
      }

      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      /*
       * ProcessProto.LoadResponse resp = ProcessProto.LoadResponse.newBuilder()
       * .setStatus(ProcessProto.ApiStatus.ERROR)
       * .setMessage("Unexpected error: " + e.getMessage()).build();
       * responseObserver.onNext(resp); responseObserver.onCompleted();
       */

      // print full stack to server log
      e.printStackTrace(System.err);

      // build a message that includes the exception class + short message
      String shortMsg = (e.getMessage() != null)
          ? e.getMessage()
          : e.toString();

      // If you want the full stacktrace in the proto message (useful in dev),
      // otherwise include just the class + message.
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      String fullTrace = sw.toString();

      ProcessProto.LoadResponse resp = ProcessProto.LoadResponse.newBuilder()
          .setStatus(ProcessProto.ApiStatus.ERROR)
          .setMessage("Unexpected error: " + shortMsg + "\n" + fullTrace)
          .build();
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void store(ProcessProto.StoreRequest request,
      StreamObserver<ProcessProto.StoreResponse> responseObserver) {
    try {
      if (request == null || request.getResource() == null) {
        ProcessProto.StoreResponse resp = ProcessProto.StoreResponse
            .newBuilder().setStatus(ProcessProto.ApiStatus.INVALID_REQUEST)
            .setMessage("StoreRequest or resource is null").build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        return;
      }

      Resource<?> resource = convertProtoResource(request.getResource());
      Delimiter delim = convertDelimiter(request.getDelimiter());

      List<Integer> payload = new ArrayList<>(request.getDataList());
      StoreRequest storeReq = new StoreRequest(resource, payload, delim);
      StoreResponse storeResp = processApi.store(storeReq);

      ProcessProto.StoreResponse resp = ProcessProto.StoreResponse.newBuilder()
          .setStatus(convertStatus(storeResp.getStatus()))
          .setMessage(
              storeResp.getMessage() != null ? storeResp.getMessage() : "")
          .build();

      responseObserver.onNext(resp);
      responseObserver.onCompleted();

    } catch (Exception e) {
      ProcessProto.StoreResponse resp = ProcessProto.StoreResponse.newBuilder()
          .setStatus(ProcessProto.ApiStatus.ERROR)
          .setMessage("Unexpected error: " + e.getMessage()).build();
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    }
  }

  /**
   * Convert proto Resource to your internal Resource<?> type
   */
  private Resource<?> convertProtoResource(ProcessProto.Resource proto) {
    ResourceType type = ResourceType.UNKNOWN;
    Object payload = null;

    switch (proto.getType()) {
      case FILE :
        type = ResourceType.FILE;
        payload = proto.getUri().trim();
        break;
      case DATABASE :
        type = ResourceType.DATABASE;
        payload = proto.getUri().trim();
        break;
      case STREAM :
        type = ResourceType.STREAM;
        payload = proto.getUri().trim();
        break;
      case CUSTOM :
        type = ResourceType.CUSTOM;
        payload = new ArrayList<Integer>(proto.getDataList());
        break;
      default :
        type = ResourceType.UNKNOWN;
    }

    if (type == ResourceType.CUSTOM) {
      return new Resource<>(type, (List<Integer>) payload);
    } else {
      return new Resource<>(type, (String) payload);
    }
  }

  /**
   * Convert string to Delimiter enum, default to COMMA
   */
  private Delimiter convertDelimiter(String delimStr) {
    if (delimStr == null || delimStr.isEmpty()) {
      return Delimiter.defaultDelimiter();
    }
    switch (delimStr.trim()) {
      case ":" :
        return Delimiter.COLON;
      case ";" :
        return Delimiter.SEMICOLON;
      case "|" :
        return Delimiter.PIPE;
      case "," :
        return Delimiter.COMMA;
      default :
        return Delimiter.defaultDelimiter();
    }
  }

  /**
   * Convert internal ApiStatus to ProcessProto.ApiStatus
   */
  private ProcessProto.ApiStatus convertStatus(shared.stuff.ApiStatus status) {
    switch (status) {
      case SUCCESS :
        return ProcessProto.ApiStatus.SUCCESS;
      case NOT_AUTHORIZED :
        return ProcessProto.ApiStatus.NOT_AUTHORIZED;
      case NOT_FOUND :
        return ProcessProto.ApiStatus.NOT_FOUND;
      case INVALID_REQUEST :
        return ProcessProto.ApiStatus.INVALID_REQUEST;
      case ERROR :
        return ProcessProto.ApiStatus.ERROR;
      case UNKNOWN :
      default :
        return ProcessProto.ApiStatus.UNKNOWN;
    }
  }
}
