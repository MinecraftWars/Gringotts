package org.gestern.gringotts.api;

public interface Eco {

	Account player(String name);
	Account bank(String name);
	Account virtual(String name);
	Account custom(String type, String id);
	
	Account faction(String id);
	Account town(String id);
	
	Currency currency();
}
