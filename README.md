Gringotts
=========

Gringotts is an item-based economy plugin for the Bukkit Minecraft server platform. Unlike earlier economy plugins such as iConomy, all currency value and money transactions are based on actual items in Minecraft, per default emeralds. The goals are to add a greater level of immersion, a generally more Minecraft-like feeling, and in the case of a PvP environment, making the currency itself vulnerable to raiding.

Gringotts was created for the [Minecraft Wars](http://www.minecraft-wars.com/) PvP/survival server.

[Get Gringotts from BukkitDev](http://dev.bukkit.org/server-mods/gringotts/)

Features
--------
* Item-backed economy (configurable, default emeralds)
* Storage of currency in chests and other containers, player inventory and ender chests (configurable)
* direct account-to-account transfers
* optional transaction taxes
* fractional currency values (2 decimal digits)
* [Factions](http://dev.bukkit.org/server-mods/factions/) support
* [Towny](http://dev.bukkit.org/server-mods/towny-advanced/) support
* [Vault](http://dev.bukkit.org/server-mods/vault/) integration
* Tekkit / 1.2.5 compatible

Usage
-----
Storing money in an account requires a Gringotts vault. A vault consists of a container, which can be either chest, dispenser or furnace, and a sign above declaring it as a vault. A player or faction may claim any number of vaults. Vaults are not protected from access through other players. If you would like them to be, you may use additional plugins such as [LWC](http://dev.bukkit.org/server-mods/lwc/) or [WorldGuard](http://dev.bukkit.org/server-mods/worldguard/).

### Player vaults ###
Place a sign above a container block, with `[vault]` written on the first line. If it was created correctly, the sign will display your name on the third line and you will receive a message that the vault has been created.

### Faction vaults ###
Place a sign above a container block, with `[faction vault]` written on the first line. If it was created correctly, the sign will display your faction's tag on the third line and you will receive a message that the vault has been created.

### Towny vaults ###

* To make a vault for your town: place a sign above a container block, with `[town vault]` written on the first line. 
* To make a vault for your nation: place a sign above a container block, with `[nation vault]` written on the first line. 
If it was created correctly, the sign will display your town's or nation's tag on the third line and you will receive a message that the vault has been created.


### User commands ###
    /money
Display your account's current balance.

    /money pay <amount> <player>
Pay an amount to a player. The transaction will only succeed if your account has at least the given amount plus any taxes that apply, and the receiving account has enough capacity for the amount.

### Admin commands ###
    /moneyadmin b <account>
Get the balance of a player's account.

    /moneyadmin c <account>
Get the available capacity of a player's account.

    /moneyadmin add <amount> <account>
Add an amount of money to a player's account.

    /moneyadmin rm <amount>
Remove an amount of money from a player's account.

    /gringotts reload
Reload Gringotts config.yml and apply any changed settings.

Installation
------------
* download [Gringotts v1.3.0](http://dev.bukkit.org/media/files/642/288/Gringotts-1.3.0.jar) and place it in your craftbukkit/plugins folder
* download [Apache Derby](http://repo1.maven.org/maven2/org/apache/derby/derby/10.9.1.0/derby-10.9.1.0.jar) and place it in your craftbukkit/lib folder



Configuration
-------------
As usual with Bukkit plugins, the configuration is in the config.yml in the plugin's directory. A config.yml with the default settings is is created after starting and stopping the server with the plugin for the first time.

### Currency ###
Per default Gringotts uses emeralds as currency, but this can be changed to any other type of item.
To use Gringotts with Tekkit, you will need to adjust this to a 1.2.5-compatible item (eg gold ingots, id 266) or a Tekkit item (eg industrial credits, id 30186)

Example configuration section:

    currency:
      type: 388
      datavalue: 0
      name:
        singular: Emerald
        plural: Emeralds
      fractional: true


#### Individual settings

* `type` The [item id](http://www.minecraftwiki.net/wiki/Data_values#Item_IDs) of the actual item type to use as currency.
* `datavalue` Some items, such as dyes, have different subtypes. To specify the exact item type, set this field appropriately. For example, to use Lapis Lazuli, set `type` to `351` and `datavalue` to `4`.
* `name` Name of the currency to be used in messages to players. Please enter both a singular and plural version.
* `fractional` Whether to allow fractional values. `true` or `false`

### Taxes ###

Gringotts supports two types of taxes on transactions done via `/money pay` command: `flat` and `rate`. Flat taxes are a flat amount added to any transaction, while rate adds a percentage of the actual transaction. These can be used individually or combined.

Example configuration section:

    transactiontax:
      flat: 1.0
      rate: 0.05

This would add to every transaction 1 plus 5% of the transaction value. For instance, if you had issued the command `/money pay 200 notch` it would remove 211 emeralds from your account, and add 200 emeralds to notch's account.

### Misc ###

    usevault:
      container: true
      enderchest: true

Globally enable use of specific kinds of vault:
* `container` Enable the use of container vaults: chests, dispensers and furnaces. If this is `false`, only player's inventory and/or enderchests will serve as a player "vault".
* `enderchest` Enable use of enderchest as vault for players globally. The permission `gringotts.usevault.enderchest` may still be used to disable this on a per-player/world basis.


Permissions
-----------

    gringotts.createvault:
      default: true

Allow players to create any type of vault.

    gringotts.createvault.player:
      default: true

Allow players to create vaults for their own account.

    gringotts.createvault.faction:
      default: true

Allow players to create vaults for their faction (Factions only).

    gringotts.createvault.town:
      default: true

Allow players to create vaults for their town (Towny only).

    gringotts.createvault.nation:
      default: true

Allow players to create vaults for their nation (Towny only).

    gringotts.usevault
      default: true

Use player inventory and player's enderchest as vault when player is online.

    gringotts.usevault.inventory
      default: true

Use player inventory as vault when player is online.

    gringotts.usevault.enderchest
      default:true

Use player's enderchest as vault when player is online.

    gringotts.transfer:
      default: true

Allow players to transfer money to other accounts via `/money pay`

    gringotts.admin:
      default: op

Allow use of all `/moneyadmin` commands


Development
-----------
This section is intended to help out developers who wish to make changes to Gringotts themselves. If you have any changes that you would like included in the main branch, please submit a pull request.

Gringotts uses the [Maven 3](http://maven.apache.org/) build system. To obtain a working plugin jar that includes all dependencies, build with the command

    mvn compile assembly:single
    
This should put a jar with the required dependencies in the _target_ subdirectory

For a build with only basic dependencies (plugin metrics), use

    mvn compile package


License
-------
All code within Gringotts is licensed under the BSD 2-clause license. See `license.txt` for details.

The jar with dependencies includes Apache Derby, which is licensed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
