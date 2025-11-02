package tests;

import java.io.File;

import network.api.ComputationRequest;
import network.api.ComputationResponse;
import network.api.Delimiter;
import network.api.NetworkApi;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

public class TestUser {

  // TODO 3: change the type of this variable to the name you're using for your
  // @NetworkAPI interface; also update the parameter passed to the constructor
  private final NetworkApi coordinator;

  public TestUser(NetworkApi coordinator) {
    this.coordinator = coordinator;

  }

  /**
   * Getting a super weird error on github, its saying testInputFile.test doesnt
   * exist adn wotn let me merge, locally everything passes with no errors, no
   * idea why this is happening now
   * 
   * public void run(String outputPath) { String projectRoot =
   * System.getProperty("user.dir"); String inputPath = projectRoot +
   * File.separator + "test" + File.separator + "testInputFile.test";
   * 
   * // TODO 4: Call the appropriate method(s) on the coordinator to get it to
   * // run the compute job specified by inputPath, outputPath, and delimiter
   * 
   * Resource inputResource = new Resource(ResourceType.FILE, inputPath);
   * Resource outputResource = new Resource(ResourceType.FILE, outputPath);
   * 
   * // delimiter object using , Delimiter delim = Delimiter.defaultDelimiter();
   * 
   * // Build request ComputationRequest request = new
   * ComputationRequest(inputResource, outputResource, delim);
   * 
   * ComputationResponse response = coordinator.compute(request);
   * 
   * if (response.getStatus() != ApiStatus.SUCCESS) {
   * System.out.println("Compute failed: " + response.getMessage()); }
   * 
   * File f = new File(outputPath); System.out.println( "Output exists? " +
   * f.exists() + ", path = " + f.getAbsolutePath());
   * 
   * }
   */

  public void run(String outputPath) {
    // Load from classpath
    String inputPath;
    try {
      inputPath = new File(
          getClass().getClassLoader().getResource("testInputFile.test").toURI())
          .getAbsolutePath();
    } catch (Exception e) {
      throw new RuntimeException("Test input file not found", e);
    }

    Resource inputResource = new Resource(ResourceType.FILE, inputPath);
    Resource outputResource = new Resource(ResourceType.FILE, outputPath);

    Delimiter delim = Delimiter.defaultDelimiter();
    ComputationRequest request = new ComputationRequest(inputResource,
        outputResource, delim);
    ComputationResponse response = coordinator.compute(request);

    if (response.getStatus() != ApiStatus.SUCCESS) {
      System.out.println("Compute failed: " + response.getMessage());
    }

    File f = new File(outputPath);
    System.out.println(
        "Output exists? " + f.exists() + ", path = " + f.getAbsolutePath());
  }

}
