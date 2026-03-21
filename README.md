<h1  align="center">
    <img src="mending-enchant-logo.png" alt="MendingEnchant" width="800" /><br>
</h1>

<h2  align="center">
    <img src="http://cf.way2muchnoise.eu/full_322356_downloads.svg" alt="download"/> 
    <img src="https://img.shields.io/github/license/stellionix/mendingenchant" alt="licence"/>
    <img src="https://img.shields.io/github/last-commit/stellionix/mendingenchant" alt="commit"/>
    <img src="https://github.com/stellionix/mendingenchant/actions/workflows/ci.yml/badge.svg" alt="CI"/>
    <a href="https://stellionix.github.io/MendingEnchant/"><img src="https://img.shields.io/badge/docs-online-blue" alt="Docs"/></a>
</h2>

MendingEnchant adds a configurable chance to obtain Mending from the enchanting table and from fishing.

It is designed for Minecraft Java Edition servers and primarily targets Bukkit, Spigot, and Paper.

## Features

- Add `MENDING` directly to enchanting table results
- Configure different probability tiers with permission-based access
- Add an optional pity system for repeated failed enchant attempts
- Give players a chance to fish Mending books
- Restrict behavior by world or by item type
- Support localization and in-game reload/info commands

## Compatibility

- Minecraft Java Edition
- Bukkit API `1.13+`
- Supported server software: Bukkit, Spigot, Paper, and similar forks

## Download

- [CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/mendingenchant)
- [BukkitDev](https://dev.bukkit.org/projects/mendingenchant)

## Quick Start

1. Download the latest `mending-enchant` JAR.
2. Place it in your server's `plugins/` directory.
3. Start the server.
4. Edit the generated `plugins/MendingEnchant/config.yml` if needed.
5. Run `/mendingenchant reload` after configuration changes.

Expected startup log:

```text
[MendingEnchant] is enabled !
```

## Documentation

The official documentation is available at [stellionix.github.io/MendingEnchant](https://stellionix.github.io/MendingEnchant/).

Useful pages:

- [Installation](https://stellionix.github.io/MendingEnchant/installation/)
- [Configuration](https://stellionix.github.io/MendingEnchant/configuration/)
- [Commands and Permissions](https://stellionix.github.io/MendingEnchant/commands-and-perms/)
- [Troubleshooting](https://stellionix.github.io/MendingEnchant/troubleshooting/)

## Commands

The main command is `/mendingenchant` with alias `/me`.

Common examples:

- `/mendingenchant reload`
- `/mendingenchant info`

The full command list and permission nodes are documented [here](https://stellionix.github.io/MendingEnchant/commands-and-perms/).

## Configuration Highlights

Main configurable areas:

- enchanting probabilities
- custom permission tiers
- pity system
- item filter
- world filter
- fishing probability
- localization

See the full configuration reference [here](https://stellionix.github.io/MendingEnchant/configuration/).

## Statistics

<img align="center" src="https://bstats.org/signatures/bukkit/MendingEnchant.svg" alt="stats"/> 

More statistics are available on [bStats](https://bstats.org/plugin/bukkit/MendingEnchant/16292).
