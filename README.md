Gringotts
=========

[![Join the chat at https://gitter.im/MinecraftWars/Gringotts](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/MinecraftWars/Gringotts?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/MinecraftWars/Gringotts.svg?branch=master)](https://travis-ci.org/MinecraftWars/Gringotts)
[![](https://jitpack.io/v/MinecraftWars/Gringotts.svg)](https://jitpack.io/#MinecraftWars/Gringotts)

Gringotts is an item-based economy plugin for the Bukkit Minecraft server platform. Unlike earlier economy plugins, all currency value and money transactions are based on actual items in Minecraft, per default emeralds. The goals are to add a greater level of immersion, a generally more Minecraft-like feeling, and in the case of a PvP environment, making the currency itself vulnerable to raiding.


[Get Gringotts from BukkitDev](https://dev.bukkit.org/projects/gringotts) or
[get Gringotts from Spigot](https://www.spigotmc.org/resources/gringotts.42071/)!

Looking for maintainers!
------------------------

I initially created Gringotts a long time ago for a server that no longer exists, and I don't play Minecraft much anymore,
and have many other interesting things to do. Thus, I can't devote much time to the project. I would like to hand the 
project off to somebody interested in its continued existence.

### How to become a maintainer? ###

1. Open a pull request with some kind of update for Gringotts: 
      * update for a new version of Bukkit
      * add a feature
      * fix a bug
      * update documentation
2. Say that you would like to be a maintainer.
3. If the PR is reasonable, I will give you commit access to the GitHub repository and the bukkitdev project. 

Features
--------
* Item-backed economy (configurable, default emeralds)
* Multiple denominations with automatic conversion (for example, use emeralds and emerald blocks)
* Storage of currency in chests and other containers, player inventory and ender chests (configurable)
* Direct account-to-account transfers commands
* Optional transaction taxes
* Fractional currency values (fixed decimal digits)
* Account support for [Factions](http://dev.bukkit.org/server-mods/factions/), [Towny](http://dev.bukkit.org/server-mods/towny-advanced/) and [WorldGuard](http://dev.bukkit.org/server-mods/worldguard/)
* [Vault](http://dev.bukkit.org/server-mods/vault/) integration

Usage
-----
Storing money in an account requires a Gringotts vault. A vault consists of a container, which can be either chest, dispenser or furnace, and a sign above or on it declaring it as a vault. A player or faction may claim any number of vaults. Vaults are not protected from access through other players. If you would like them to be, you may use additional plugins such as [LWC](http://dev.bukkit.org/server-mods/lwc/) or [WorldGuard](http://dev.bukkit.org/server-mods/worldguard/).

For full usage documentation, please see [the usage page](https://github.com/MinecraftWars/Gringotts/blob/master/doc/usage.md)

### Player vaults ###

Place a sign above a container block, with `[vault]` written on the first line. If it was created correctly, the sign will display your name on the third line and you will receive a message that the vault has been created.

### Faction vaults ###

Place a sign above a container block, with `[faction vault]` written on the first line. If it was created correctly, the sign will display your faction's tag on the third line and you will receive a message that the vault has been created.

### Towny vaults ###

* To make a vault for your town: place a sign above a container block, with `[town vault]` written on the first line. 
* To make a vault for your nation: place a sign above a container block, with `[nation vault]` written on the first line. 
If it was created correctly, the sign will display your town's or nation's tag on the third line and you will receive a message that the vault has been created.

Commands
--------
See [Usage](https://github.com/MinecraftWars/Gringotts/blob/master/doc/usage.md#commands).

Installation and Configuration
------------------------------
Download [Gringotts](http://dev.bukkit.org/server-mods/gringotts/files/) and place it in your craftbukkit/plugins folder

Please see the [Configuration and Permissions](https://github.com/MinecraftWars/Gringotts/blob/master/doc/configuration.md) document on how to configure Gringotts.

Problems? Questions?
--------------------
Have a look at the [Wiki](https://github.com/MinecraftWars/Gringotts/wiki). You're welcome to improve it, too!


Development
-----------
Would you like to make changes to Gringotts yourself? Fork it!
Pull requests are very welcome, but please make sure your changes fulfill the Gringotts quality baseline:

* new features, settings, permissions are documented
* required dependencies are all added to the build by Maven, not included in the repo
* the project builds with Maven out-of-the-box

Gringotts uses the [Apache Maven](http://maven.apache.org/) build system. Build a working plugin jar with the command:

```bash
mvn compile package
```

This shades in some dependencies (such as plugin metrics). For this reason, creating a jar package manually or from an IDE may not work correctly.


Depending on Gringotts
-----------
Gringotts makes use of JitPack to provide itself as a just-in-time compiled Maven dependency.

#### Step 1
Add the JitPack repository to your project's build system.
> See [JitPack.io](https://jitpack.io) for boilerplate examples.

Supported build systems:
- **Apache Maven** &mdash; [Maven POM Reference: Repositories](https://maven.apache.org/pom.html#Repositories)
- **Gradle** &mdash; [Gradle User Guide: Declaring Repositories](https://docs.gradle.org/current/userguide/declaring_repositories.html)

#### Step 2
Add the dependency.
See your documentation, or the boilerplate on JitPack.io, for dependency declaration instructions.
An example is given below for Apache Maven.

Replace the `RELEASE_TAG` with a valid [tagged commit](https://github.com/MinecraftWars/Gringotts/tags), or use
`master-SNAPSHOT` to track the master branch.
Note that Snapshots may require extra configuration of your repository.

```xml
<dependency>
   <groupId>com.github.MinecraftWars</groupId>
   <artifactId>Gringotts</artifactId>
   <version>RELEASE_TAG</version>
   <scope>provided</scope>
</dependency>
```

License
-------
All code within Gringotts is licensed under the BSD 2-clause license. See [the LICENSE.txt file](./LICENSE.txt) for details.
