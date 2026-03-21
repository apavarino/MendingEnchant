## MendingEnchant - Troubleshooting

This page covers common issues and quick fixes.

### Mending never appears on enchanting table results

Check:

- the player has `mendingenchant.use`
- the item is not blocked by `enchanting.item-filter`
- the world is not blocked by `world-filter`
- the item is not receiving `Infinity` in the same enchant action
- your probability values are not set too low for the expected result

### Fishing never gives Mending books

Check:

- `fishing.probability` is greater than `0`
- the current world is allowed by `world-filter`
- the player has inventory space for the enchanted book

### Config values keep changing after startup or reload

This is usually the validator normalizing invalid values.

Examples:

- invalid probabilities are clamped to the valid range
- invalid filter modes fall back to `disabled`
- unknown materials are removed
- unknown worlds are removed

### Locale file is ignored

Check:

- `localization.locale` matches an existing file name
- the JSON file is valid
- `/mendingenchant reload` was run after editing

If loading fails, the plugin falls back to `en_US`.

### Commands not working

Check:

- the command is `/mendingenchant` or `/me`
- the sender has the required admin permission
- the plugin enabled cleanly at startup

### Need more help

- Share startup logs and the relevant `config.yml` section when reporting an issue.
- Open an issue on GitHub: [stellionix/MendingEnchant](https://github.com/stellionix/MendingEnchant/issues)
