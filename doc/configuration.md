Configuration, Permissions, Localization
========================================

Configuration
-------------
As usual with Bukkit plugins, the configuration is in the config.yml in the plugin's directory. A config.yml with the default settings is is created after starting and stopping the server with the plugin for the first time.

Please refer to the [default config.yml](https://github.com/MinecraftWars/Gringotts/blob/master/config.yml) for a complete example.

### Tekkit quick-start ###

To run Gringotts on a Tekkit server, set `currency.denominations` to use only 1.2.5-compatible items (eg gold ingots, id 266) or Tekkit items (eg industrial credits, id 30186), and set usevault.enderchest to `false`.

### Language ###
The `language` option allows you to set one of Gringotts' supported language for interaction messages with players. Currently supported options are `custom` (default/english) and `de` (German). See below on how to modify custom messages to your preferences.

### Currency ###
Per default Gringotts uses emeralds as currency, but this can be changed to any other type of item.

Example configuration section:

    currency:
      name:
        singular: Emerald
        plural: Emeralds
      digits: 2
      denominations:
        388: 1
        133: 9


This is the default configuration which uses emeralds as currency, with emeralds having value 1, and emerald blocks value 9.

#### Individual settings

* `name` Name of the currency to be used in messages to players. Please enter both a singular and plural version.
* `fractional` Whether to allow fractional values. `true` or `false`
* `digits` Decimal digits used in representation and calculation of the currency. Set this to 0 to use only whole number values.
* `denominations` A key-value map, defining the [item id](http://www.minecraftwiki.net/wiki/Data_values#Item_IDs) of the actual item type to use as currency, and the value of the item. The keys may be either a single number for the item id, or two numbers in the format "id;damage", where "damage" is the damage aka data value of the item. The value can be a whole or fractional number denoting the value of a denomination. However the number of fractional digits in a currency value should not exceed the number defined as `digits`.

#### Example denomination setup with fractional values and items with data values

The following setup shows how to specify a currency with Lapis Lazuli as minor denomination with a value of 0.05, Skeleton Heads with a value of 10 and Creeper Heads with a value of 60:

    denominations:
      351;4: 0.05
      397: 10
      397;4: 60

### Taxes ###

Gringotts supports two types of taxes on transactions done via `/money pay` command: `flat` and `rate`. Flat taxes are a flat amount added to any transaction, while rate adds a percentage of the actual transaction. These can be used individually or combined.

Example configuration section:

    transactiontax:
      flat: 1.0
      rate: 0.05

This would add to every transaction 1 plus 5% of the transaction value. For instance, if you had issued the command `/money pay 200 notch` it would remove 211 emeralds from your account, and add 200 emeralds to notch's account.

### Misc ###

	startingbalance:
	  player: 0
	  faction: 0
	  town: 0
	  nation: 0
	
Amount of virtual money to gift to players on first join, or accounts with other plugins upon creation. This money may be spent as usual, but will not be backed by physical currency. Enable these if you want your players/factions/etc. to start with some money that can't be lost or stolen.  

    usevault:
      container: true
      enderchest: true

Globally enable use of specific kinds of vault:
* `container` Enable the use of container vaults: chests, dispensers and furnaces. If this is `false`, only player's inventory and/or enderchests will serve as a player "vault".
* `enderchest` Enable use of enderchest as vault for players globally. The permission `gringotts.usevault.enderchest` may still be used to disable this on a per-player/world basis.


Localization and message customization
--------------------------------------
On first start of Gringotts, a `messages.yml` file will be written to the Gringotts plugin folder.
You can freely edit the available strings and also include [text formatting (color) codes](http://minecraft.gamepedia.com/Formatting_codes).

Some messages contain variables, for example `%player` for the player's name or `%value` for the amount transferred in a transaction. It is important not to change or translate these variable names. They may only be used in messages where they are already present in the original version, but it is safe to omit them from a custom message.


Permissions
-----------

### Vault creation

    gringotts.createvault:
      default: true

Allow players to create any type of vault.

    gringotts.createvault.admin:
      default: op

Allow players to create vaults for other players.

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

    gringotts.createvault.worldguard:
      default: true

Allow players to create vaults for WorldGuard regions they are member of.

### Vault usage

    gringotts.usevault
      default: true

Use player inventory and player's enderchest as vault when player is online.

    gringotts.usevault.inventory
      default: true

Use player inventory as vault when player is online.

    gringotts.usevault.enderchest
      default: true

### User commands

Use Gringotts commands.

    gringotts.command
      default: true

Allow transfer command (pay)

    gringotts.transfer:
      default: true

Allow withdrawal of money from chest storage to inventory via `/money withdraw`.
    
    gringotts.command.withdraw:
      default: true
      
Allow deposit of money to chest storage from inventory via `/money deposit`.
    
    gringotts.command.deposit:
      default: true

### Admin permissions

Allow players to transfer money to other accounts via `/money pay`

    gringotts.admin:
      default: op

Allow use of all `/moneyadmin` commands
