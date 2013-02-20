package org.gestern.gringotts;

import org.bukkit.configuration.file.FileConfiguration;

public enum Language
{

    
    LANG;
    public String pay_noperm;
    public String pay_taxMessage;
    public String pay_sentMessage;
    public String pay_receivedMessage;
    public String pay_insufficientFunds;
    public String pay_insS_sentMessage;
    public String pay_insS_receivedMessage;
    public String pay_error;

    public void readLanguage(FileConfiguration savedLanguage)
    {
        LANG.pay_sentMessage = savedLanguage.getString("pay.success.sentMessage", "Sent %value to %player. ");
        LANG.pay_noperm = savedLanguage.getString("pay.noperm", "You do not have permission to transfer money.");
        LANG.pay_taxMessage = savedLanguage.getString("pay.success.taxMessage", "Received %value from %player.");
        LANG.pay_receivedMessage = savedLanguage.getString("pay.success.receivedMessage", "Transaction tax deducted from your account: %value");
        LANG.pay_error = savedLanguage.getString("pay.error", "Your attempt to send %value to %player failed for unknown reasons.");
        LANG.pay_insufficientFunds = savedLanguage.getString("pay.insufficientFunds", "Your account has insufficient balance. Current balance: %balance. Required: %value");
        LANG.pay_insS_sentMessage = savedLanguage.getString("pay.insufficientSpace.sentMessage", "%player has insufficient storage space for %value");
        LANG.pay_insS_receivedMessage = savedLanguage.getString("pay.insufficientSpace.receivedMessage", "%player tried to send %value, but you don't have enough space for that amount.");
    }
}
