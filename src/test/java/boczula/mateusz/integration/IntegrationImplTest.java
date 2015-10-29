package boczula.mateusz.integration;

import java.util.List;





import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.Purchase;
import com.google.common.collect.Sets;

public class IntegrationImplTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Integration integration;
	
	@Before
	public void setup() {
		integration = new IntegrationImpl(UUID.randomUUID().toString());
	}
	
	@Test
	public void shouldBuyItem() {
		String itemId = "someItem";
		Purchase purchase = integration.buy(itemId);
		
		Assert.assertNotNull(purchase);
		Assert.assertNotNull(purchase.getId());
		Assert.assertTrue(!purchase.getId().isEmpty());
		Assert.assertEquals(itemId, purchase.getItemId());
		Assert.assertFalse(purchase.getConsumed());
	}
	
	@Test
	public void shouldNotConsumeWhenPurchaseNotFound() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Purchase does not exists");
		integration.consume(new PurchaseImpl("not-found-id", "not-found-itemId"));
	}
	
	@Test
	public void shouldNotConsumeWhenPurchaseAlreadyConsumed() {
		String itemId = "someItemId";
		Purchase purchase = integration.buy(itemId);
		integration.consume(purchase);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Purchase has already been consumed");
		integration.consume(purchase);
	}
	
	@Test
	public void shouldGetPurchases() {
		Purchase notConsumedPurchase = integration.buy("someItemId");

		Purchase consumedPurchase = integration.buy("someOtherItemId");
		integration.consume(consumedPurchase);
		
		List<Purchase> purchases = integration.getPurchases();
		
		Assert.assertNotNull(purchases);
		Assert.assertEquals(2, purchases.size());
		
		String consumedPurchaseId = consumedPurchase.getId();
		String notConsumedPurchaseId = notConsumedPurchase.getId();
		Set<String> purchaseIds = Sets.newHashSet(consumedPurchaseId, notConsumedPurchaseId);
		Assert.assertTrue(purchases.stream().map(Purchase::getId).allMatch(purchaseIds::contains));
		Assert.assertTrue(!purchases.stream().map(Purchase::getId).anyMatch(purchase -> !purchaseIds.contains(purchase)));
		
		Optional<Purchase> foundNotConsumedPurchase = purchases.stream()
				.filter(purchase -> notConsumedPurchaseId.equals(purchase.getId()))
				.findAny();
		Assert.assertTrue(foundNotConsumedPurchase.isPresent());
		Assert.assertTrue(!foundNotConsumedPurchase.get().getConsumed());
		
		Optional<Purchase> foundConsumedPurchase = purchases.stream()
				.filter(purchase -> consumedPurchaseId.equals(purchase.getId()))
				.findAny();
		Assert.assertTrue(foundConsumedPurchase.isPresent());
		Assert.assertTrue(foundConsumedPurchase.get().getConsumed());
		
	}

}
