package tests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import api.implementations.ConceptualAPI;
import api.implementations.NetworkAPI;
import api.implementations.ProcessAPI;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
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
  public void setup() throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Use real APIs instead of mocks
    networkApi = new NetworkAPI();

    processApi = Mockito.mock(ProcessAPI.class);
    conceptualApi = Mockito.mock(ConceptualAPI.class);

    networkApi.setCompute(conceptualApi);
    networkApi.setReadWrite(processApi);

    Resource resource = new Resource(ResourceType.DATABASE, "db://mydb");
    networkApi.setResource(resource);

    String dbPath = new File("auth.db").getAbsolutePath();
    System.setProperty("sqlite.db.path", dbPath);

    String adminUser = "admin";
    String adminPass = "admin";
    String hashedPass = shared.stuff.InitDatabase.hashPassword(adminPass);

    network.api.LoginRequest loginReq = new network.api.LoginRequest(adminUser,
        hashedPass);
    network.api.LoginResponse loginResp = networkApi.login(loginReq);

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
    assertNull(resp.getUserId());
    assertNull(resp.getSessionToken());
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
  public void testSetReadWrite() {
    assertEquals(processApi, networkApi.getReadWrite());
  }

  @Test
  public void testSetCompute() {
    assertEquals(conceptualApi, networkApi.getCompute());
  }

}
