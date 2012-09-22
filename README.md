Gringotts
=========

Gringotts is an economy plugin for the Bukkit Minecraft server platform. Unlike earlier economy plugins such as iConomy, all currency value and transactions is based on actual items in Minecraft, per default emeralds. The goals are to add a greater level of immersion, a generally more Minecraft-like feeling, and in the case of a PvP environment, making the currency itself vulnerable to raiding.

Gringotts was created for the [Minecraft Wars](http://www.minecraft-wars.com/) PvP/survival server.

Features
--------
* Item-backed economy (configurable, default emeralds)
* Storage of currency in chests and other containers
* direct account-to-account
* optional transaction taxes
* fractional currency values (2 decimal digits)
* [Factions](http://dev.bukkit.org/server-mods/factions/) support
* [Vault](http://dev.bukkit.org/server-mods/vault/) integration

Usage
-----
Storing money in an account requires a Gringotts vault. A vault consists of a container, which can be either chest, dispenser or furnace, and a sign above declaring it as a vault. A player or faction may claim any number of vaults. Vaults are not protected from access through other players. If you would like them to be, you may use additional plugins such as [LWC](http://dev.bukkit.org/server-mods/lwc/) or [WorldGuard](http://dev.bukkit.org/server-mods/worldguard/).

### Player vaults ###
Place a sign above a container block, with `[vault]` written on the first line. If it was created correctly, the will display your name on the third line and you will receive a message that the vault has been created.

### Faction vaults ###
Place a sign above a container block, with `[faction vault]` written on the first line. If it was created correctly, the will display your faction's tag on the third line and you will receive a message that the vault has been created.

### User commands ###
    /money
Display your account's current balance.

    /money pay <amount> <player>
Pay an amount to a player. The transaction will only succeed if your account has at least the given amount plus any taxes that apply, and the receiving account has enough capacity for the amount.

### Admin commands ###
    /moneyadmin b <account>
Get the balance of a player's account.

    /moneyadmin add <amount> <account>
Add an amount of money to a player's account.

    /moneyadmin rm <amount>
Remove an amount of money from a player's account.

Installation
------------
Installing Gringotts is as simple as putting gringotts.jar in the _plugins_ subfolder of your CraftBukkit folder. The exact behavior may be customized as described in the Configuration section.

### Caveats ###
Currently using the `reload` command on a running server may cause Gringotts to lose its database connection or even crash CraftBukkit. Please restart the server instead.

Configuration
-------------
As usual with Bukkit plugins, the configuration is in the config.yml in the plugin's directory. A config.yml with the default settings is is created after starting and stopping the server with the plugin for the first time.

### Currency ###
Per default Gringotts uses emeralds as currency, but this can be changed to any other type of item.

Example configuration section:

    currency:
      type: 388
      datavalue: 0
      name:
        singular: Emerald
        plural: Emeralds

This is the default configuration which uses emeralds as currency.

Individual settings:
* `type` The [item id](http://www.minecraftwiki.net/wiki/Data_values#Item_IDs) of the actual item type to use as currency.
* `datavalue` Some items, such as dyes, have different subtypes. To specify the exact item type, set this field appropriately. For example, to use Lapis Lazuli, set `type` to `351` and `datavalue` to `4`.
* `name` Name of the currency to be used in messages to players. Please enter both a singular and plural version.

### Taxes ###
Gringotts supports two types of taxes on transactions done via `/money pay` command: `flat` and `rate`. Flat taxes are a flat amount added to any transaction, while rate adds a percentage of the actual transaction. These can be used individually or combined.

Example configuration section:

    transactiontax:
      flat: 1.0
      rate: 0.05

This would add to every transaction 1 plus 5% of the transaction value. For instance, if you had issued the command `/money pay 200 notch` it would remove 211 emeralds from your account, and add 200 emeralds to notch's account.

### Permissions ###
**TODO**

Development
-----------
This section is intended to help out developers who wish to make changes to Gringotts themselves. If you have any changes that you would like included in the main branch, please submit a pull request.

Gringotts uses the [Maven 3](http://maven.apache.org/ref/3.0/) build system. To obtain a working plugin jar, build with the command

    mvn compile assembly:single
    
This should put a jar with the required dependencies in the _target_ subdirectory