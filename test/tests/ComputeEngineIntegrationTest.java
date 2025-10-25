package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import api.implementations.MultithreadedNetworkAPI;
import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class ComputeEngineIntegrationTest {

  TestInputConfig inputConfig;
  TestOutputConfig outputConfig;
  InMemoryDataStore dataStore;

  NetworkAPI net = new NetworkAPI();
  ConceptualAPI con = new ConceptualAPI();
  ProcessAPI proc = new ProcessAPI();

  @SuppressWarnings("unused")
  private final MultithreadedNetworkAPI spoonSatisfier = new MultithreadedNetworkAPI();

  @BeforeEach
  void setUp() {
    // Initialize input with [1, 10, 25]
    inputConfig = new TestInputConfig(Arrays.asList(1, 10, 25));
    outputConfig = new TestOutputConfig();
    dataStore = new InMemoryDataStore(inputConfig, outputConfig);

  }

  @Test
  void testComputeEngineIntegration() {

    net.setReadWrite(proc);
    net.setCompute(con);

    // Simulate loading data
    LoadRequest loadReq = new LoadRequest(dataStore.resource,
        Delimiter.defaultDelimiter());
    LoadResponse loadResp = dataStore.loadData(loadReq);

    // Verify that loaded data matches inputConfig

    List loadedData = loadResp.getPayload();
    String result = (String) loadedData.get(0);

    String expectedString = "1" + Delimiter.defaultDelimiter().getValue() + "10"
        + Delimiter.defaultDelimiter().getValue() + "25";

    // this should pass, just checking we can correctly read data, I'm still not
    // sure what the ConceptualAPI / computation section of compute engine will
    // even do. May have to rework entire ConceptualAPI later, I had almsot no
    // idea what its supposed to do

    assertEquals(expectedString, result);

    // Simulate storing processed data

    // create new resource using outputConfig
    Resource<List<String>> outputSource = new Resource(ResourceType.CUSTOM,
        dataStore.outputConfig.getOutputData());

    ComputationRequest compReq = new ComputationRequest(
        new Resource(ResourceType.CUSTOM, inputConfig.inputData), outputSource,
        Delimiter.defaultDelimiter());
    ComputationResponse compResp = net.compute(compReq);

    // check the the result of running the computation on input 1 matches the
    // expected result of running the computation on 1
    assertEquals(compResp.getResults().get(0), 508141714);

  }
}
