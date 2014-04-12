package org.gestern.gringotts.api;

public interface Currency {

    /**
     * Singular name of this currency.
     * @return singular name of this currency
     */
    String name();

    /**
     * Plural name of this currency.
     * @return plural name of this currency
     */
    String namePlural();

    /**
     * Get a formatted currency value. 
     * The resulting string includes the amount as well as currency name or name of individual denominations.
     * @param value value to format.
     * @return the formatted currency value.
     */
    String format(double value);

    /**
     * Get the amount of fractional digits supported by this currency.
     * @return the amount of fractional digits supported by this currency
     */
    int fractionalDigits();
}