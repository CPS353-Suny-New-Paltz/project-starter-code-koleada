package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import api.implementations.MultithreadedNetworkAPI;
import api.implementations.ProcessAPI;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;
import shared.stuff.TimingLogger;

public class PerformanceTest {

  @Test
  public void testComputeVsComputeFast() {
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

    TimingLogger.reset();
    int runs = 5;
    for (int i = 0; i < runs; i++) {
      ComputationResponse resp = coordinator.compute(request);
      if (resp.getStatus() != ApiStatus.SUCCESS) {
        System.out.println(
            "Original compute run " + i + " failed: " + resp.getMessage());
      }
    }
    long originalCompPhaseTotal = TimingLogger
        .getTotalTime("Computation Phase");

    TimingLogger.reset();
    for (int i = 0; i < runs; i++) {
      ComputationResponse resp = coordinator.computeFast(request);
      if (resp.getStatus() != ApiStatus.SUCCESS) {
        System.out
            .println("Fast compute run " + i + " failed: " + resp.getMessage());
      }
    }
    long fastCompPhaseTotal = TimingLogger.getTotalTime("Computation Phase");

    double improvement = ((double) (originalCompPhaseTotal - fastCompPhaseTotal)
        / originalCompPhaseTotal) * 100.0;
    System.out.println("Computation Phase improvement: " + improvement + "%");

    assertTrue(improvement >= 10.0,
        "Fast compute should be at least 10% faster than original compute");
  }
}
