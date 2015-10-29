package boczula.mateusz.integration;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.Purchase;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IntegrationImpl implements Integration {
	
	private static final String UNEXPECTED_HTTP_STATUS = "Unexpected HTTP status: ";
	
	private String developerId;
	private ObjectMapper mapper = new ObjectMapper();
	
	public IntegrationImpl(String developerId) {
		Preconditions.checkNotNull(developerId);
		Preconditions.checkArgument(!developerId.isEmpty());
		
		this.developerId = developerId;
	}

	public Purchase buy(String itemId) {
		Preconditions.checkNotNull(itemId);
		Preconditions.checkArgument(!itemId.isEmpty());
		
		ClientResponse response = IntegrationApi.callBuy(developerId, itemId);
		
		try {
			checkSuccessResponseStatus(response);
			return mapper.readValue(response.getEntityInputStream(), PurchaseImpl.class);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void consume(Purchase purchase) {
		Preconditions.checkNotNull(purchase);
		
		List<Purchase> purchases = getPurchases();
		Optional<Purchase> optPurchase = nullGuard(purchases).stream()
				.filter(p -> p.getId().equals(purchase.getId())).findAny();
		
		if(optPurchase.isPresent()) {
			Purchase found = optPurchase.get();
			
			if(!found.getConsumed()) {
				ClientResponse response = IntegrationApi.callConsume(developerId, purchase);
				checkSuccessResponseStatus(response);
			}
			else {
				throw new IllegalArgumentException("Purchase has already been consumed");
			}
		}
		else {
			throw new IllegalArgumentException("Purchase does not exists");
		}
	}

	public List<Purchase> getPurchases() {
		ClientResponse response = IntegrationApi.callGetAllPurchases(developerId);
		checkSuccessResponseStatus(response);

		try {
			PurchaseCollection purchaseCollection = mapper.readValue(
					response.getEntityInputStream(), PurchaseCollection.class);
				
			if(purchaseCollectionNotEmpty(purchaseCollection)) {
				return (List<Purchase>) purchaseCollection.getPurchases();
			}
			else {
				return Collections.emptyList();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void checkSuccessResponseStatus(ClientResponse response) {
		int status = response.getStatus();
		if(status != 200) {
			throw new RuntimeException(UNEXPECTED_HTTP_STATUS + status);
		}
	}
	
	private static <T> List<T> nullGuard(List<T> list) {
		return Optional.ofNullable(list).orElse(Collections.emptyList());
	}

	private static boolean purchaseCollectionNotEmpty(PurchaseCollection purchaseCollection) {
		return purchaseCollection != null && purchaseCollection.getPurchases() != null 
				&& !purchaseCollection.getPurchases().isEmpty();
	}

}
