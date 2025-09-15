package process.api;

import java.util.ArrayList;

import project.annotations.ProcessAPIPrototype;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * The class for our Process API prototype
 */
public class ProcessApiPrototype {

  /**
   * The method that showcases the Process API functionality (storing and
   * loading)
   * 
   * @param api
   *          interface
   */
  @ProcessAPIPrototype
  public void prototype(ProcessApi api) {

    // create DataBatch (generic wrapper to hold data)
    ArrayList<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
    numbers.add(4);
    numbers.add(5);
    DataBatch<ArrayList<Integer>> data = new DataBatch<>(numbers);

    // Store data in a resource
    Resource rec = new Resource(ResourceType.DATABASE,
        "db://myDatabaseToWrite");

    StoreRequest storeReq = new StoreRequest(rec, data);
    StoreResponse storeResp = api.store(storeReq);
    System.out.println("Store status: " + storeResp.success());
    if (storeResp.getMessage() != null) {
      System.out.println(storeResp.getMessage());
    }

    // Load data from a resource
    Resource rec2 = new Resource(ResourceType.DATABASE,
        "db://myDatabaseToRead");

    LoadRequest loadReq = new LoadRequest(rec2);
    LoadResponse loadResp = api.load(loadReq);
    System.out.println("Load status: " + loadResp.getStatus());
    if (loadResp.getMessage() != null) {
      System.out.println(loadResp.getMessage());
    }
    if (loadResp.getData() != null) {
      System.out.println("Loaded data: " + loadResp.getData());
    } else {
      System.out.println("No data returned.");
    }
  }
}
