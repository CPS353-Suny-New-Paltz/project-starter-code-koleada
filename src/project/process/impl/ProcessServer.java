package project.process.impl;

import java.io.IOException;
import java.util.Scanner;

import api.implementations.ProcessAPI;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import process.api.ProcessApi;

public class ProcessServer {

  public static void main(String[] args)
      throws IOException, InterruptedException {
    int port = 50052; // port for Process API
    ProcessApi processApi = new ProcessAPI();

    Server server = ServerBuilder.forPort(port)
        .addService(new ProcessServiceImpl(processApi)).build();

    Thread serverThread = new Thread(() -> {
      try {
        server.start();
        System.out
            .println("[ProcessServer] gRPC server started on port " + port);
        server.awaitTermination();
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    });
    serverThread.start();

    // same console shutdown idea, that only kinda works at least in eclipse
    Scanner scanner = new Scanner(System.in);
    System.out.println("[ProcessServer] Type 'exit' to stop the server.");
    while (true) {
      String input = scanner.nextLine();
      if ("exit".equalsIgnoreCase(input.trim())) {
        System.out.println("[ProcessServer] Shutting down...");
        server.shutdown();
        break;
      }
    }
    scanner.close();

    server.awaitTermination();
    System.out.println("[ProcessServer] Server stopped.");
  }
}
