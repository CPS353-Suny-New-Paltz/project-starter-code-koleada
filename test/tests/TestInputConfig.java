package tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Test-only in-memory input configuration Used to load/read data from
 */
public class TestInputConfig {
  List<Integer> inputData;

  public TestInputConfig() {
    this.inputData = new ArrayList<>();
  }

  public TestInputConfig(List<Integer> inputData) {
    this.inputData = inputData;
  }

  public void addInputData(Integer value) {
    this.inputData.add(value);
  }
}
