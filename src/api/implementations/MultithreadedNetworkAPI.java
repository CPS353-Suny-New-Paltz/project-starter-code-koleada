package api.implementations;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import network.api.NetworkApi;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.TimingLogger;

/**
 * Multi-threaded implementation of NetworkApi using BigInteger.
 *
 * - Uses a fixed thread pool with MAX_THREADS threads. - Per-value computation
 * (compute.performComputation) is submitted as callables. - Preserves input
 * order: results collected in same order as submitted. - Handles exceptions
 * from worker threads via Future.get().
 */
public class MultithreadedNetworkAPI implements NetworkApi {

  private ProcessAPI readWrite;
  private ConceptualAPI compute;
  private Delimiter defaultDelimiter = Delimiter.COMMA;
  private Resource resource;

  private static final int MAX_THREADS = Math.min(8,
      Runtime.getRuntime().availableProcessors());

  private final ExecutorService executor = Executors
      .newFixedThreadPool(MAX_THREADS);

  @Override
  public LoginResponse login(LoginRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("req cannot be null");
      }
      return new LoginResponse(UUID.randomUUID().toString(),
          UUID.randomUUID().toString(), ApiStatus.ERROR);

    } catch (IllegalArgumentException e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Error: " + e.getMessage());
    }
  }

  @Override
  public LogoutResponse logout(LogoutRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }
      return new LogoutResponse(ApiStatus.ERROR);

    } catch (IllegalArgumentException e) {
      return new LogoutResponse(ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return new LogoutResponse(ApiStatus.ERROR, "Error: " + e.getMessage());
    }
  }

  /**
   * Fast, performance-improved version of compute using BigInteger
   */
  public ComputationResponse computeFast(ComputationRequest request) {

    long totalStart = TimingLogger.startSection("Total Compute Time");

    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }

    try {
      long loadStart = TimingLogger.startSection("Load Phase");
      LoadResponse loadResp = readWrite.load(
          new LoadRequest(request.getInputResource(), request.getDelimiter()));
      TimingLogger.endSection("Load Phase", loadStart);

      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to load input data");
      }

      List<BigInteger> inputs = loadResp.getPayload();
      List<BigInteger> results = new ArrayList<>();

      long taskSubmitStart = TimingLogger.startSection("Task Submission");
      List<Future<BigInteger>> futures = new ArrayList<>(inputs.size());

      for (BigInteger value : inputs) {
        Callable<BigInteger> task = () -> {
          long compStart = TimingLogger.startSection("Computation Phase");
          BigInteger result = compute.performComputationFast(value).getResult();
          TimingLogger.endSection("Computation Phase", compStart);
          return result;
        };
        futures.add(executor.submit(task));
      }
      TimingLogger.endSection("Task Submission", taskSubmitStart);

      long collectStart = TimingLogger.startSection("Result Collection");
      for (Future<BigInteger> f : futures) {
        try {
          results.add(f.get());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Computation interrupted");
        } catch (ExecutionException e) {
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Worker thread failed: " + e.getCause());
        }
      }
      TimingLogger.endSection("Result Collection", collectStart);

      long storeStart = TimingLogger.startSection("Store Phase");
      StoreResponse storeResp = readWrite.store(new StoreRequest(
          request.getOutputResource(), results, request.getDelimiter()));
      TimingLogger.endSection("Store Phase", storeStart);

      TimingLogger.endSection("Total Compute Time", totalStart);

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to store results");
      }

      return new ComputationResponse(ApiStatus.SUCCESS, results,
          "Computation completed");

    } catch (Exception e) {
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Error: " + e.getMessage());
    }
  }

  /**
   * Standard compute method using BigInteger
   */
  public ComputationResponse compute(ComputationRequest request) {

    long totalStart = TimingLogger.startSection("Total Compute Time");

    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }

    try {
      long loadStart = TimingLogger.startSection("Load Phase");
      LoadResponse loadResp = readWrite.load(
          new LoadRequest(request.getInputResource(), request.getDelimiter()));
      TimingLogger.endSection("Load Phase", loadStart);

      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to load input data");
      }

      List<BigInteger> inputs = loadResp.getPayload();
      List<BigInteger> results = new ArrayList<>();

      long taskSubmitStart = TimingLogger.startSection("Task Submission");
      List<Future<BigInteger>> futures = new ArrayList<>(inputs.size());

      for (BigInteger value : inputs) {
        Callable<BigInteger> task = () -> {
          long compStart = TimingLogger.startSection("Computation Phase");
          BigInteger result = compute.performComputation(value).getResult();
          TimingLogger.endSection("Computation Phase", compStart);
          return result;
        };
        futures.add(executor.submit(task));
      }
      TimingLogger.endSection("Task Submission", taskSubmitStart);

      long collectStart = TimingLogger.startSection("Result Collection");
      for (Future<BigInteger> f : futures) {
        try {
          results.add(f.get());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Computation interrupted");
        } catch (ExecutionException e) {
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Worker thread failed: " + e.getCause());
        }
      }
      TimingLogger.endSection("Result Collection", collectStart);

      long storeStart = TimingLogger.startSection("Store Phase");
      StoreResponse storeResp = readWrite.store(new StoreRequest(
          request.getOutputResource(), results, request.getDelimiter()));
      TimingLogger.endSection("Store Phase", storeStart);

      TimingLogger.endSection("Total Compute Time", totalStart);

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to store results");
      }

      return new ComputationResponse(ApiStatus.SUCCESS, results,
          "Computation completed");

    } catch (Exception e) {
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Error: " + e.getMessage());
    }
  }

  public List<String> processRequests(List<String> requests) {
    return new ArrayList<>(requests);
  }

  public void shutdown() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      executor.shutdownNow();
    }
  }

  public ProcessAPI getReadWrite() {
    return readWrite;
  }

  public void setReadWrite(ProcessAPI readWrite) {
    this.readWrite = readWrite;
  }

  public ConceptualAPI getCompute() {
    return compute;
  }

  public void setCompute(ConceptualAPI compute) {
    this.compute = compute;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }
}
