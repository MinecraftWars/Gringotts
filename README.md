Gringotts
=========

Gringotts is an item-based economy plugin for the Bukkit Minecraft server platform. Unlike earlier economy plugins such as iConomy, all currency value and money transactions are based on actual items in Minecraft, per default emeralds. The goals are to add a greater level of immersion, a generally more Minecraft-like feeling, and in the case of a PvP environment, making the currency itself vulnerable to raiding.

Gringotts was created for the [Minecraft Wars](http://www.minecraft-wars.com/) PvP/survival server.

[Get Gringotts from BukkitDev](http://dev.bukkit.org/server-mods/gringotts/)

Features
--------
* Item-backed economy (configurable, default emeralds)
* multiple denominations with automatic conversion (for example, use emeralds and emerald blocks)
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

    /moneyadmin add <amount> <account>
Add an amount of money to a player's account.

    /moneyadmin rm <amount>
Remove an amount of money from a player's account.

    /gringotts reload
Reload Gringotts config.yml and apply any changed settings.


Installation and Configuration
------------------------------
* download [Gringotts](http://dev.bukkit.org/server-mods/gringotts/files/) and place it in your craftbukkit/plugins folder
* download [Apache Derby](http://repo1.maven.org/maven2/org/apache/derby/derby/10.9.1.0/derby-10.9.1.0.jar) and place it in your craftbukkit/lib folder

Please see the [Configuration and Permissions](https://github.com/MinecraftWars/Gringotts/blob/master/doc/configuration.md) document on how to configure Gringotts.


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
