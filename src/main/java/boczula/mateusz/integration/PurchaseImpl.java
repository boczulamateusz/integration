package boczula.mateusz.integration;

import com.flexionmobile.codingchallenge.integration.Purchase;

public class PurchaseImpl implements Purchase {
	
	private String id;
	private String itemId;
	private boolean consumed;


	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@Override
	public String getItemId() {
		return itemId;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}
	
	@Override
	public boolean getConsumed() {
		return consumed;
	}

}
