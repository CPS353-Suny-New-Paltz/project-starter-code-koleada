package checkpointtests;

import java.math.BigInteger;
import java.util.List;

import api.implementations.ConceptualAPI;
import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class ManualTestingFramework {

  public static final String INPUT = "manualTestInput.txt";
  public static final String OUTPUT = "manualTestOutput.txt";

  public static void main(String[] args) {
    // Set up APIs
    ProcessAPI processApi = new ProcessAPI();
    ConceptualAPI conceptualApi = new ConceptualAPI();
    NetworkAPI networkApi = new NetworkAPI();

    networkApi.setReadWrite(processApi);
    networkApi.setCompute(conceptualApi);

    // Use FILE resources
    Resource inputRes = new Resource(ResourceType.FILE, INPUT);
    Resource outputRes = new Resource(ResourceType.FILE, OUTPUT);

    ComputationRequest request = new ComputationRequest(inputRes, outputRes,
        Delimiter.COMMA);

    ComputationResponse resp = networkApi.compute(request);

    System.out.println("Computation finished with status: " + resp.getStatus());
    System.out.println("Results:");
    List<BigInteger> results = resp.getResults();
    for (BigInteger i : results) {
      System.out.println(i);
    }
    System.out.println("Message: " + resp.getMessage());
  }
}
