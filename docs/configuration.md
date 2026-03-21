## MendingEnchant - Configuration

Make sure you have [installed](installation.md) the plugin before editing the configuration.

You can edit the plugin behavior through `plugins/MendingEnchant/config.yml`.
After any change, run `/mendingenchant reload`.

### Updates

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `updater.enabled` | boolean | `true` | Enables automatic update checks and downloads. |

### Localization

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `localization.locale` | string | `en_US` | Language file loaded from `plugins/MendingEnchant/localization/<locale>.json`. |

### Enchanting Probabilities

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enchanting.probabilities.default` | number | `6.0` | Base chance to receive Mending at enchant level 30. |
| `enchanting.probabilities.custom-permission-1` | number | `12.0` | Same logic for players with `mendingenchant.custom1`. |
| `enchanting.probabilities.custom-permission-2` | number | `18.0` | Same logic for players with `mendingenchant.custom2`. |
| `enchanting.probabilities.custom-permission-3` | number | `24.0` | Same logic for players with `mendingenchant.custom3`. |

All probability values are clamped to the `0.0` to `100.0` range by the config validator.

### Pity System

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enchanting.pity.enabled` | boolean | `false` | Enables the pity bonus for repeated failed enchanting attempts. |
| `enchanting.pity.bonus-per-failure` | number | `2.0` | Extra percentage points added after each failed attempt. |
| `enchanting.pity.max-bonus` | number | `30.0` | Maximum pity bonus that can be accumulated. |

`bonus-per-failure` cannot be negative, and `max-bonus` is clamped to `0.0` to `100.0`.

### Enchanting Item Filter

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enchanting.item-filter.mode` | string | `disabled` | Filter mode: `disabled`, `whitelist`, or `blacklist`. |
| `enchanting.item-filter.materials` | list<string> | `[]` | Bukkit material names used by the selected mode. |

Rules:

- unknown materials are removed automatically when the config is validated
- material names are normalized to the canonical Bukkit enum names
- players with `mendingenchant.bypass.itemfilter` ignore this filter

### World Filter

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `world-filter.mode` | string | `disabled` | Filter mode: `disabled`, `whitelist`, or `blacklist`. |
| `world-filter.worlds` | list<string> | `[]` | World names used by the selected mode. |

Rules:

- unknown worlds are removed automatically when the config is validated
- world names are normalized to lowercase
- players with `mendingenchant.bypass.worldfilter` ignore this filter
- the world filter applies to both enchanting-table rewards and fishing rewards

### Fishing

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `fishing.probability` | number | `5.0` | Chance to receive a Mending enchanted book while fishing. |

This value is also clamped to the `0.0` to `100.0` range.
