package api.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import network.api.Delimiter;
import process.api.LoadRequest;
import process.api.LoadResponse;
import process.api.ProcessApi;
import process.api.StoreRequest;
import process.api.StoreResponse;
import shared.stuff.ApiStatus;
import shared.stuff.Resource;
import shared.stuff.ResourceType;

/**
 * Implementation of the ProcessApi interface
 */
public class ProcessAPI implements ProcessApi {

  // need to know which resource for storing and loading data
  private Resource resource;

  public ProcessAPI(Resource resource) {
    this.resource = resource;
  }

  @Override
  public LoadResponse load(LoadRequest request) {
    Resource src = request.getSource();
    if (src.getType() == ResourceType.FILE && src.getUri() != null) {
      return loadFromFile(src, request.getDelimiter());
    }
    return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
        Delimiter.defaultDelimiter(),
        "Unsupported resource type or missing URI");
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    Resource dest = request.getDestination();
    if (dest.getType() == ResourceType.FILE && dest.getUri() != null) {
      return storeToFile(dest, request.getPayload(), request.getDelimiter());
    }
    return new StoreResponse(ApiStatus.ERROR, dest,
        "Unsupported resource type or missing URI");
  }

  /**
   * Helper method to load data from a file
   * 
   * @param src,
   *          Resource to read from
   * @param delimiter
   *          used to split data and add it to a List
   * @return
   */
  private LoadResponse loadFromFile(Resource src, Delimiter delimiter) {
    try {
      String content = Files.readString(Paths.get(src.getUri()));
      String[] tokens = content.split(delimiter.getValue());

      List<Integer> data = Arrays.stream(tokens).map(String::trim)
          .filter(s -> !s.isEmpty()).map(Integer::parseInt)
          .collect(Collectors.toList());

      return new LoadResponse(ApiStatus.SUCCESS, new ArrayList<>(data),
          Delimiter.defaultDelimiter(), "Loaded successfully");
    } catch (IOException | NumberFormatException e) {
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Failed to load: " + e.getMessage());
    }

  }

  /**
   * Helper method to store data to a file
   * 
   * @param dest
   *          the Resource which describes the file we are to write to
   * @param batch
   *          DataBatch<List> the data we write to the file
   * @param delimiter
   *          added in between each data element
   * @return
   */
  private StoreResponse storeToFile(Resource dest, List batch,
      Delimiter delimiter) {
    try {
      List<?> payload = batch;
      String joined = payload.stream().map(Object::toString)
          .collect(Collectors.joining(delimiter.getValue()));

      Files.writeString(Paths.get(dest.getUri()), joined);

      return new StoreResponse(ApiStatus.SUCCESS, dest, "Stored successfully");
    } catch (IOException e) {
      return new StoreResponse(ApiStatus.ERROR, dest,
          "Failed to store: " + e.getMessage());
    }

  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

}
