package boczula.mateusz.integration;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.Purchase;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IntegrationImpl implements Integration {
	
	private static final String REST_API_URI = "http://dev2.flexionmobile.com/javachallenge/rest";
	private static final String APPLICATION_JSON = "application/json";
	
	private String developerId;
	private ObjectMapper mapper = new ObjectMapper();
	
	public IntegrationImpl(String developerId) {
		this.developerId = developerId;
	}

	public Purchase buy(String itemId) {
		URI uri = UriBuilder
			.fromPath(REST_API_URI).path("/developer/{developerId}/buy/{itemId}")
			.build(developerId, itemId);
		
		try {
			ClientResponse response = postJsonResponse(uri);
			
			if(response.getStatus() == 200) {
				return mapper.readValue(response.getEntityInputStream(), PurchaseImpl.class);
			}
			
		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void consume(Purchase purchase) {
		
		List<Purchase> purchases = getPurchases();
		Optional<Purchase> foundPurchase = purchases.stream()
				.filter(p -> !p.getConsumed())
				.filter(p -> p.getId().equals(purchase.getId())).findAny();
		
		if(foundPurchase.isPresent()) {
			Purchase found = foundPurchase.get();
			URI uri = UriBuilder
					.fromPath(REST_API_URI).path("/developer/{developerId}/consume/{purchaseId}")
					.build(developerId, found.getId());

			postJsonResponse(uri);
		}
	}

	public List<Purchase> getPurchases() {
		URI uri = UriBuilder
				.fromPath(REST_API_URI).path("/developer/{developerId}/all")
				.build(developerId);
		
		try {
			ClientResponse response = getJsonResponse(uri);
			
			if(response.getStatus() == 200) {
				PurchaseCollection purchaseCollection = mapper.readValue(
						response.getEntityInputStream(), PurchaseCollection.class);
				
				if(purchaseCollectionNotEmpty(purchaseCollection)) {
					return (List<Purchase>) purchaseCollection.getPurchases();
				}
			}
		
		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}

	private static boolean purchaseCollectionNotEmpty(PurchaseCollection purchaseCollection) {
		return purchaseCollection != null && purchaseCollection.getPurchases() != null 
				&& !purchaseCollection.getPurchases().isEmpty();
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
