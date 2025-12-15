package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import network.api.ComputationRequest;
import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * This class is used to test our parameter validation and exception handling
 */
public class ValidationAndExceptionTests {

  private NetworkAPI networkAPI;
  private ProcessAPI processAPI;

  @BeforeEach
  public void setup() {
    networkAPI = new NetworkAPI();
    processAPI = new ProcessAPI();
  }

  @Test
  void testResourceValidation() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Resource(ResourceType.CUSTOM, "dummy");
    });
  }

  @Test
  void testLoadRequestValidation() {
    Resource r = new Resource(ResourceType.CUSTOM,
        List.of(BigInteger.ONE, BigInteger.TWO));
    assertThrows(IllegalArgumentException.class, () -> {
      new LoadRequest(r, null);
    });
  }

  @Test
  void testComputationRequestValidation() {
    Resource input = new Resource(ResourceType.CUSTOM,
        List.of(BigInteger.ONE, BigInteger.TWO));
    Resource output = new Resource(ResourceType.CUSTOM,
        List.of(BigInteger.valueOf(3), BigInteger.valueOf(4)));
    assertThrows(IllegalArgumentException.class, () -> {
      new ComputationRequest(input, output, null);
    });
  }

  @Test
  public void testProcessApiLoadExceptionHandling() {
    Resource fileResource = new Resource(ResourceType.FILE,
        "nonexistent-file.txt");
    LoadRequest request = new LoadRequest(fileResource, Delimiter.COMMA);
    LoadResponse response = processAPI.load(request);

    assertNotNull(response);
    assertEquals(0, response.getPayload().size());
    assertEquals("Invalid file: File nonexistent-file.txt does not exist",
        response.getMessage());
  }
}
