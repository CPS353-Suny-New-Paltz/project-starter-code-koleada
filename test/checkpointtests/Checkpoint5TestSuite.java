package checkpointtests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class Checkpoint5TestSuite {

  // Verify that exceptions do not propagate back to the user as exceptions
  @Test
  public void testFileWritten() throws Exception {
    Path inputPath = Paths.get(ManualTestingFramework.INPUT);
    Files.deleteIfExists(inputPath);

    ManualTestingFramework.main(new String[]{});
  }
}
