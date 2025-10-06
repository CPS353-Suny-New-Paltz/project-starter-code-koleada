package process.api;

import shared.stuff.ApiStatus;
import shared.stuff.Resource;

public class StoreResponse {

	private final ApiStatus status;
	private final Resource resource;
	private final String message;

	public StoreResponse(ApiStatus status, Resource resource, String message) {
		this.status = status;
		this.resource = resource;
		this.message = message;
	}

	public StoreResponse(ApiStatus status) {
		this.status = status;
		this.resource = null;
		this.message = null;
	}

	public boolean success() {
		return status == ApiStatus.SUCCESS;
	}

	public ApiStatus getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}
}
