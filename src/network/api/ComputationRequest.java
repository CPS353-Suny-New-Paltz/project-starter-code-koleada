package network.api;

import shared.stuff.Resource;
/**
 * 
 * Request for a computation, containing input and output resources.
 */
public class ComputationRequest {
  private final Resource outputResource;
  private final Resource inputResource;
  private final Delimiter delimiter;

  public ComputationRequest(Resource inputResource, Resource outputResource,
      Delimiter delimiter) {

    // validation of inputs, since this class is passed as a param to public
    // network api methods
    if (inputResource == null) {
      throw new IllegalArgumentException("Input resource cannot be null.");
    }
    if (outputResource == null) {
      throw new IllegalArgumentException("Output resource cannot be null.");
    }
    if (delimiter == null) {
      throw new IllegalArgumentException("Delimiter cannot be null.");
    }
    if (inputResource.equals(outputResource)) {
      throw new IllegalArgumentException(
          "Input and output resources must be different.");
    }

    this.inputResource = inputResource;
    this.outputResource = outputResource;
    this.delimiter = delimiter;
  }

  public Resource<?> getInputResource() {
    return inputResource;
  }

  public Resource<?> getOutputResource() {
    return outputResource;
  }

  public Delimiter getDelimiter() {
    return delimiter;
  }
}