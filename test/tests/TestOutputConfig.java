package tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Test-only in-memory output configuration used to store data.
 */
public class TestOutputConfig {

  List<String> outputData;

  public TestOutputConfig() {
    this.outputData = new ArrayList<>();
  }

  public TestOutputConfig(List<String> outputData) {
    this.outputData = outputData;
  }

  public List<String> getOutputData() {
    return outputData;
  }

  public void setOutputData(List<String> outputData) {
    this.outputData = outputData;
  }

  public void writeData(String value) {
    this.outputData.add(value);
  }
}
