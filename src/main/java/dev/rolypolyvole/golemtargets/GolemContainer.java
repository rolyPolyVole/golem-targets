package dev.rolypolyvole.golemtargets;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class GolemContainer extends SimpleContainer {

    private final Entity golem;

    public GolemContainer(Entity golem, int size) {
        super(size);
        this.golem = golem;
    }

    @Override
    public boolean stillValid(Player player) {
        return golem.isAlive() && player.distanceToSqr(golem) <= 64.0;
    }
}
