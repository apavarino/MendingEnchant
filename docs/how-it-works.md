## MendingEnchant - How It Works

This page explains how MendingEnchant behaves in common gameplay situations.

### Enchanting table behavior

When a player enchants an item, MendingEnchant can inject `MENDING` into the result if all of the following are true:

- the player has `mendingenchant.use`
- the current world is allowed by `world-filter`
- the item is allowed by `enchanting.item-filter`
- the enchant result does not already contain `Infinity`

The plugin then computes a success chance from:

- the configured base probability tier
- the enchanting cost level used for the action
- the optional pity bonus accumulated after failures

### Probability tiers

The base chance is controlled by:

- `enchanting.probabilities.default`
- `enchanting.probabilities.custom-permission-1`
- `enchanting.probabilities.custom-permission-2`
- `enchanting.probabilities.custom-permission-3`

The highest matching tier is selected with this precedence:

- `mendingenchant.custom1`
- `mendingenchant.custom2`
- `mendingenchant.custom3`
- otherwise the default tier

Higher enchanting cost levels increase the effective success roll.

### Pity system

If `enchanting.pity.enabled` is enabled, each failed enchanting attempt increases the next chance by `enchanting.pity.bonus-per-failure`, up to `enchanting.pity.max-bonus`.

When Mending is successfully granted, the pity counter is reset for that player.

### Fishing behavior

On a successful fishing catch in an allowed world, the plugin rolls against `fishing.probability`.

If the roll succeeds:

- an enchanted book with `MENDING I` is added directly to the player's inventory
- a pickup sound is played
- the configured success message is sent

### Filters

Two filters can restrict where MendingEnchant applies:

- `enchanting.item-filter`: controls allowed or blocked materials for enchanting-table rewards
- `world-filter`: controls allowed or blocked worlds for enchanting-table rewards and fishing rewards

Each filter supports:

- `disabled`
- `whitelist`
- `blacklist`

Bypass permissions are available for both filters.
