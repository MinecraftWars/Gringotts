package org.gestern.gringotts;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Deals with all the language Strings.
 * 
 * In case Strings are not included in language.yml or there is no language.yml
 * there are default-values included here. If someone complains about a String not translating,
 * check for the yml-nodes in readLanguage below. If the node does not match the language file,
 * the default English message will be shown.
 * 
 * @author Daenara (KanaYamamoto Q bukkit.org)
 */
public enum Language
{
    LANG;
    //global
    public String noperm;
    public String playerOnly;
    public String balance;
    public String vault_balance;
    public String inv_balance;
    public String invalid_account;
    public String reload;
    //pay command
    public String pay_success_tax;
    public String pay_success_sender;
    public String pay_success_target;
    public String pay_insufficientFunds;
    public String pay_insS_sender;
    public String pay_insS_target;
    public String pay_error;
    //deposit command
    public String deposit_success;
    public String deposit_error;
    //withdraw command
    public String withdraw_success;
    public String withdraw_error;
    //moneyadmin command
    public String moneyadmin_b;
    public String moneyadmin_add_sender;
    public String moneyadmin_add_target;
    public String moneyadmin_add_error;
    public String moneyadmin_rm_sender;
    public String moneyadmin_rm_target;
    public String moneyadmin_rm_error;
    //gringotts vaults
    public String vault_created;
    public String vault_error;
    public String vault_noVaultPerm;
    //towny plugin
    public String plugin_towny_noTownVaultPerm;
    public String plugin_towny_noTownResident;
    public String plugin_towny_noNationVaultPerm;
    public String plugin_towny_notInNation;
    //faction plugin
    public String plugin_faction_noVaultPerm;
    public String plugin_faction_notInFaction;
    //vault plugin
    public String plugin_vault_insufficientFunds;
    public String plugin_vault_insufficientSpace;
    public String plugin_vault_error;
    public String plugin_vault_notImplemented;

    public void readLanguage(FileConfiguration savedLanguage)
    {
        //global
        LANG.noperm = savedLanguage.getString("noperm", "You do not have permission to transfer money.");
        LANG.playerOnly = savedLanguage.getString("playeronly", "This command can only be run by a player.");
        LANG.balance = savedLanguage.getString("balance", "Your current total balance: %balance");
        LANG.vault_balance = savedLanguage.getString("vault_balance", "Vault balance: %balance");
        LANG.inv_balance = savedLanguage.getString("inv_balance", "Inventory balance: %balance");
        LANG.invalid_account = savedLanguage.getString("invalidaccount", "Invalid account: %player");
        LANG.reload = savedLanguage.getString("reload", "Gringotts: Reloaded configuration!");

        //pay command
        LANG.pay_success_sender = savedLanguage.getString("pay.success.sender", "Sent %value to %player. ");
        LANG.pay_success_tax = savedLanguage.getString("pay.success.tax", "Received %value from %player.");
        LANG.pay_success_target = savedLanguage.getString("pay.success.target", "Transaction tax deducted from your account: %value");
        LANG.pay_error = savedLanguage.getString("pay.error", "Your attempt to send %value to %player failed for unknown reasons.");
        LANG.pay_insufficientFunds = savedLanguage.getString("pay.insufficientFunds", "Your account has insufficient balance. Current balance: %balance. Required: %value");
        LANG.pay_insS_sender = savedLanguage.getString("pay.insufficientSpace.sender", "%player has insufficient storage space for %value");
        LANG.pay_insS_target = savedLanguage.getString("pay.insufficientSpace.target", "%player tried to send %value, but you don't have enough space for that amount.");

        //deposit command
        LANG.deposit_success = savedLanguage.getString("deposit.success", "Deposited %value to your storage.");
        LANG.deposit_error = savedLanguage.getString("deposit.error", "Unable to deposit %value to your storage.");

        //withdraw command
        LANG.withdraw_success = savedLanguage.getString("withdraw.success", "Withdrew %value from your storage.");
        LANG.withdraw_error = savedLanguage.getString("withdraw.error", "Unable to withdraw %value from your storage.");

        //moneyadmin command
        LANG.moneyadmin_b = savedLanguage.getString("moneyadmin.b", "Balance of account %player: %balance");
        LANG.moneyadmin_add_sender = savedLanguage.getString("moneyadmin.add.sender", "Added %value to account %player");
        LANG.moneyadmin_add_target = savedLanguage.getString("moneyadmin.add.target", "Added to your account: %value");
        LANG.moneyadmin_add_error = savedLanguage.getString("moneyadmin.add.error", "Could not add %value to account %player");
        LANG.moneyadmin_rm_sender = savedLanguage.getString("moneyadmin.rm.sender", "Removed %value from account %player");
        LANG.moneyadmin_rm_target = savedLanguage.getString("moneyadmin.rm.target", "Removed from your account: %value");
        LANG.moneyadmin_rm_error = savedLanguage.getString("moneyadmin.rm.error", "Could not remove %value from account %player");

        //gringotts vaults
        LANG.vault_created = savedLanguage.getString("vault.created", "Created vault successfully.");
        LANG.vault_noVaultPerm = savedLanguage.getString("vault.noVaultPerm", "You do not have permission to create vaults here.");
        LANG.vault_error = savedLanguage.getString("vault.error", "Failed to create vault.");

        //towny plugin
        LANG.plugin_towny_noTownVaultPerm = savedLanguage.getString("plugins.towny.noTownPerm", "You do not have permission to create town vaults here.");
        LANG.plugin_towny_noTownResident = savedLanguage.getString("plugins.towny.noTownResident", "Cannot create town vault: You are not resident of a town.");
        LANG.plugin_towny_noNationVaultPerm = savedLanguage.getString("plugins.towny.NoNationVaultPerm", "You do not have permission to create nation vaults here.");
        LANG.plugin_towny_notInNation = savedLanguage.getString("plugins.towny.notInNation", "Cannot create nation vault: You do not belong to a nation.");

        //faction plugin
        LANG.plugin_faction_noVaultPerm = savedLanguage.getString("plugins.faction.noFactionVaultPerm", "You do not have permission to create a faction vault here.");
        LANG.plugin_faction_notInFaction = savedLanguage.getString("plugins.faction.notInFaction", "Cannot create faction vault: You are not in a faction.");

        //vault plugin
        LANG.plugin_vault_insufficientFunds = savedLanguage.getString("plugins.vault.insufficientFunds", "Insufficient funds.");
        LANG.plugin_vault_insufficientSpace = savedLanguage.getString("plugins.vault.insufficientSpace", "Insufficient space.");
        LANG.plugin_vault_error = savedLanguage.getString("plugins.vault.unknownError", "Unknown failure.");
        LANG.plugin_vault_notImplemented = savedLanguage.getString("plugins.vault.notImplemented", "Gringotts does not support banks");
    }
}
