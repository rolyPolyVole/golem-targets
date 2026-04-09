package dev.rolypolyvole.golemtargets.mixin;

import dev.rolypolyvole.golemtargets.GolemTargetAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @Inject(method = "spawnGolemInWorld", at = @At("TAIL"))
    private static void golemTargets$onSpawnGolem(Level level, BlockPattern.BlockPatternMatch match, Entity entity, BlockPos blockPos, CallbackInfo ci) {
        if (!(entity instanceof GolemTargetAccessor accessor)) return;

        Player nearest = level.getNearestPlayer(blockPos.getX() + 0.5, blockPos.getY() + 0.05, blockPos.getZ() + 0.5, 5.0, false);
        if (nearest != null) {
            accessor.golemTargets$setOwnerUUID(nearest.getUUID());
        }
    }
}
