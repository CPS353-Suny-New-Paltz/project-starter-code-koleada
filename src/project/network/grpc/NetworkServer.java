package project.network.grpc;

import java.io.IOException;
import java.util.Scanner;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class NetworkServer {

  public static void main(String[] args)
      throws IOException, InterruptedException {
    int port = 50051;
    Server server = ServerBuilder.forPort(port)
        .addService(new NetworkServiceImpl()).build();

    Thread serverThread = new Thread(() -> {
      try {
        server.start();
        System.out
            .println("[NetworkServer] gRPC server started on port " + port);
        server.awaitTermination();
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    });
    serverThread.start();

    // Allow user to type "exit" to stop server
    Scanner scanner = new Scanner(System.in);
    System.out.println("[NetworkServer] Type 'exit' to stop the server.");
    while (true) {
      String input = scanner.nextLine();
      if ("exit".equalsIgnoreCase(input.trim())) {
        System.out.println("[NetworkServer] Shutting down...");
        server.shutdown();
        break;
      }
    }
    scanner.close();
    server.awaitTermination(); // optional: wait until shutdown completes
    System.out.println("[NetworkServer] Server stopped.");
  }
}