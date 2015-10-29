package boczula.mateusz.integration;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.flexionmobile.codingchallenge.integration.Purchase;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public final class IntegrationApi {

	private static final String REST_API_URI = "http://dev2.flexionmobile.com/javachallenge/rest";
	private static final String APPLICATION_JSON = "application/json";

	static ClientResponse callBuy(String developerId, String itemId) {
		URI uri = UriBuilder
				.fromPath(REST_API_URI).path("/developer/{developerId}/buy/{itemId}")
				.build(developerId, itemId);
		

		return postJsonResponse(uri);
	}
	
	static ClientResponse callConsume(String developerId, Purchase purchase) {
		URI uri = UriBuilder
				.fromPath(REST_API_URI).path("/developer/{developerId}/consume/{purchaseId}")
				.build(developerId, purchase.getId());
		
		return postJsonResponse(uri);
	}
	
	static ClientResponse callGetAllPurchases(String developerId) {
		URI uri =  UriBuilder
				.fromPath(REST_API_URI).path("/developer/{developerId}/all")
				.build(developerId);
		
		return getJsonResponse(uri);
	}
	
	private static ClientResponse getJsonResponse(URI uri) {
		WebResource resource = Client.create().resource(uri);
		
		return resource.accept(APPLICATION_JSON).get(ClientResponse.class);
	}
	
	private static ClientResponse postJsonResponse(URI uri) {
		WebResource resource = Client.create().resource(uri);
		
		return resource.accept(APPLICATION_JSON).post(ClientResponse.class);
	}
}
