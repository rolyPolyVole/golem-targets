package dev.rolypolyvole.golemtargets;

import net.fabricmc.api.ModInitializer;

public class GolemTargets implements ModInitializer {

    @Override
    public void onInitialize() {
        GolemTargetsConfig.load();
    }
}
