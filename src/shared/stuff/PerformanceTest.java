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

/**
 * Simple class that runs compute so we can gather performance data, used to
 * locate performance issues
 */
public class PerformanceTest {

  public static void main(String[] args) {
    // Prepare fake resources with small test data
    List<BigInteger> inputData = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
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

    // Run multiple times to gather averages
    for (int i = 0; i < 5; i++) {
      ComputationResponse resp = coordinator.compute(request);
      if (resp.getStatus() != ApiStatus.SUCCESS) {
        System.out.println("Run " + i + " failed: " + resp.getMessage());
      }
    }

    // Print out all timing averages
    TimingLogger.printStats();
  }
}