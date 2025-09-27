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