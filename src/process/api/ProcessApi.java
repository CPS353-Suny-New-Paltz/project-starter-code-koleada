package process.api;

import project.annotations.ProcessAPI;

@ProcessAPI
public interface ProcessApi {

  LoadResponse load(LoadRequest request);

  StoreResponse store(StoreRequest request);
}
