package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ProcessAPI;
import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * Test suite for our current ProcessAPI implementation
 */
public class TestProcessAPI {

  private ProcessAPI processApi;
  private Resource dummyResource;

  /**
   * Creates an example resource and uses it in the ProcessAPI constructor
   */
  @BeforeEach
  void setup() {
    processApi = new ProcessAPI();
  }

  /**
   * Tests the load method by examining response values
   */
  @Test
  void testLoad() {
    // tests the ProcessAPI.load() method
    LoadRequest request = new LoadRequest(
        new Resource(ResourceType.DATABASE, "dummy"), Delimiter.COMMA);

    LoadResponse response = processApi.load(request);

    assertNotNull(response);
    assertEquals(ApiStatus.ERROR, response.getStatus());
    assertEquals("Unsupported resource type or missing URI",
        response.getMessage());
    assertNotNull(response.getPayload()); // getData() should just return our
    // buffer
  }

  /**
   * Tests the store method again by examining response values
   */
  @Test
  void testStore() {
    // tsets the ProcessAPI.store() method
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(2);
    list.add(4);

    List batch = new ArrayList<>(list);
    StoreRequest request = new StoreRequest(
        new Resource(ResourceType.DATABASE, "dummy"), batch, Delimiter.COMMA);

    StoreResponse response = processApi.store(request);

    assertNotNull(response);
    assertEquals(ApiStatus.ERROR, response.getStatus());
    assertEquals("Unsupported resource type or missing URI",
        response.getMessage());
  }

}
