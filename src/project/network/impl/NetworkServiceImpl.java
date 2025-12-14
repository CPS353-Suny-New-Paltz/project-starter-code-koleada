package project.network.impl;

import java.math.BigInteger;
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
  public void login(NetworkProto.LoginRequest protoReq,
      StreamObserver<NetworkProto.LoginResponse> responseObserver) {

    try {
      String username = protoReq.getUsername();
      String hashedPassword = protoReq.getHashedPassword();

      // Use NetworkAPI login method
      network.api.LoginRequest loginReq = new network.api.LoginRequest(username,
          hashedPassword);
      network.api.LoginResponse loginResp = networkAPI.login(loginReq);

      NetworkProto.LoginResponse.Builder respBuilder = NetworkProto.LoginResponse
          .newBuilder();
      respBuilder.setStatus(
          NetworkProto.ApiStatus.valueOf(loginResp.getStatus().name()));
      if (loginResp.getSessionToken() != null) {
        respBuilder.setSessionToken(loginResp.getSessionToken());
      }
      if (loginResp.getUserId() != null) {
        respBuilder.setUserId(loginResp.getUserId());
      }
      if (loginResp.getMessage() != null) {
        respBuilder.setMessage(loginResp.getMessage());
      }

      responseObserver.onNext(respBuilder.build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void logout(NetworkProto.LogoutRequest protoReq,
      StreamObserver<NetworkProto.LogoutResponse> responseObserver) {

    try {
      network.api.LogoutRequest logoutReq = new network.api.LogoutRequest(
          protoReq.getSessionToken());
      network.api.LogoutResponse logoutResp = networkAPI.logout(logoutReq);

      NetworkProto.LogoutResponse.Builder respBuilder = NetworkProto.LogoutResponse
          .newBuilder();
      respBuilder.setStatus(
          NetworkProto.ApiStatus.valueOf(logoutResp.getStatus().name()));
      if (logoutResp.getMessage() != null)
        respBuilder.setMessage(logoutResp.getMessage());

      responseObserver.onNext(respBuilder.build());
      responseObserver.onCompleted();

    } catch (Exception e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void compute(NetworkProto.ComputationRequest protoReq,
      StreamObserver<NetworkProto.ComputationResponse> responseObserver) {

    try {
      // --- Input resource ---
      NetworkProto.Resource protoIn = protoReq.hasInputResource()
          ? protoReq.getInputResource()
          : null;
      Resource inputResource = null;
      if (protoIn != null) {
        ResourceType rtype = fromProtoResourceType(protoIn.getType());
        if (rtype == ResourceType.CUSTOM) {
          List<BigInteger> data = new ArrayList<>();
          for (String s : protoIn.getDataList()) {
            data.add(new BigInteger(s));
          }
          inputResource = new Resource(rtype, data);
        } else {
          inputResource = new Resource(rtype, protoIn.getUri());
        }
      }

      // --- Output resource ---
      NetworkProto.Resource protoOut = protoReq.hasOutputResource()
          ? protoReq.getOutputResource()
          : null;
      Resource outputResource = null;
      if (protoOut != null) {
        ResourceType rtype = fromProtoResourceType(protoOut.getType());
        outputResource = new Resource(rtype, protoOut.getUri());
      }

      // --- Delimiter ---
      Delimiter delim = Delimiter.defaultDelimiter();
      if (protoReq.hasDelimiter() && !protoReq.getDelimiter().isEmpty()) {
        switch (protoReq.getDelimiter().trim()) {
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
        }
      }

      // --- Build domain request ---
      ComputationRequest domainReq = new ComputationRequest(inputResource,
          outputResource, delim);

      // --- Check login before compute ---
      ComputationResponse domainResp;
      if (networkAPI.getSessionToken() == null
          || networkAPI.getLoggedInUserId() == null) {
        domainResp = new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "User must be logged in to perform compute");
      } else {
        domainResp = networkAPI.compute(domainReq);
      }

      // --- Build proto response ---
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
      List<BigInteger> results = domainResp.getResults();
      if (results != null) {
        for (BigInteger bi : results) {
          respBuilder.addResults(bi.toString());
        }
      }

      // Message
      respBuilder.setMessage(
          domainResp.getMessage() != null ? domainResp.getMessage() : "");

      responseObserver.onNext(respBuilder.build());
      responseObserver.onCompleted();

    } catch (Exception e) {
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
