package dev.rolypolyvole.golemtargets.util;

import dev.rolypolyvole.golemtargets.config.GolemTargetsConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;

public class GolemTargetGoal extends TargetGoal {

    private final AbstractGolem golem;

    public GolemTargetGoal(AbstractGolem golem) {
        super(golem, false);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        SimpleContainer container = ((GolemTargetAccessor) golem).golemTargets$getContainer();
        Set<String> targetNames = new HashSet<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME)) {
                targetNames.add(stack.get(DataComponents.CUSTOM_NAME).getString());
            }
        }

        if (targetNames.isEmpty()) return false;

        double rangeSq = GolemTargetsConfig.range * GolemTargetsConfig.range;

        Player closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Player player : golem.level().players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            if (!targetNames.contains(player.getName().getString())) continue;
            double dist = player.distanceToSqr(golem);
            if (dist > rangeSq) continue;
            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }

        this.targetMob = closest;
        return this.targetMob != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetMob == null || !this.targetMob.isAlive()) return false;
        if (this.targetMob instanceof Player player && (player.isSpectator() || player.isCreative())) return false;

        double rangeSq = GolemTargetsConfig.range * GolemTargetsConfig.range;
        if (this.targetMob.distanceToSqr(golem) > rangeSq) return false;

        SimpleContainer container = ((GolemTargetAccessor) golem).golemTargets$getContainer();
        String targetName = this.targetMob.getName().getString();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME)) {
                if (stack.get(DataComponents.CUSTOM_NAME).getString().equals(targetName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.targetMob);
    }

    @Override
    public void tick() {
        this.mob.setTarget(this.targetMob);
    }

    @Override
    public void stop() {
        this.targetMob = null;
        this.mob.setTarget(null);
    }
}
