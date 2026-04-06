package dev.rolypolyvole.golemtargets.mixin;

import dev.rolypolyvole.golemtargets.GolemTargetAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void golemTargets$onDeath(DamageSource source, CallbackInfo ci) {
        if (!((Object) this instanceof GolemTargetAccessor accessor)) return;
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.level() instanceof ServerLevel serverLevel) {
            SimpleContainer container = accessor.golemTargets$getContainer();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    self.spawnAtLocation(serverLevel, stack);
                }
            }

            container.clearContent();
        }
    }
}
