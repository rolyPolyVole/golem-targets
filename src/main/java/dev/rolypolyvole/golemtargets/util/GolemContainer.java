package dev.rolypolyvole.golemtargets.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public class GolemContainer extends SimpleContainer {

    private final Entity golem;

    public GolemContainer(Entity golem, int size) {
        super(size);
        this.golem = golem;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return golem.isAlive() && player.distanceToSqr(golem) <= 64.0;
    }
}
