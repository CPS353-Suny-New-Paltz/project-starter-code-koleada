package project.network.grpc;

import java.util.ArrayList;
import java.util.List;

import api.implementations.NetworkAPI;
import io.grpc.stub.StreamObserver;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class NetworkServiceImpl
    extends
      NetworkServiceGrpc.NetworkServiceImplBase {

  private final NetworkAPI networkAPI;

  public NetworkServiceImpl() {
    this.networkAPI = new NetworkAPI(); // ensures readWrite (ProcessAPI) is
                                        // initialized
  }

  @Override
  public void compute(NetworkProto.ComputationRequest protoReq,
      StreamObserver<NetworkProto.ComputationResponse> responseObserver) {

    try {
      System.out.println("---- Received Compute request ----");

      // ---------------------------
      // Input resource
      // ---------------------------
      NetworkProto.Resource protoIn = null;
      if (protoReq.hasInputResource()) {
        protoIn = protoReq.getInputResource();
      }

      Resource<?> inputResource = null;
      if (protoIn != null) {
        ResourceType rtype = fromProtoResourceType(protoIn.getType());
        if (rtype == ResourceType.CUSTOM) {
          inputResource = new Resource<>(rtype,
              new ArrayList<>(protoIn.getDataList()));
        } else {
          inputResource = new Resource<>(rtype, protoIn.getUri());
        }
      }

      // ---------------------------
      // Output resource
      // ---------------------------
      NetworkProto.Resource protoOut = null;
      if (protoReq.hasOutputResource()) {
        protoOut = protoReq.getOutputResource();
      }

      Resource<?> outputResource = null;
      if (protoOut != null) {
        ResourceType rtype = fromProtoResourceType(protoOut.getType());
        outputResource = new Resource<>(rtype, protoOut.getUri());
      }

      // ---------------------------
      // Delimiter
      // ---------------------------
      Delimiter delim = Delimiter.defaultDelimiter(); // default
      if (protoReq.hasDelimiter() && !protoReq.getDelimiter().isEmpty()) {
        String delimStr = protoReq.getDelimiter();
        if (delimStr.equals(Delimiter.COMMA.getValue())) {
          delim = Delimiter.COMMA;
        } else if (delimStr.equals(Delimiter.SEMICOLON.getValue())) {
          delim = Delimiter.SEMICOLON;
        } else if (delimStr.equals(Delimiter.PIPE.getValue())) {
          delim = Delimiter.PIPE;
        } else if (delimStr.equals(Delimiter.COLON.getValue())) {
          delim = Delimiter.COLON;
        } else {
          delim = Delimiter.defaultDelimiter(); // fallback
        }
      }

      System.out.println("Input resource type: "
          + (inputResource != null ? inputResource.getType() : "null"));
      System.out.println("Input URI: "
          + (inputResource != null ? inputResource.getUri() : "null"));
      System.out.println("Output resource type: "
          + (outputResource != null ? outputResource.getType() : "null"));
      System.out.println("Output URI: "
          + (outputResource != null ? outputResource.getUri() : "null"));
      System.out.println("Delimiter: " + delim.getValue());

      ComputationRequest domainReq = new ComputationRequest(inputResource,
          outputResource, delim);

      // ---------------------------
      // Call NetworkAPI
      // ---------------------------
      ComputationResponse domainResp = networkAPI.compute(domainReq);

      // ---------------------------
      // Build proto response
      // ---------------------------
      NetworkProto.ComputationResponse.Builder respBuilder = NetworkProto.ComputationResponse
          .newBuilder();

      // Status
      ApiStatus status = domainResp.getStatus();
      if (status != null) {
        try {
          respBuilder.setStatus(NetworkProto.ApiStatus.valueOf(status.name()));
        } catch (IllegalArgumentException e) {
          respBuilder.setStatus(NetworkProto.ApiStatus.UNKNOWN);
        }
      } else {
        respBuilder.setStatus(NetworkProto.ApiStatus.UNKNOWN);
      }

      // Results
      List<Integer> results = domainResp.getResults();
      if (results != null) {
        respBuilder.addAllResults(results);
      }

      // Include actual ProcessAPI error message if present
      if (domainResp.getMessage() != null
          && !domainResp.getMessage().isEmpty()) {
        respBuilder.setMessage(domainResp.getMessage());
      } else {
        respBuilder.setMessage("");
      }

      System.out.println("Computation completed. Status: "
          + respBuilder.getStatus() + ", message: " + respBuilder.getMessage());

      responseObserver.onNext(respBuilder.build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      System.err.println("Exception in Compute: " + e.getMessage());
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  private ResourceType fromProtoResourceType(
      NetworkProto.ResourceType protoType) {
    switch (protoType) {
      case FILE :
        return ResourceType.FILE;
      case CUSTOM :
        return ResourceType.CUSTOM;
      case DATABASE :
        return ResourceType.DATABASE;
      case STREAM :
        return ResourceType.STREAM;
      default :
        return ResourceType.FILE;
    }
  }
}
