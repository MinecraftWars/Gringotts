Gringotts Changelog
===================

v1.4.0
------
* support multiple denominations. New installations will use emeralds and emerald blocks by default.
* removed capacity methods. With multiple denominations a single capacity cannot always be defined.


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
* New permissions: `gringotts.createvault`, `gringotts.createvault`
* Bugfixes concerning vault destruction and disbanded factions

v1.0.1
------
* Minor bugfixes
* Can build without dependencies, and include them in `craftbukkit/lib` directory

v1.0
----
* First "official" release.