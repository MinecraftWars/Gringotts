package org.gestern.gringotts.api;

public interface Account {

	boolean exists();
	Account create();
	void delete();
	
	
	double balance();
	boolean has(double value);
	TransactionResult setBalance(double newBalance);

	TransactionResult add(double value);
	TransactionResult remove(double value);
	
	TransactionResult sendTo(double value, Account recepient);
	
	void message(String message);

}
