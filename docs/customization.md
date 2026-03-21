## MendingEnchant - Customization

MendingEnchant is intentionally small, but you can still adapt it to different server profiles.

### Reward tuning

Use the probability tiers to differentiate access by rank or permission group:

- `enchanting.probabilities.default`
- `enchanting.probabilities.custom-permission-1`
- `enchanting.probabilities.custom-permission-2`
- `enchanting.probabilities.custom-permission-3`

Typical use cases:

- lower default chance for survival balance
- higher tier for VIP ranks
- higher fishing chance for economy or crate servers

### Restricting items

Use `enchanting.item-filter.mode` with `materials` to control which items can receive Mending from the enchanting table.

Examples:

- allow only tools and armor with `whitelist`
- block high-value items like `ELYTRA` with `blacklist`

Material names must match Bukkit `Material` names.

### Restricting worlds

Use `world-filter.mode` with `worlds` to restrict the feature to selected worlds.

Examples:

- enable Mending only in the main survival world
- disable the feature in challenge or minigame worlds

The same world filter also controls fishing rewards.

### Localization

Messages are loaded from `plugins/MendingEnchant/localization/<locale>.json`.

To customize messages:

1. Start the server once to generate the locale file.
2. Edit the generated JSON file.
3. Run `/mendingenchant reload`.

If the configured locale cannot be loaded, the plugin falls back to `en_US`.
