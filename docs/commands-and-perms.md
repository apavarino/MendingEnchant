## MendingEnchant - Commands & Permissions

Make sure you have [installed](installation.md) the plugin before reading this section.

This page documents the current `/mendingenchant` commands and permission nodes.

### Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/mendingenchant reload` | `mendingenchant.admin.reload` | Reload configuration, localization, and pity state. |
| `/mendingenchant info` | `mendingenchant.admin.info` | Show the current effective configuration summary in chat. |
| `/me reload` | `mendingenchant.admin.reload` | Alias of `/mendingenchant reload`. |
| `/me info` | `mendingenchant.admin.info` | Alias of `/mendingenchant info`. |

### Permission Nodes

| Permission | Description |
|------------|-------------|
| `mendingenchant.use` | Allows a player to obtain Mending from the enchanting table. |
| `mendingenchant.custom1` | Uses the `custom-permission-1` enchanting probability tier. |
| `mendingenchant.custom2` | Uses the `custom-permission-2` enchanting probability tier. |
| `mendingenchant.custom3` | Uses the `custom-permission-3` enchanting probability tier. |
| `mendingenchant.admin.reload` | Allows reloading the MendingEnchant configuration. |
| `mendingenchant.admin.info` | Allows viewing the current configuration summary. |
| `mendingenchant.bypass.itemfilter` | Bypasses the item filter restrictions. |
| `mendingenchant.bypass.worldfilter` | Bypasses the world filter restrictions. |

### Command Output

`/mendingenchant info` prints:

- the plugin version
- updater state
- enchanting probability tiers
- fishing probability
- item filter mode and configured materials
- world filter mode and configured worlds
