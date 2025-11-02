package api.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
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

  public ProcessAPI() {

  }

  @Override
  public LoadResponse load(LoadRequest request) {
    try {
      if (request == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }
      Resource src = request.getSource();
      if (src.getType() == ResourceType.FILE && src.getUri() != null) {
        // delegate to helper, which already has its own exception handling
        return loadFromFile(src, request.getDelimiter());
      }
      if (src.getType() == ResourceType.CUSTOM) {
        return new LoadResponse(ApiStatus.SUCCESS, src.getData(),
            request.getDelimiter(), null);
      }
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(),
          "Unsupported resource type or missing URI");

    } catch (IllegalArgumentException e) {
      // null req
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // unexpected exceptions
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public StoreResponse store(StoreRequest request) {
    try {
      if (request == null) {
        throw new IllegalArgumentException("Request cannot be null");
      }
      Resource dest = request.getDestination();
      if (dest.getType() == ResourceType.FILE && dest.getUri() != null) {
        // delegate to helper, which already has its own exception handling
        return storeToFile(dest, request.getPayload(), request.getDelimiter());
      }
      if (dest.getType() == ResourceType.CUSTOM) {
        dest.setData(request.getPayload());
        return new StoreResponse(ApiStatus.SUCCESS, dest, null);
      }
      return new StoreResponse(ApiStatus.ERROR, dest,
          "Unsupported resource type or missing URI");

    } catch (IllegalArgumentException e) {
      // catch null request
      return new StoreResponse(ApiStatus.ERROR,
          // if request is not null add the destination to the response
          request != null ? request.getDestination() : null,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      // unexpected exceptions
      return new StoreResponse(ApiStatus.ERROR,
          request != null ? request.getDestination() : null,
          "Unexpected error: " + e.getMessage());
    }
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
      if (src.getUri() == null) {
        throw new IllegalArgumentException("File URI cannot be null");
      }

      // Just get path as is; relative paths go to project root
      Path path = Paths.get(src.getUri().trim());

      if (!Files.exists(path)) {
        throw new IllegalArgumentException("File " + path + " does not exist");
      }
      if (!Files.isReadable(path)) {
        throw new IllegalArgumentException("File is not readable: " + path);
      }

      String content = Files.readString(path);
      String regex = Pattern.quote(delimiter.getValue()); // got a super weird
                                                          // error from using |
                                                          // as a delim have to
                                                          // escape special
                                                          // chars
      String[] tokens = content.split(regex);

      List<Integer> data = Arrays.stream(tokens).map(String::trim)
          .filter(s -> !s.isEmpty()).map(Integer::parseInt)
          .collect(Collectors.toList());

      return new LoadResponse(ApiStatus.SUCCESS, new ArrayList<>(data),
          delimiter, "Loaded successfully"); // <-- return the actual delimiter
                                             // used

    } catch (IllegalArgumentException e) {
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Invalid file: " + e.getMessage());
    } catch (IOException e) {
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "I/O error: " + e.getMessage());
    } catch (Exception e) {
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Unexpected error: " + e.getMessage());
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
  private StoreResponse storeToFile(Resource<?> dest, List<?> batch,
      Delimiter delimiter) {
    try {
      if (dest.getUri() == null) {
        throw new IllegalArgumentException("Destination URI cannot be null");
      }

      // Just write directly to the file; relative paths go to project root
      Path path = Paths.get(dest.getUri().trim());

      String joined = batch.stream().map(Object::toString)
          .collect(Collectors.joining(delimiter.getValue()));

      Files.writeString(path, joined);

      return new StoreResponse(ApiStatus.SUCCESS, dest, "Stored successfully");

    } catch (IllegalArgumentException e) {
      return new StoreResponse(ApiStatus.ERROR, dest,
          "Invalid file: " + e.getMessage());
    } catch (IOException e) {
      return new StoreResponse(ApiStatus.ERROR, dest,
          "I/O error: " + e.getMessage());
    } catch (Exception e) {
      return new StoreResponse(ApiStatus.ERROR, dest,
          "Unexpected error: " + e.getMessage());
    }
  }

}
