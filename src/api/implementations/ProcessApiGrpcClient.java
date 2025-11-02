package api.implementations;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import project.process.grpc.ProcessProto;
import project.process.grpc.ProcessServiceGrpc;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;

/**
 * Grpc process client - keeps same method signatures as ProcessAPI but uses the
 * GRPC server to perform the tasks
 */
public class ProcessApiGrpcClient implements ProcessApi {

  private final ProcessServiceGrpc.ProcessServiceBlockingStub stub;

  public ProcessApiGrpcClient(String host, int port) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext().build();
    stub = ProcessServiceGrpc.newBlockingStub(channel);
  }

  @Override
  public LoadResponse load(LoadRequest request) {
    Delimiter delimiterToUse;
    if (request.getDelimiter() != null) {
      delimiterToUse = request.getDelimiter();
    } else {
      delimiterToUse = Delimiter.defaultDelimiter();
    }

    ProcessProto.LoadRequest.Builder protoReq = ProcessProto.LoadRequest
        .newBuilder();
    protoReq.setResource(convertToProtoResource(request.getSource()));
    protoReq.setDelimiter(delimiterToUse.getValue());

    ProcessProto.LoadResponse protoResp = stub.load(protoReq.build());

    Delimiter responseDelimiter;
    if (request.getDelimiter() != null) {
      responseDelimiter = request.getDelimiter();
    } else {
      responseDelimiter = Delimiter.defaultDelimiter();
    }

    String msg = protoResp.getMessage();
    if (msg.isEmpty()) {
      msg = null;
    }

    return new LoadResponse(convertStatus(protoResp.getStatus()),
        new ArrayList<>(protoResp.getDataList()), responseDelimiter, msg);
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    Delimiter delimiterToUse;
    if (request.getDelimiter() != null) {
      delimiterToUse = request.getDelimiter();
    } else {
      delimiterToUse = Delimiter.defaultDelimiter();
    }

    ProcessProto.StoreRequest.Builder protoReq = ProcessProto.StoreRequest
        .newBuilder();
    protoReq.setResource(convertToProtoResource(request.getDestination()));
    protoReq.setDelimiter(delimiterToUse.getValue());

    // Ensure payload is List<Integer> for protobuf
    List<Integer> payloadInt = new ArrayList<>();
    for (Object obj : request.getPayload()) {
      payloadInt.add((Integer) obj);
    }
    protoReq.addAllData(payloadInt);

    ProcessProto.StoreResponse protoResp = stub.store(protoReq.build());

    String msg = protoResp.getMessage();
    if (msg.isEmpty()) {
      msg = null;
    }

    return new StoreResponse(convertStatus(protoResp.getStatus()),
        request.getDestination(), msg);
  }

  private ProcessProto.Resource convertToProtoResource(Resource<?> resource) {
    ProcessProto.Resource.Builder builder = ProcessProto.Resource.newBuilder();

    switch (resource.getType()) {
      case FILE :
        builder.setType(ProcessProto.ResourceType.FILE)
            .setUri((String) resource.getUri());
        break;
      case DATABASE :
        builder.setType(ProcessProto.ResourceType.DATABASE)
            .setUri((String) resource.getUri());
        break;
      case STREAM :
        builder.setType(ProcessProto.ResourceType.STREAM)
            .setUri((String) resource.getUri());
        break;
      case CUSTOM :
        builder.setType(ProcessProto.ResourceType.CUSTOM);
        List<Integer> dataList = new ArrayList<>();
        for (Object obj : resource.getData()) {
          dataList.add((Integer) obj);
        }
        builder.addAllData(dataList);
        break;
      default :
        builder.setType(ProcessProto.ResourceType.UNKNOWN_RESOURCE);
    }

    return builder.build();
  }

  private ApiStatus convertStatus(ProcessProto.ApiStatus protoStatus) {
    switch (protoStatus) {
      case SUCCESS :
        return ApiStatus.SUCCESS;
      case NOT_AUTHORIZED :
        return ApiStatus.NOT_AUTHORIZED;
      case NOT_FOUND :
        return ApiStatus.NOT_FOUND;
      case INVALID_REQUEST :
        return ApiStatus.INVALID_REQUEST;
      case ERROR :
        return ApiStatus.ERROR;
      default :
        return ApiStatus.UNKNOWN;
    }
  }
}