package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
  public void setUp() throws Exception {
    inputConfig = new TestInputConfig(
        Arrays.asList(BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(25)));
    outputConfig = new TestOutputConfig();
    dataStore = new InMemoryDataStore(inputConfig, outputConfig);

    net = new NetworkAPI(); // recreate fresh
    net.setReadWrite(dataStore);
    net.setCompute(con);

    // login here for consistency

    String dbPath = new File("auth.db").getAbsolutePath();
    System.setProperty("sqlite.db.path", dbPath);

    String hashedPass = shared.stuff.InitDatabase.hashPassword("admin");
    network.api.LoginRequest loginReq = new network.api.LoginRequest("admin",
        hashedPass);
    network.api.LoginResponse loginResp = net.login(loginReq);
    System.out.println(new File("auth.db").getAbsolutePath());
    System.out.println("Logged in? " + (net.getSessionToken() != null));

    if (loginResp.getStatus() != shared.stuff.ApiStatus.SUCCESS) {
      throw new IllegalStateException(
          "Failed to login: " + loginResp.getMessage());
    }
  }

  @Test
  void testComputeEngineIntegration() {

    // Create LoadRequest with the resource
    LoadRequest loadReq = new LoadRequest(dataStore.resource,
        Delimiter.defaultDelimiter());

    // Load the data
    List<BigInteger> payload = dataStore.loadData(loadReq).getPayload();

    // Convert to single string joined by delimiter for assertion
    String result = payload.stream().map(BigInteger::toString)
        .collect(Collectors.joining(Delimiter.defaultDelimiter().getValue()));

    String expectedString = "1,10,25";
    System.out.println(result);
    assertEquals(expectedString, result);

    // Simulate computation
    Resource outputResource = new Resource(ResourceType.CUSTOM, payload);
    ComputationRequest compReq = new ComputationRequest(
        new Resource(ResourceType.CUSTOM, inputConfig.inputData),
        outputResource, Delimiter.defaultDelimiter());

    ComputationResponse compResp = net.compute(compReq);

    // Validate computation result for first input
    assertEquals(1303319248, compResp.getResults().get(0).intValue());
  }
}
