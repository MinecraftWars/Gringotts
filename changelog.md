Gringotts Changelog
===================

Pending Changes
-------

> The following changes are not yet generally available, or only in SNAPSHOT form, and are pending a tagged release.

#### Dependency Updates
* Update bStats (1.8), Towny (0.96.7.0), WorldEdit (7.2.2), and WorldGuard (7.0.4) \[PR [225][225]\]
* Upgrade from JUnit4 to JUnit5 (JUnit Jupiter; 5.7.1) \[PR [225][225]\]
* Remove zno.de repository and deployment scheme - Would run into HTTP 502 errors. \[PR [225][225]\]

#### Fixes
* Changed over to use Towny's new economy APIs. Breaks backwards compatibility of Towny versions. \[PR [225][225]\]
  * In addition to updating Towny from 0.90.0.0 (which is _old_), most issues related to Towny should be resolved. \[PR [225][225]\]

#### Deprecation & Removals
* Removed Migration class: Has no ill effect on a modern codebase, or servers since prior to [v2.10.1](changelog.md#v2.10.1) \[PR [225][225]\]
* Removed Apache Derby references, as well as DerbyDAO \[PR [225][225]\]
* Remove `Funding` file: Not enabled on this repository, repo has no active maintainers. \[PR [225][225]\]

#### Documentation
* Add Changelog entries for versions 2.10, 2.10.1, 2.11.0, and 2.11.1; Add `Pending Changes` entry. \[PR [225][225]\]
* Rewrite the `Maven/Gradle repo` section as `Depending on Gringotts` in the README \[PR [225][225]\]
* Delimit LICENSE.txt to 80 characters in length. \[PR [225][225]\]

<!--- References --->
[225]: ttps://github.com/MinecraftWars/Gringotts/pull/# "Pending PR"


v2.11.1
-------

#### Dependency Updates
* Bump JUnit4 (4.13.1)

#### Fixes
* Fix Issues [#193][193] and [#197][197] with [Pull Request 206][206], where pay would always send `0`.

#### Continuous Deployment
* Update TravisCI configuration to use OpenJDK 8 instead of OracleJDK 8 \[PR [207][207]\]

<!--- References --->
[193]: https://github.com/MinecraftWars/Gringotts/issues/193 "2.11 snapshot not sending or receiving money"
[197]: https://github.com/MinecraftWars/Gringotts/issues/197 "Issue with /money pay 100 <playername>"
[206]: https://github.com/MinecraftWars/Gringotts/pull/206 "Fix pay command always sending 0."
[207]: https://github.com/MinecraftWars/Gringotts/pull/207 "Update Travis to use openjdk8"

v2.11.0
-------

#### Dependency Updates
  * Update Maven Plugins \[PR [192][192]\]
  * Switch from Bukkit to the SpigotAPI (1.14.1) \[PR [202][202]\]

#### Fixes
  * Added barrels to validated storage blocks, fix Sign validation. \[PR [202][202]\]

#### Documentation
  * Add JitPack badge as well as quickstart instructions for the Gradle and Maven build tools. \[PR [194][194]\]
  * Created `.github/FUNDING.yml` - Added nikosgram's information. \[PR [202][202]\]
  * Set Jekyll Theme to `jekyll-theme-slate` \[PR [202][202]\]

<!--- References --->
[192]: https://github.com/MinecraftWars/Gringotts/pull/192 "Update version and maven plugins"
[194]: https://github.com/MinecraftWars/Gringotts/pull/194 "Update README.md"
[202]: https://github.com/MinecraftWars/Gringotts/pull/202 "Minecraft 1.14"

v2.10.1
-------
#### Dependency Updates
  * Update Bukkit (1.13) and set api-version to 1.13 \[PR [179][179]\]
  * Update VaultAPI (1.7) \[PR [182][182]\]
  * Update WorldGuard (7.0.0) \[PRs [189][189], [190][190]\]

#### Fixes
  * Fix unitName method issues that couldn't identify material names \[PR [172][172]\]
  * Fixed some mistaken schematics \[PR [172][172]\]
  * Fix Issues with Vault 1.7.x \[PR [182][182]\]
  * Fix crashing edge case with Factions forks (SavageFactions, in particular), by testing against
    TerritoryAccess for dependency recognition. \[PR [188][188]\]
  * General Code Cleanup, Refactoring, and Restructuring \[PR [190][190]\]
    * Fix hook failure message to log as a warning (was standard / informational)
    * Removed duplicated error messages.

#### Additions
  * Replaced MCStats with bStats \[PR [190][190]\]
  * API: [Changes to API from PR 190][190]
    * Added `org.gestern.gringotts.commands` package to house refactored command framework.
    * Added various accessor methods for privatized fields.
    * Added Type enumeration for `VaultCreationEvent`, replacing String comparisons.
    * Implement `TownyHandler.sendMessage(String)`
    * Overhaul `WorldGuardHandler`

#### Documentation (See PR [191])
  * Update `docs/usage.md`
    * Display `User commands` in table format.
  * Update `README.md`
    * Point to the SpigotMC Resource page, in addition to Bukkit Dev listing.
    * Direct command usage to the usage documentation.
    * Capitalize list entries.

#### Attributions
  * Add "nikosgram13" to list of authors in `plugin.yml` \[PR [172][172]\]

<!--- References --->
[172]: https://github.com/MinecraftWars/Gringotts/pull/172 "Update to v2.10.1-SNAPSHOT"
[179]: https://github.com/MinecraftWars/Gringotts/pull/179 "Update to 1.13"
[182]: https://github.com/MinecraftWars/Gringotts/pull/182 "Fix Vault 1.7 issues"
[188]: https://github.com/MinecraftWars/Gringots/pull/188 "Provide better support for detecting Factions"
[189]: https://github.com/MinecraftWars/Gringotts/pull/189 "Implement support for WorldGuard 7.0.0 only dropping support for pre 1.13"
[190]: https://github.com/MinecraftWars/Gringotts/pull/190 "Code cleanup and refactor"
[191]: https://github.com/MinecraftWars/Gringotts/pull/191 "Changed command display style to table in .md files"


v2.10
-----

#### Dependency Updates
  * Update Bukkit (1.12), Vault (1.6) and WorldGuard (6.2) \[PR [162][162]\]
  * Update Factions (2.12.0) and code restyling \[PR [163][163]\]

<!--- References --->
[162]: https://github.com/MinecraftWars/Gringotts/pull/162 "Update for Bukkit's version 1.12-R0.1-SNAPSHOT"
[163]: https://github.com/MinecraftWars/Gringotts/pull/163 "Support the latest version of Factions"

v2.9
----
* DEPENDS ON JAVA 8. If your server is not on Java 8 yet, you should upgrade, or ask your service provider to upgrade. Java 7 isn't supported anymore by Oracle.
* Should now work both in regular as well as async context. This means there should be no more "Asynchronous entity world add" errors anymore, and Gringotts is now compatible with Towny in async mode, as well as other plugins that call economy methods asynchronously.
* The maximum virtual money allowed for an account is now equal to the *lowest* denomination, instead of the highest. I changed this because that behavior was causing lots of confusion.
* fixed other minor bugs

v2.8
----
* built for Minecraft 1.9 and Factions 2.8. May not be compatible with older Factions versions!
* unsupported versions of Factions should be handled more gracefully now.
* updated to current version of Metrics-Lite. Fixes annoying error messages.

v2.7
----
* Named denominations: Each denomination can now have its own name, which will be shown in /balance messages and the like. To activate this feature, use the `named-denominations` config setting. By default this will use the denomination's displayname or regular item name, but you can define a custom name as well. See [configuration instructions](https://github.com/MinecraftWars/Gringotts/blob/master/doc/configuration.md) for details.
* Balance messages can now be configured to show only regular balance or also vault/inventory balances. 
* Now also supports `&` prefix to color codes in messages and item displayname/lore in addition to the default `§` prefix.
* can now also used Vault-supported item names in denomination config, not only regular Bukkit names. This can make the config a bit more intuitive. (requires Vault)
* lore config for denomination items now also allows a simple string instead of a string list.
* configuration should now always be loaded as UTF8.
* fixed an issue with `/money` commands failing on some servers.

v2.6
----
* adds display name and lore support for currency items. See [configuration instructions](https://github.com/MinecraftWars/Gringotts/blob/master/doc/configuration.md) on how to configure this.
  Thanks to dj5566 for contributing this feature!
* `startingbalance` config option works again

v2.5
----
* fix some currency requests causing internet accesses and thus lag spikes. Should fix some problems people have been having with Towny
* /money now shows money in vaults and money in inventory in addition to total balance

v2.4
----
* updates to dependency versions:
    - Bukkit API 1.8.3-R0.1
    - Vault API 1.5
    - Factions / MassiveCore 2.7.5 (incompatible with older Factions versions)
    - Towny 0.89.2.0
    - WorldGuard 6.0 beta
* minor fixes in message formatting

v2.3
----
* supports player UUIDs
* old Derby databases will be automatically migrated to the Bukkit-internal database
* player names in the database will be automatically migrated to UUIDs
* built against Bukkit 1.7.9, Vault 1.4, Java 7. 
  This version of Gringotts will no longer work with older versions of Bukkit, Vault and Java.
* vaults can now be created from most containers (chest, trapped chest, hopper, dropper, dispenser, furnace)
* fix a bug preventing creation of vaults when the db still contained vaults in deleted worlds
* fix a bug allowing to create multiple vaults on a single container

v2.2
----
* use factions 2.x
* build against bukkit 1.7.2

v2.0 beta5
----------
* fix NPE with trying to get a chest from a null block
* fix NPE when you delete a world with chests in it

v2.0 beta4
----------
* fix vault creation breaking under some circumstances involving Towny
* added support for built-in Bukkit database support. This means that the Derby library is no longer required. If the Derby jar is present, Gringotts will continue to use it, otherwise a DB will be created via the Bukkit API. This should also support MySQL if you configure it in bukkit.yml. Please see the [http://wiki.bukkit.org/Bukkit.yml#database](Bukkit Wiki) for details.

v2.0 beta3
----------
* fix bug where Towny vaults would get deactivated on creation of other chests or deposits
* update Vault connector to Vault v 1.2.24 -- this version of Gringotts may be incompatible with older versions of Vault

v2.0 beta2
----------
* ops/players with permission `createvault.forothers` can now create vaults for other players and other accounts by creating the vault as usual, but adding the owner's name/id on the thord line of the sign.
* fix bug with WorldGuard vault creation permissions

v2.0 beta1
----------
* can create a vault now by attaching the [vault] sign to a chest directly (shift+rightclick with a sign), instead of requiring it to be above the chest
* added string customization / internationalization for messages to players (contributed by KanaYamamoto/Daenara)
* German translation (contributed by KanaYamamoto/Daenara)
* accounts can have a purely virtual starting balance (contributed by bezeek)
* support for WorldGuard region vaults. Region owners may create a `[region vault]` with the id of the region on the third line.
* /moneyadmin command now supports optional account type parameter. example: `/moneyadmin b foo faction`. This is primariliy intended for plugins that use worldguard for region management, as there are no player commands to handle these accounts.
* for devs: added an event system for vault creation. Gringotts triggers a `PlayerVaultCreationEvent` when a player attempts to create a vault in the world. Plugins can register an event handler and supply their information for Gringotts to create their own vault types.

v1.5.1a
-------
* fix Vault connector not handling requests properly for non-player accounts
* fix vaults with bold marked signs getting destroyed at any transaction

v1.5.0
------
* add commands `/money withdraw` and `/money deposit` to withdraw money from vaults to inventory and vice versa.
* new alias for `/money`: `/m`
* fix issues with `/gringotts reload` not affecting all operations in Gringotts
* [vault] line is marked in bold letters now after successful creation
* for devs: an API for Gringotts transactions. Currently only documented per Javadoc in source and incomplete.

v1.4.2
------
* fix enderchest content being included in balance despite being disabled in config
* minor fix to maintain Tekkit compatibility

v1.4.1b
------
* fix a dependency loop with factions and possible dependency issues

v1.4.0
------
* support multiple denominations. New installations will use emeralds and emerald blocks by default. See configuration on how to set them up. Old installations will retain their configuration. 
* denominations can now use the "damage" value of item types.
* removed capacity methods. With multiple denominations a single capacity number cannot always be defined.
* configuration option `currency.fractional` deprecated in favor or `currency.digits`, which specifies the amount of decimal digits to use, and consequently the smallest possible currency value.
* Gringotts registers its own Vault compatible Economy service provider now. Minor changes in functionality exposed to Vault (supports configured decimal digits)
* lots of internal changes you needn't worry about

v1.3.2
------
* fix enderchest config option
* some internal restructuring

v1.3.1
------
* Tekkit/1.2.5 compatible  (configuration required)
* added configuration option `usevault.enderchest`: globally enable/disable enderchest as valid vault. This can also be done on a per-player/world basis with the permission `gringotts.usevault.enderchest`
* fix players getting kicked on breaking a vault sign under some circumstances
* fix money transfers not allowing fractional values despite fractional values being enabled
* fix money not being deducted from account with fractional values disabled
* fix adding money not calculating capacity correctly
* check version of plugin dependency and emit warning if they are not supported
* some touch-ups to /money command responses
* new command: `/moneyadmin c <user>` show capacity of a user's account

v1.3.0
------
* added Towny support: can now create town and nation vaults when Towny plugin is installed and you are member of a town or nation
* permissions for creating town vault: `gringotts.createvault.town` and nation vault: `gringotts.createvault.nation`
* added plugin metrics

v1.2.1
------
* fix bug in currency configuration

v1.2.0
------
* use player inventory as "vault", on by default, permission `gringotts.usevault.inventory`
* use enderchests as vault, on by default, permission `gringotts.usevault.enderchest`
* toggle using containers vaults: configuration option `usevault.container`
* make fractional values optional: configuration option `currency.fractional`
* new command `/gringotts reload` reloads configuration from plugins `config.yml`
* minor bugfixes

v1.1.0
------
* New permissions: `gringotts.createvault`
* Bugfixes concerning vault destruction and disbanded factions

v1.0.1
------
* Minor bugfixes
* Can build without dependencies, and include them in `craftbukkit/lib` directory

v1.0
----
* First "official" release.