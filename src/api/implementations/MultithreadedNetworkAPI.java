package api.implementations;

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

/**
 * Multi-threaded implementation of NetworkApi.
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

  // my CPU has 6 physical cores and 12 threads so 8 seemed to be a good level
  // to maxamize efficiency without overwhelming. Also accommodated clients with
  // less processing power too
  private static final int MAX_THREADS = Math.min(8,
      Runtime.getRuntime().availableProcessors());

  // Dedicated executor for this instance (shared across compute calls)
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
      // null req
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // Unexpected exceptions
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
      // catch null req
      return new LogoutResponse(ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // catch all exceptions we dont expect
      return new LogoutResponse(ApiStatus.ERROR, "Error: " + e.getMessage());
    }

  }

  /**
   * does the computation: read input, run compute, write output, return results
   * as Arraylist<Integer>
   * 
   * multi-threaded differences: - makes Callable task for each input value
   * (each calls compute.performComputation(value).getResult()) - waits using
   * Future.get() and gets results in order
   */

  /**
   * 
   */
  @Override
  public ComputationResponse compute(ComputationRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }

    try {
      // same as normal
      LoadResponse loadResp = readWrite.load(
          new LoadRequest(request.getInputResource(), request.getDelimiter()));
      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to load input data");
      }

      List<Integer> inputs = loadResp.getPayload();
      List<Integer> results = new ArrayList<>();

      // submit Callables to executor (1 per input)
      List<Future<Integer>> futures = new ArrayList<>(inputs.size());
      for (int value : inputs) {
        Callable<Integer> task = () -> {
          // performComputation should be fully thread safe
          return compute.performComputation(value).getResult();
        };
        futures.add(executor.submit(task));
      }

      // grab results in order
      for (Future<Integer> f : futures) {
        try {
          Integer r = f.get(); // wait until task is done
          results.add(r);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt(); // allow exception to be caught in
                                              // higher level code
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Computation interrupted");
        } catch (ExecutionException e) {
          // job threw error, whole computation fails
          return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
              "Computation failed in worker thread: " + e.getCause());
        }
      }

      // same as normal
      StoreResponse storeResp = readWrite.store(new StoreRequest(
          request.getOutputResource(), results, request.getDelimiter()));

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            "Failed to store results");
      }

      return new ComputationResponse(ApiStatus.SUCCESS, results,
          "Computation completed (multi-threaded)");

    } catch (IllegalArgumentException e) {
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Error: " + e.getMessage());
    }
  }

  /**
   * needed for smoke test
   */
  public List<String> processRequests(List<String> requests) {
    return new ArrayList<>(requests);
  }

  /**
   * also needed for tests, ensures everything is shut down - no threads left
   * running this should be better than just doing .shutdown()
   */
  public void shutdown() {
    executor.shutdown(); // no new tasks
    try {
      // wait 5s to finish up, if not done inpterupt
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      executor.shutdownNow(); // shut down immedaitely
    }
  }

  // same as normal
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