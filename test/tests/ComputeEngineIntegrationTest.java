package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import network.api.Delimiter;
import network.api.LoadDataRequest;
import network.api.LoadDataResponse;
import network.api.StoreDataRequest;
import network.api.StoreDataResponse;
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
  }

  @Test
  void testComputeEngineIntegration() {

    // Simulate loading data
    LoadDataRequest loadReq = new LoadDataRequest(UUID.randomUUID().toString(),
        dataStore.resource); // no delimiter specified
    LoadDataResponse loadResp = dataStore.loadData(loadReq);

    // Verify that loaded data matches inputConfig
    String loadedString = new String(loadResp.getPayload());
    String expectedString = "1" + Delimiter.defaultDelimiter().getValue() + "10"
        + Delimiter.defaultDelimiter().getValue() + "25";

    // this should pass, just checking we can correctly read data, I'm still not
    // sure what the ConceptualAPI / computation section of compute engine will
    // even do. May have to rework entire ConceptualAPI later, I had almsot no
    // idea what its supposed to do
    assertEquals(expectedString, loadedString);

    //
    // Simulate storing processed data

    // create new resource using outputConfig
    Resource<TestOutputConfig> outputSource = new Resource(ResourceType.CUSTOM,
        dataStore.outputConfig);

    StoreDataRequest storeReq = new StoreDataRequest(
        UUID.randomUUID().toString(), outputSource, loadResp.getPayload()); // no
                                                                            // delimiter
                                                                            // specified

    StoreDataResponse storeResp = dataStore.storeData(storeReq);

    // Validate API status, fails currently
    assertEquals(ApiStatus.SUCCESS, storeResp.getStatus());

    // Validate that outputConfig has been populated correctly
    List<String> expectedOutput = Arrays.asList("computed_1", "computed_10",
        "computed_25");

    // This will fail, written output will be ran through the computation
    // section of compute engine
    assertEquals(expectedOutput, dataStore.outputConfig.getOutputData());

  }
}
