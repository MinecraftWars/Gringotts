Usage
=====

Storing money in an account requires a Gringotts vault. A vault consists of a container, which can be either chest, dispenser or furnace, and a sign above or on it declaring it as a vault. A player or faction may claim any number of vaults. Vaults are not protected from access through other players. If you would like them to be, you may use additional plugins such as [LWC](http://dev.bukkit.org/server-mods/lwc/) or [WorldGuard](http://dev.bukkit.org/server-mods/worldguard/).

Vaults
------

Gringotts supports vaults for players as well as other plugins: currently Factions, Towny and WorldGuard. All vaults are created by placing a sign onto or above a container and writing the vault type on first line the sign. When a vault is successfully created, you will receive a message.

### Player vaults ###

First line: `[vault]`
Third line: will display your name on successful creation.

### Faction vaults ###

First line: `[faction vault]`
Third line: will display your faction's tag on successful creation.

### Towny vaults ###

* Town vaults first line: `[town vault]`
* Nation vaults first line: `[nation vault]`

If it was created correctly, the sign will display your town's or nation's tag on the third line.

### WorldGuard vaults ###

First line: `[region vault]`
Third line: region's id

Writing the region id manually is required because a player may be part of several regions. If the vault has been created correctly, you will receive a message that the vault has been created.

### Creating vaults for other players/accounts ###

The permission `createvault.forothers` allows you to create vaults for other players or factions, towns, etc. that do not belong to you. To create a vault for others, write the appropriate vault type on the first line and the designated owner on the third line (player, faction tag, town name). 

Commands
--------

### User commands ###

    /money
Display your account's current balance. Alias `/m`

    /money pay <amount> <player>
Pay an amount to a player. The transaction will only succeed if your account has at least the given amount plus any taxes that apply, and the receiving account has enough capacity for the amount.

    /money withdraw <amount>
Withdraw an amount from chest storage into inventory.

    /money deposit <amount>
Deposit an amount from inventory into chest storage. 


### Admin commands ###

    /moneyadmin b <account> [type]
Get the balance of a player's account. Optional `type` parameter specifies a specific account type for disambiguation. Valid `type` arguments are `faction`, `town`, `nation`, `worldguard`

    /moneyadmin add <amount> <account>
Add an amount of money to a player's account.

    /moneyadmin rm <amount>
Remove an amount of money from a player's account.

    /gringotts reload
Reload Gringotts config.yml and messages.yml and apply any changed settings.
