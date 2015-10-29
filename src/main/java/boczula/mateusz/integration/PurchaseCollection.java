package boczula.mateusz.integration;

import java.util.List;
import java.util.stream.Collectors;

import com.flexionmobile.codingchallenge.integration.Purchase;

public class PurchaseCollection {
	
	private List<PurchaseImpl> purchases;

	public List<? extends Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<PurchaseImpl> purchases) {
		this.purchases = purchases;
	}

}
