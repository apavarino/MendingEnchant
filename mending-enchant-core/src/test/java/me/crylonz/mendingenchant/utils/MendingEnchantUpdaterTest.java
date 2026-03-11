package me.crylonz.mendingenchant.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MendingEnchantUpdaterTest {

    @Test
    @DisplayName("Version comparison should detect newer remote patch versions")
    public void comparePatchVersions() {
        Assertions.assertTrue(MendingEnchantUpdater.compareVersions("1.6.3", "1.6.4") < 0);
    }

    @Test
    @DisplayName("Version comparison should handle double digit segments correctly")
    public void compareDoubleDigitVersions() {
        Assertions.assertTrue(MendingEnchantUpdater.compareVersions("1.9.9", "1.10.0") < 0);
    }

    @Test
    @DisplayName("Version comparison should detect local unreleased versions")
    public void compareLocalNewerVersions() {
        Assertions.assertTrue(MendingEnchantUpdater.compareVersions("1.7.0", "1.6.9") > 0);
    }

    @Test
    @DisplayName("Version comparison should treat equivalent versions as equal")
    public void compareEquivalentVersions() {
        Assertions.assertEquals(0, MendingEnchantUpdater.compareVersions("1.7", "1.7.0"));
    }
}
