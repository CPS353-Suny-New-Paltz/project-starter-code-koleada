package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ProcessAPI;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.DataBatch;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * Test suite for our current ProcessAPI implementation
 */
public class TestProcessAPI {

  private ProcessAPI<?> processApi;
  private Resource dummyResource;

  /**
   * Creates an example resource and uses it in the ProcessAPI constructor
   */
  @BeforeEach
  void setup() {
    // create dummy resource for ProcessAPI
    dummyResource = new Resource(ResourceType.DATABASE, "db://mydb");
    processApi = new ProcessAPI<>(dummyResource);
  }

  /**
   * Tests the load method by examining response values
   */
  @Test
  void testLoad() {
    // tests the ProcessAPI.load() method
    LoadRequest request = new LoadRequest(processApi.getResource());

    LoadResponse<?> response = processApi.load(request);

    assertNotNull(response);
    assertEquals(ApiStatus.ERROR, response.getStatus());
    assertEquals("Not Implemented", response.getMessage());
    assertNotNull(response.getData()); // getData() should just return our
                                       // buffer
  }

  /**
   * Tests the store method again by examining response values
   */
  @Test
  void testStore() {
    // tsets the ProcessAPI.store() method
    DataBatch<String> batch = new DataBatch<>("test data");
    StoreRequest<DataBatch<String>> request = new StoreRequest(
        processApi.getResource(), batch);

    StoreResponse response = processApi.store(request);

    assertNotNull(response);
    assertEquals(ApiStatus.ERROR, response.getStatus());
    assertEquals("Not Implemented", response.getMessage());
  }

  /**
   * Tests the ProcessAPI resource getters and setters, as well as the resource
   * class getters
   */
  @Test
  void testProcessApiResource() {

    // test ProcessAPI constructor works properly
    assertNotNull(processApi.getResource());
    assertEquals("db://mydb", dummyResource.getUri());

    // test setResource is working
    processApi
        .setResource(new Resource(ResourceType.FILE, "file://myfile.txt"));
    assertEquals(ResourceType.FILE, processApi.getResource().getType());
  }

}
