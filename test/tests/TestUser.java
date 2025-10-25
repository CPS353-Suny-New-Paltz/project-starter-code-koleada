package tests;

import java.io.File;

import network.api.NetworkApi;

public class TestUser {

  // TODO 3: change the type of this variable to the name you're using for your
  // @NetworkAPI interface; also update the parameter passed to the constructor
  private final NetworkApi coordinator;

  public TestUser(NetworkApi coordinator) {
    this.coordinator = coordinator;
  }

  public void run(String outputPath) {
    char delimiter = ';';
    String inputPath = "test" + File.separatorChar + "testInputFile.test";

    // TODO 4: Call the appropriate method(s) on the coordinator to get it to
    // run the compute job specified by inputPath, outputPath, and delimiter
  }

}
