package network.api;

import shared.stuff.Resource;
/**
 * 
 * Request for a computation, containing input and output resources.
 */
public class ComputationRequest {
  private final Resource outputResource;
  private final Resource inputResource;

  public ComputationRequest(Resource inputResource, Resource outputResource) {
    this.inputResource = inputResource;
    this.outputResource = outputResource;
  }

  public Resource<?> getInputResource() {
    return inputResource;
  }

  public Resource<?> getOutputResource() {
    return outputResource;
  }
}