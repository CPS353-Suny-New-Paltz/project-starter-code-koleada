package project.network.impl;

import java.util.ArrayList;
import java.util.List;

import api.implementations.NetworkAPI;
import io.grpc.stub.StreamObserver;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import project.network.grpc.NetworkProto;
import project.network.grpc.NetworkServiceGrpc;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class NetworkServiceImpl
    extends
      NetworkServiceGrpc.NetworkServiceImplBase {

  private final NetworkAPI networkAPI;

  public NetworkServiceImpl() {
    this.networkAPI = new NetworkAPI();
  }

  @Override
  public void compute(NetworkProto.ComputationRequest protoReq,
      StreamObserver<NetworkProto.ComputationResponse> responseObserver) {

    try {

      // Input resource
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
      // Output resource
      NetworkProto.Resource protoOut = null;
      if (protoReq.hasOutputResource()) {
        protoOut = protoReq.getOutputResource();
      }

      Resource<?> outputResource = null;
      if (protoOut != null) {
        ResourceType rtype = fromProtoResourceType(protoOut.getType());
        outputResource = new Resource<>(rtype, protoOut.getUri());
      }

      // Delimiter
      Delimiter delim = Delimiter.defaultDelimiter(); // default
      if (protoReq.hasDelimiter() && !protoReq.getDelimiter().isEmpty()) {
        String delimStr = protoReq.getDelimiter();
        switch (delimStr.trim()) {
          case "," :
            delim = Delimiter.COMMA;
            break;
          case ";" :
            delim = Delimiter.SEMICOLON;
            break;
          case "|" :
            delim = Delimiter.PIPE;
            break;
          case ":" :
            delim = Delimiter.COLON;
            break;
          default :
            delim = Delimiter.defaultDelimiter();
            break;
        }
      }

      ComputationRequest domainReq = new ComputationRequest(inputResource,
          outputResource, delim);

      // Call normala networkAPI
      ComputationResponse domainResp = networkAPI.compute(domainReq);

      // Build proto response
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
        return ResourceType.FILE; // fallback
    }
  }
}
