package dev.rolypolyvole.golemtargets.util;

import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface GolemTargetAccessor {
    SimpleContainer golemTargets$getContainer();

    @Nullable UUID golemTargets$getOwnerUUID();
    void golemTargets$setOwnerUUID(@Nullable UUID uuid);
}
