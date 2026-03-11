package me.crylonz.mendingenchant.service;

import java.util.function.DoubleSupplier;

public class MendingEnchantRandomService {
    private DoubleSupplier randomSupplier = () -> Math.random() * 100;

    public double nextPercentage() {
        return randomSupplier.getAsDouble();
    }

    public void setRandomSupplier(DoubleSupplier randomSupplier) {
        this.randomSupplier = randomSupplier;
    }

    public void reset() {
        this.randomSupplier = () -> Math.random() * 100;
    }
}
