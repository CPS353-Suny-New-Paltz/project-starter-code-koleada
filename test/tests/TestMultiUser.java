package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.implementations.MultithreadedNetworkAPI;
import api.implementations.NetworkAPI;
import network.api.NetworkApi;

public class TestMultiUser {

  // TODO 1: change the type of this variable to the name you're using for your
  // @NetworkAPI
  // interface
  private NetworkApi coordinator;
  private MultithreadedNetworkAPI networkAPI;

  @BeforeEach
  public void initializeComputeEngine() {

    networkAPI = new MultithreadedNetworkAPI();
    // TODO 2: create an instance of the implementation of your @NetworkAPI;
    // this is the component
    // that the user will make requests to
    // Store it in the 'coordinator' instance variable
    coordinator = new NetworkAPI();
  }

  public void cleanup() {
    if (networkAPI != null) {
      networkAPI.shutdown();
    }
  }

  @Test
  public void compareMultiAndSingleThreaded() throws Exception {
    int numThreads = 4;
    List<TestUser> testUsers = new ArrayList<>();
    for (int i = 0; i < numThreads; i++) {
      testUsers.add(new TestUser(coordinator));
    }

    // Run single threaded
    String singleThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp";
    for (int i = 0; i < numThreads; i++) {
      File singleThreadedOut = new File(singleThreadFilePrefix + i);
      singleThreadedOut.deleteOnExit();
      testUsers.get(i).run(singleThreadedOut.getCanonicalPath());
    }

    // Run multi threaded
    ExecutorService threadPool = Executors.newCachedThreadPool();
    List<Future<?>> results = new ArrayList<>();
    String multiThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp";
    for (int i = 0; i < numThreads; i++) {
      File multiThreadedOut = new File(multiThreadFilePrefix + i);
      multiThreadedOut.deleteOnExit();
      String multiThreadOutputPath = multiThreadedOut.getCanonicalPath();
      TestUser testUser = testUsers.get(i);
      results.add(threadPool.submit(() -> testUser.run(multiThreadOutputPath)));
    }

    results.forEach(future -> {
      try {
        future.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    // Check that the output is the same for multi-threaded and single-threaded
    List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix,
        numThreads);
    List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix,
        numThreads);
    assertEquals(singleThreaded, multiThreaded);
  }

  private List<String> loadAllOutput(String prefix, int numThreads)
      throws IOException {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < numThreads; i++) {
      File multiThreadedOut = new File(prefix + i);
      result.addAll(Files.readAllLines(multiThreadedOut.toPath()));
    }
    return result;
  }
  @Test
  public void smokeTest() {
    List<String> requests = List.of("test1", "test2", "test3");
    List<String> results = networkAPI.processRequests(requests);
    assertEquals(requests.size(), results.size());
  }
}
