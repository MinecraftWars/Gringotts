package net.mcw.gringotts;

public final class Util {

	//Have these as utility functions for easy tweaking of conversion
	
	static long ToCents( double emeralds ) {
		return (long)(emeralds * 100);
	}
	
	static double ToEmeralds( long cents ) { 
		return cents / 100;
	}
	
}
