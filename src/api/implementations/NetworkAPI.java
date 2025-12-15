package api.implementations;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import network.api.LoginRequest;
import network.api.LoginResponse;
import network.api.LogoutRequest;
import network.api.LogoutResponse;
import network.api.NetworkApi;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.InitDatabase;
import shared.stuff.Resource;

/**
 * 
 * Implementation of the NetworkApi interface
 */
public class NetworkAPI implements NetworkApi {
  private ProcessApi readWrite;

  private ConceptualAPI compute;

  // Tracks currently logged-in user info
  private String loggedInUserId;
  private String sessionToken;

  // DB connection info
  String path = System.getProperty("sqlite.db.path", "auth.db");

  // Only letters & numbers allowed for usernames/passwords
  private static final Pattern VALID_INPUT = Pattern.compile("^[a-zA-Z0-9]+$");

  public NetworkAPI() {
    // will need to communicate with the ProcessAPI to pass instructions to the
    // Data Storage System

    this.readWrite = new ProcessApiGrpcClient("localhost", 50052); // GRPC
                                                                   // process
                                                                   // client,
                                                                   // implements
                                                                   // ProcessApi

    // Will also need to talk to the computation section to perform
    // calculations,
    // get session keys, etc
    this.compute = new ConceptualAPI();
  }

  // default delimiter if user does not provide one
  private Delimiter defaultDelimiter = Delimiter.COMMA;

  private Resource resource;

  private boolean isValidInput(String input) {
    return input != null && VALID_INPUT.matcher(input).matches();
  }

  @Override
  public LoginResponse login(LoginRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }

      String username = req.getUsername();
      String passwordHash = req.getHashedPassword(); // client pre-hashed

      // only allow letters and numbers in username to prevent SQLi
      // password is fine because it is hashed
      if (!username.matches("^[a-zA-Z0-9]+$")) {
        return new LoginResponse(null, null, ApiStatus.ERROR,
            "Username must be letters and numbers only");
      }

      try (Connection conn = DriverManager
          .getConnection("jdbc:sqlite:" + path)) {
        String sql = "SELECT id, password_hash FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setString(1, username);
          ResultSet rs = stmt.executeQuery();

          if (!rs.next()) {
            return new LoginResponse(null, null, ApiStatus.ERROR,
                "Invalid username or password");
          }

          // check if hashed password is equal to the provided hashed password
          // requiring the user to hash their password prevents transmission of
          // clear text passwords
          String storedHash = rs.getString("password_hash");
          if (!storedHash.equals(passwordHash)) {
            return new LoginResponse(null, null, ApiStatus.ERROR,
                "Invalid username or password");
          }

          // login successful, update fields
          loggedInUserId = rs.getString("id");
          sessionToken = UUID.randomUUID().toString();

          return new LoginResponse(sessionToken, loggedInUserId,
              ApiStatus.SUCCESS, "Login successful");
        }
      }

    } catch (IllegalArgumentException e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (SQLException e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Database error: " + e.getMessage());
    } catch (Exception e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Error: " + e.getMessage());
    }
  }

  /**
   * Create a new user in the database. Only works if the caller is logged in.
   * 
   * We use loginRequest and response to do this as it fits well.
   */
  public LoginResponse createUser(String newUsername, String newPassword) {
    try {
      // ensuree caller is logged in, only want active users to be able to add
      // new users
      if (loggedInUserId == null || sessionToken == null) {
        return new LoginResponse(null, null, ApiStatus.ERROR,
            "Must be logged in to create a new user");
      }

      // ensure good username
      if (!newUsername.matches("^[a-zA-Z0-9]+$")) {
        return new LoginResponse(null, null, ApiStatus.ERROR,
            "Username must be letters and numbers only");
      }

      // Hash the password using the same hashing function
      String passwordHash = InitDatabase.hashPassword(newPassword);

      // add insert statement
      try (Connection conn = DriverManager
          .getConnection("jdbc:sqlite:" + path)) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setString(1, newUsername);
          stmt.setString(2, passwordHash);
          stmt.executeUpdate();
        }
      }

      return new LoginResponse(null, null, ApiStatus.SUCCESS,
          "User created successfully");

    } catch (IllegalArgumentException e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Invalid input: " + e.getMessage());
    } catch (SQLException e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Database error: " + e.getMessage());
    } catch (Exception e) {
      return new LoginResponse(null, null, ApiStatus.ERROR,
          "Error: " + e.getMessage());
    }
  }

  @Override
  public LogoutResponse logout(LogoutRequest req) {
    try {
      if (req == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }

      if (sessionToken == null || !sessionToken.equals(req.getSessionToken())) {
        return new LogoutResponse(ApiStatus.ERROR, "Invalid session token");
      }

      // clear user session
      loggedInUserId = null;
      sessionToken = null;

      return new LogoutResponse(ApiStatus.SUCCESS, "Logout successful");

    } catch (IllegalArgumentException e) {
      return new LogoutResponse(ApiStatus.ERROR,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return new LogoutResponse(ApiStatus.ERROR, "Error: " + e.getMessage());
    }
  }

  /**
   * 
   * does the computation: read input, run compute, write output, return results
   * as ArrayList<BigInteger>
   */
  @Override
  public ComputationResponse compute(ComputationRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }

    if (loggedInUserId == null || sessionToken == null) {
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "User must be logged in to run computations");
    }
    try {
      // Load BigIntegers from input resource
      LoadResponse loadResp = readWrite.load(
          new LoadRequest(request.getInputResource(), request.getDelimiter()));
      if (loadResp.getStatus() != ApiStatus.SUCCESS) {
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
            loadResp.getMessage());
      }

      // input list of BigInteger
      List<BigInteger> inputs = loadResp.getPayload();
      List<BigInteger> results = new ArrayList<>();

      // Run computation for each BigInteger
      for (BigInteger value : inputs) {
        results.add(compute.performComputation(value).getResult());
      }

      // Store results in output resource
      List<BigInteger> resultBatch = new ArrayList<>(results);
      StoreResponse storeResp = readWrite.store(new StoreRequest(
          request.getOutputResource(), resultBatch, request.getDelimiter()));

      if (storeResp.getStatus() != ApiStatus.SUCCESS) {
        String msg = storeResp.getMessage();
        if (msg == null) {
          msg = "Failed to store results";
        }
        return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(), msg);
      }

      // return ComputationResponse to the user, results stored in a List
      return new ComputationResponse(ApiStatus.SUCCESS, resultBatch,
          "Computation completed");

    } catch (IllegalArgumentException e) {
      // catch null request
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // catch unexpected exceptions
      return new ComputationResponse(ApiStatus.ERROR, new ArrayList<>(),
          "Error: " + e.getMessage());
    }
  }

  // not exactly sure why i have to add this, but according to TestMultiUser i
  // do
  public List<String> processRequests(List<String> requests) {

    return new ArrayList<>(requests);
  }

  public ProcessApi getReadWrite() {
    return readWrite;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public String getLoggedInUserId() {
    return loggedInUserId;
  }

  public void setReadWrite(ProcessApi readWrite) {
    this.readWrite = readWrite;
  }

  public ConceptualAPI getCompute() {
    return compute;
  }

  public void setCompute(ConceptualAPI compute) {
    this.compute = compute;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}
