package tests;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Test-only in-memory input configuration used to load/read data.
 */
public class TestInputConfig {
  List<BigInteger> inputData;

  public TestInputConfig() {
    this.inputData = new ArrayList<>();
  }

  public TestInputConfig(List<BigInteger> inputData) {
    this.inputData = inputData;
  }

  public void addInputData(BigInteger value) {
    this.inputData.add(value);
  }
}