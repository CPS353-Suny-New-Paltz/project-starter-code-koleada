package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.NetworkAPI;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import shared.stuff.InitDatabase;

/**
 * Fast test to ensure lgoin.logout are working as expected
 */
public class NetworkAPILoginLogoutTest {

  private NetworkAPI networkAPI;

  @BeforeEach
  void setup() {
    networkAPI = new NetworkAPI();
  }

  @Test
  void testLoginLogoutFlow() throws Exception {
    // good login
    LoginRequest loginReq = new LoginRequest("admin",
        InitDatabase.hashPassword("admin"));
    LoginResponse loginResp = networkAPI.login(loginReq);

    System.out.println(loginResp.getMessage());
    assertEquals(true, loginResp.success());

    Field userIdField = NetworkAPI.class.getDeclaredField("loggedInUserId");
    Field tokenField = NetworkAPI.class.getDeclaredField("sessionToken");
    userIdField.setAccessible(true);
    tokenField.setAccessible(true);

    String loggedInUserId = (String) userIdField.get(networkAPI);
    String sessionToken = (String) tokenField.get(networkAPI);

    assertNotNull(loggedInUserId, "loggedInUserId should be set after login");
    assertNotNull(sessionToken, "sessionToken should be set after login");

    // logout
    LogoutRequest logoutReq = new LogoutRequest(sessionToken);
    LogoutResponse logoutResp = networkAPI.logout(logoutReq);

    assertTrue(logoutResp.success(), "Logout should succeed");

    // verify fields are cleared
    loggedInUserId = (String) userIdField.get(networkAPI);
    sessionToken = (String) tokenField.get(networkAPI);

    assertNull(loggedInUserId, "loggedInUserId should be cleared after logout");
    assertNull(sessionToken, "sessionToken should be cleared after logout");

    // bad creds
    LoginRequest invalidLogin = new LoginRequest("admin",
        InitDatabase.hashPassword("wrongpass"));
    LoginResponse invalidResp = networkAPI.login(invalidLogin);
    assertFalse(invalidResp.success(),
        "Login should fail for invalid password");

    // special chars in username
    LoginRequest specialCharLogin = new LoginRequest("admin!-",
        InitDatabase.hashPassword("pa$$word"));
    LoginResponse specialResp = networkAPI.login(specialCharLogin);
    assertFalse(specialResp.success(),
        "Login should fail for username/password with special chars");
  }
}
