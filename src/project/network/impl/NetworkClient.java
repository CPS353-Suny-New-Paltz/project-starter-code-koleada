package project.network.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import project.network.grpc.NetworkProto;
import project.network.grpc.NetworkServiceGrpc;

public class NetworkClient {

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    // allow user to specify host and port
    System.out.print("Enter server host (default localhost): ");
    String host = scanner.nextLine();
    if (host.isEmpty()) {
      host = "localhost";
    }
    System.out.print("Enter server port (default 50051): ");
    String portStr = scanner.nextLine();
    int port = portStr.isEmpty() ? 50051 : Integer.parseInt(portStr);

    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext().build();

    NetworkServiceGrpc.NetworkServiceBlockingStub stub = NetworkServiceGrpc
        .newBlockingStub(channel);

    // Ask user if input is from file or typed numbers
    System.out.print("Input type? (1 = file, 2 = numbers): ");
    String choice = scanner.nextLine().trim();

    String inputUri = null;
    List<Integer> numbers = new ArrayList<>();

    if ("1".equals(choice)) {
      System.out.print("Enter input file explicit path: ");
      inputUri = scanner.nextLine();
    } else {
      System.out.print("Enter numbers separated by spaces: ");
      String line = scanner.nextLine();
      for (String s : line.trim().split("\\s+")) {
        if (!s.isEmpty()) {
          numbers.add(Integer.parseInt(s));
        }
      }
    }

    // MUST be explicit path!!
    System.out.print("Enter output file explicit path: ");
    String outputFile = scanner.nextLine();

    System.out.print("Enter delimiter (optional, default ','): ");
    String delim = scanner.nextLine();
    if (delim.isEmpty()) {
      delim = ",";
    }
    NetworkProto.Resource inputResource;
    if ("1".equals(choice)) {
      inputResource = NetworkProto.Resource.newBuilder()
          .setType(NetworkProto.ResourceType.FILE).setUri(inputUri).build();
    } else {
      NetworkProto.Resource.Builder inBuilder = NetworkProto.Resource
          .newBuilder();
      inBuilder.setType(NetworkProto.ResourceType.CUSTOM);
      for (int n : numbers) {
        inBuilder.addData(n);
      }
      inputResource = inBuilder.build();
    }

    NetworkProto.Resource outputResource = NetworkProto.Resource.newBuilder()
        .setType(NetworkProto.ResourceType.FILE) // must be FILE for ProcessAPI
                                                 // to write
        .setUri(outputFile).build();

    NetworkProto.ComputationRequest request = NetworkProto.ComputationRequest
        .newBuilder().setInputResource(inputResource)
        .setOutputResource(outputResource).setDelimiter(delim).build();

    System.out.println("Sending compute request...");
    NetworkProto.ComputationResponse response = stub.compute(request);

    System.out.println("Computation finished:");
    System.out.println("Status: " + response.getStatus());
    System.out.println("Message: " + response.getMessage());
    System.out.println("Results: " + response.getResultsList());

    channel.shutdown();
    scanner.close();
  }
}
