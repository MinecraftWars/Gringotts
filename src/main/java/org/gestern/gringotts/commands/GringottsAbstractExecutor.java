package org.gestern.gringotts.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.Permissions;
import org.gestern.gringotts.api.*;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.COMMAND_DEPOSIT;
import static org.gestern.gringotts.Permissions.COMMAND_WITHDRAW;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

public abstract class GringottsAbstractExecutor implements CommandExecutor {
    static final String TAG_BALANCE = "%balance";

    static final String TAG_PLAYER = "%player";

    static final String TAG_VALUE = "%value";

    final Gringotts plugin = Gringotts.getInstance();
    final Eco eco = plugin.getEco();

    static void invalidAccount(CommandSender sender, String accountName) {
        sender.sendMessage(LANG.invalid_account.replace(TAG_PLAYER, accountName));
    }

    boolean pay(Player player, double value, String[] args) {
        if (!Permissions.TRANSFER.allowed(player)) {
            player.sendMessage(LANG.noperm);

            return true;
        }

        String recipientName = args[2];

        Account from = eco.player(player.getUniqueId());
        Account to = eco.account(recipientName);

        PlayerAccount playerAccount = eco.player(player.getUniqueId());

        TaxedTransaction transaction = playerAccount.send(value).withTaxes();
        TransactionResult result = playerAccount.send(value).withTaxes().to(eco.player(recipientName));

        double tax = transaction.tax();
        double valueAdded = value + tax;

        String formattedBalance = eco.currency().format(from.balance());
        String formattedValue = eco.currency().format(value);
        String formattedValuePlusTax = eco.currency().format(valueAdded);
        String formattedTax = eco.currency().format(tax);

        switch (result) {
            case SUCCESS:
                String succTaxMessage = LANG.pay_success_tax.replace(TAG_VALUE, formattedTax);
                String succSentMessage = LANG.pay_success_sender.replace(TAG_VALUE, formattedValue).replace
                        (TAG_PLAYER, recipientName);
                from.message(succSentMessage + (tax > 0 ? succTaxMessage : ""));
                String succReceivedMessage = LANG.pay_success_target.replace(TAG_VALUE, formattedValue).replace
                        (TAG_PLAYER, player.getName());
                to.message(succReceivedMessage);
                return true;
            case INSUFFICIENT_FUNDS:
                String insFMessage = LANG.pay_insufficientFunds.replace(TAG_BALANCE, formattedBalance).replace
                        (TAG_VALUE, formattedValuePlusTax);
                from.message(insFMessage);
                return true;
            case INSUFFICIENT_SPACE:
                String insSSentMessage = LANG.pay_insS_sender.replace(TAG_PLAYER, recipientName).replace(TAG_VALUE,
                        formattedValue);
                from.message(insSSentMessage);
                String insSReceiveMessage = LANG.pay_insS_target.replace(TAG_PLAYER, from.id()).replace(TAG_VALUE,
                        formattedValue);
                to.message(insSReceiveMessage);
                return true;
            default:
                String error = LANG.pay_error.replace(TAG_VALUE, formattedValue).replace(TAG_PLAYER, recipientName);
                from.message(error);
                return true;
        }
    }

    void deposit(Player player, double value) {

        if (COMMAND_DEPOSIT.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).deposit(value);
            String formattedValue = eco.currency().format(value);

            if (result == SUCCESS) {
                String success = LANG.deposit_success.replace(TAG_VALUE, formattedValue);
                player.sendMessage(success);
            } else {
                String error = LANG.deposit_error.replace(TAG_VALUE, formattedValue);
                player.sendMessage(error);
            }
        }
    }

    void withdraw(Player player, double value) {
        if (COMMAND_WITHDRAW.allowed(player)) {
            TransactionResult result = eco.player(player.getUniqueId()).withdraw(value);
            String formattedValue = eco.currency().format(value);
            if (result == SUCCESS) {
                String success = LANG.withdraw_success.replace(TAG_VALUE, formattedValue);
                player.sendMessage(success);
            } else {
                String error = LANG.withdraw_error.replace(TAG_VALUE, formattedValue);
                player.sendMessage(error);
            }
        }
    }

    void balanceMessage(Account account) {

        account.message(LANG.balance.replace(TAG_BALANCE, eco.currency().format(account.balance())));

        if (Configuration.CONF.balanceShowVault) {
            account.message(LANG.vault_balance.replace(TAG_BALANCE, eco.currency().format(account.vaultBalance())));
        }

        if (Configuration.CONF.balanceShowInventory) {
            account.message(LANG.inv_balance.replace(TAG_BALANCE, eco.currency().format(account.invBalance())));
        }
    }
}
