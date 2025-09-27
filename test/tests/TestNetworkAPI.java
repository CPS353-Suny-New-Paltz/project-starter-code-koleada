package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.ConceptualAPI;
import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import network.api.LoadDataRequest;
import network.api.LoadDataResponse;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import network.api.StoreDataRequest;
import network.api.StoreDataResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * Test suite for the NetworkAPI
 */
public class TestNetworkAPI {

  private NetworkAPI networkApi;
  private ProcessAPI processApi;
  private ConceptualAPI conceptualApi;

  @BeforeEach
  public void setup() {
    // Use real APIs instead of mocks
    networkApi = new NetworkAPI();

    conceptualApi = new ConceptualAPI();

    networkApi.setCompute(conceptualApi);

    Resource resource = new Resource(ResourceType.DATABASE, "db://mydb");
    networkApi.setResource(resource);
    processApi = new ProcessAPI(resource);
    networkApi.setReadWrite(processApi);
  }

  @Test
  public void testDependency() {
    assertSame(processApi, networkApi.getReadWrite());
    assertSame(conceptualApi, networkApi.getCompute());
  }

  @Test
  public void testLogin() {
    LoginRequest req = new LoginRequest("testUser", "hashedPassword");
    LoginResponse resp = networkApi.login(req);

    assertNotNull(resp);
    assertNotNull(resp.getUserId());
    assertNotNull(resp.getSessionToken());
    assertEquals(ApiStatus.ERROR, resp.getStatus()); // not implemented yet
  }

  @Test
  public void testLogout() {
    LogoutRequest req = new LogoutRequest(UUID.randomUUID().toString());
    LogoutResponse resp = networkApi.logout(req);

    assertNotNull(resp);
    assertEquals(false, resp.success());
    assertEquals(ApiStatus.ERROR, resp.getStatus());
  }

  @Test
  public void testStoreData() {
    StoreDataRequest req = new StoreDataRequest(UUID.randomUUID().toString(),
        networkApi.getResource(), new ArrayList<>());

    assertEquals(ResourceType.DATABASE, req.getDestination().getType());

    StoreDataResponse resp = networkApi.storeData(req);

    assertNotNull(resp);
    assertEquals(resp.getLocation().getUri(), req.getDestination().getUri());
    assertEquals(ApiStatus.ERROR, resp.getStatus());
  }

  @Test
  public void testLoadData() {
    LoadDataRequest req = new LoadDataRequest(UUID.randomUUID().toString(),
        networkApi.getResource());

    LoadDataResponse resp = networkApi.loadData(req);

    assertNotNull(resp);
    assertEquals(false, resp.success());
    assertEquals(ApiStatus.ERROR, resp.getStatus());
  }

}
