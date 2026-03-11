package me.crylonz.mendingenchant.service;

import me.crylonz.mendingenchant.utils.MendingEnchantConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MendingEnchantPityService {
    private final MendingEnchantConfig config;
    private final Map<UUID, Integer> failuresByPlayer = new HashMap<>();

    public MendingEnchantPityService(MendingEnchantConfig config) {
        this.config = config;
    }

    public double getBonus(UUID playerId) {
        if (!config.getBoolean("enchanting.pity.enabled")) {
            return 0.0;
        }

        int failures = failuresByPlayer.getOrDefault(playerId, 0);
        double bonusPerFailure = config.getDouble("enchanting.pity.bonus-per-failure");
        double maxBonus = config.getDouble("enchanting.pity.max-bonus");
        return Math.min(failures * bonusPerFailure, maxBonus);
    }

    public void recordSuccess(UUID playerId) {
        failuresByPlayer.remove(playerId);
    }

    public void recordFailure(UUID playerId) {
        if (!config.getBoolean("enchanting.pity.enabled")) {
            return;
        }

        failuresByPlayer.merge(playerId, 1, Integer::sum);
    }

    public int getFailures(UUID playerId) {
        return failuresByPlayer.getOrDefault(playerId, 0);
    }

    public void reset() {
        failuresByPlayer.clear();
    }
}
