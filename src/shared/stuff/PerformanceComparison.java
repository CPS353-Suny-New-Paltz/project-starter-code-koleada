package shared.stuff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import api.implementations.ConceptualAPI;
import api.implementations.MultithreadedNetworkAPI;
import api.implementations.ProcessAPI;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;

public class PerformanceComparison {

  public static void main(String[] args) {

    List<BigInteger> inputData = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      inputData.add(BigInteger.valueOf(i));
    }

    Resource inputResource = new Resource(ResourceType.CUSTOM, inputData);
    Resource outputResource = new Resource(ResourceType.CUSTOM,
        new ArrayList<>());

    ComputationRequest request = new ComputationRequest(inputResource,
        outputResource, Delimiter.defaultDelimiter());

    MultithreadedNetworkAPI coordinator = new MultithreadedNetworkAPI();
    coordinator.setCompute(new ConceptualAPI());
    coordinator.setReadWrite(new ProcessAPI());

    int runs = 5;

    // original
    long[] originalTimes = new long[runs];
    TimingLogger.reset();

    System.out.println("Running original compute...");
    for (int i = 0; i < runs; i++) {
      ComputationResponse resp = coordinator.compute(request);
      if (resp.getStatus() != ApiStatus.SUCCESS) {
        System.out.println(
            "Original compute run " + i + " failed: " + resp.getMessage());
      }
      long compTime = TimingLogger.getTotalTime("Computation Phase");
      originalTimes[i] = compTime;
      System.out.println("Run " + i + " Computation Phase time: "
          + compTime / 1_000_000.0 + " ms");
      TimingLogger.reset();
    }

    // improved
    long[] fastTimes = new long[runs];
    TimingLogger.reset();

    System.out.println("\nRunning fast compute...");
    for (int i = 0; i < runs; i++) {
      ComputationResponse resp = coordinator.computeFast(request);
      if (resp.getStatus() != ApiStatus.SUCCESS) {
        System.out
            .println("Fast compute run " + i + " failed: " + resp.getMessage());
      }
      long compTime = TimingLogger.getTotalTime("Computation Phase");
      fastTimes[i] = compTime;
      System.out.println("Run " + i + " Computation Phase time: "
          + compTime / 1_000_000.0 + " ms");
      TimingLogger.reset();
    }

    // calculate + display time differences
    double originalAvg = 0;
    double fastAvg = 0;
    for (int i = 0; i < runs; i++) {
      originalAvg += originalTimes[i];
      fastAvg += fastTimes[i];
    }
    originalAvg /= runs;
    fastAvg /= runs;

    double improvement = ((originalAvg - fastAvg) / originalAvg) * 100.0;
    System.out.println("\nAverage Original Computation Phase: "
        + originalAvg / 1_000_000.0 + " ms");
    System.out.println(
        "Average Fast Computation Phase: " + fastAvg / 1_000_000.0 + " ms");
    System.out.println("Percentage improvement: " + improvement + "%");
  }
}
