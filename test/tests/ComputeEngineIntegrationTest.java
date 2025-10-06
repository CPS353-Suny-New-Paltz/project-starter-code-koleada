package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import process.api.LoadResponse;
import process.api.LoadRequest;
import process.api.StoreRequest;
import process.api.StoreResponse;

import api.implementations.ConceptualAPI;
import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import conceptual.api.ConceptualApi;
import network.api.Delimiter;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class ComputeEngineIntegrationTest {

  TestInputConfig inputConfig;
  TestOutputConfig outputConfig;
  InMemoryDataStore dataStore;

  @BeforeEach
  void setUp() {
    // Initialize input with [1, 10, 25]
    inputConfig = new TestInputConfig(Arrays.asList(1, 10, 25));
    outputConfig = new TestOutputConfig();
    dataStore = new InMemoryDataStore(inputConfig, outputConfig);

    NetworkAPI net = new NetworkAPI();
    ConceptualApi con = new ConceptualAPI();
    ProcessAPI proc = new ProcessAPI(null);
  }

  @Test
  void testComputeEngineIntegration() {

    // Simulate loading data
    LoadRequest loadReq = new LoadRequest(
        dataStore.resource, Delimiter.defaultDelimiter()); 
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


    //
    // Simulate storing processed data

    // create new resource using outputConfig
    Resource<TestOutputConfig> outputSource = new Resource(ResourceType.CUSTOM,
        dataStore.outputConfig);

    StoreRequest storeReq = new StoreRequest(
        outputSource, loadResp.getPayload(), Delimiter.defaultDelimiter()); 

    StoreResponse storeResp = dataStore.storeData(storeReq);


    // Validate API status
    assertEquals(ApiStatus.SUCCESS, storeResp.getStatus());


  }
}
