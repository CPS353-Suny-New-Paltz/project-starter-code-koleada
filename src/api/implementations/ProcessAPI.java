package api.implementations;

import java.io.IOException;
import java.math.BigInteger;
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
        return loadFromFile(src, request.getDelimiter());
      }

      if (src.getType() == ResourceType.CUSTOM) {
        List<BigInteger> data = src.getData() != null
            ? src.getData()
            : new ArrayList<>();
        return new LoadResponse(ApiStatus.SUCCESS, new ArrayList<>(data),
            request.getDelimiter(), "Loaded successfully");
      }

      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(),
          "Unsupported resource type or missing URI");

    } catch (IllegalArgumentException e) {
      return new LoadResponse(ApiStatus.ERROR, new ArrayList<>(),
          Delimiter.defaultDelimiter(), "Invalid request: " + e.getMessage());
    } catch (Exception e) {
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
      List<BigInteger> batch = request.getPayload();

      if (dest.getType() == ResourceType.FILE && dest.getUri() != null) {
        return storeToFile(dest, batch, request.getDelimiter());
      }

      if (dest.getType() == ResourceType.CUSTOM) {
        dest.setData(new ArrayList<>(batch));
        return new StoreResponse(ApiStatus.SUCCESS, dest,
            "Stored successfully");
      }

      return new StoreResponse(ApiStatus.ERROR, dest,
          "Unsupported resource type or missing URI");

    } catch (IllegalArgumentException e) {
      return new StoreResponse(ApiStatus.ERROR,
          request != null ? request.getDestination() : null,
          "Invalid request: " + e.getMessage());
    } catch (Exception e) {
      return new StoreResponse(ApiStatus.ERROR,
          request != null ? request.getDestination() : null,
          "Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Helper method to load data from a file
   */
  private LoadResponse loadFromFile(Resource src, Delimiter delimiter) {
    try {
      if (src.getUri() == null) {
        throw new IllegalArgumentException("File URI cannot be null");
      }

      Path path = Paths.get(src.getUri().trim());

      if (!Files.exists(path)) {
        throw new IllegalArgumentException("File " + path + " does not exist");
      }
      if (!Files.isReadable(path)) {
        throw new IllegalArgumentException("File is not readable: " + path);
      }

      String content = Files.readString(path);
      String regex = Pattern.quote(delimiter.getValue());
      String[] tokens = content.split(regex);

      List<BigInteger> data = Arrays.stream(tokens).map(String::trim)
          .filter(s -> !s.isEmpty()).map(BigInteger::new)
          .collect(Collectors.toList());

      return new LoadResponse(ApiStatus.SUCCESS, new ArrayList<>(data),
          delimiter, "Loaded successfully");

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
   */
  private StoreResponse storeToFile(Resource dest, List<BigInteger> batch,
      Delimiter delimiter) {
    try {
      if (dest.getUri() == null) {
        throw new IllegalArgumentException("Destination URI cannot be null");
      }

      Path path = Paths.get(dest.getUri().trim());

      String joined = batch.stream().map(BigInteger::toString)
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
