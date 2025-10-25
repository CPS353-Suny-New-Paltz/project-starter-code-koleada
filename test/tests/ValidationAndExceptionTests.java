package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * This class is used to test our paramater validation and exception handling
 */
public class ValidationAndExceptionTests {

  private NetworkAPI networkAPI;
  private ProcessAPI processAPI;

  @BeforeEach
  public void setup() {
    networkAPI = new NetworkAPI();
    processAPI = new ProcessAPI();
  }

  /**
   * I think it is most benefical to test the param validation within the actual
   * helper classes themselves. In the APIs we ensure requests are not null and
   * then throw exceptions if they are, which aligns more with the integration
   * tests. Ih the classes we have some more unique validation
   */

  // resource test
  @Test
  void testResourceValidation() {
    assertThrows(IllegalArgumentException.class, () -> {
      // cannot create custom resource without a list
      new Resource<>(ResourceType.CUSTOM, "dummy");
    });
  }

  // load request test
  @Test
  void testLoadRequestValidation() {
    Resource<Integer> r = new Resource<>(ResourceType.CUSTOM, List.of(1, 2));
    assertThrows(IllegalArgumentException.class, () -> {
      new LoadRequest(r, null); // Delimiter is null
    });
  }

  // computationRequest test
  @Test
  void testComputationRequestValidation() {
    Resource<Integer> input = new Resource<>(ResourceType.CUSTOM,
        List.of(1, 2));
    Resource<Integer> output = new Resource<>(ResourceType.CUSTOM,
        List.of(3, 4));
    assertThrows(IllegalArgumentException.class, () -> {
      new ComputationRequest(input, output, null); // Delimiter is null
    });
  }

  // Integration test for exception hanlding
  @Test
  public void testProcessApiLoadExceptionHandling() {
    // create a FILE resource pointing to a non-existent path
    Resource<?> fileResource = new Resource(ResourceType.FILE,
        "nonexistent-file.txt");

    LoadRequest request = new LoadRequest(fileResource, Delimiter.COMMA);
    LoadResponse response = processAPI.load(request);

    // Response should not be null
    assertNotNull(response);

    // list should be empty on error
    assertEquals(0, response.getPayload().size());

    // message should indicate invalid file error
    assertEquals("Invalid file: File nonexistent-file.txt does not exist",
        response.getMessage());
  }
}
