package dev.rolypolyvole.golemtargets.mixin;

import dev.rolypolyvole.golemtargets.GolemTargetAccessor;
import dev.rolypolyvole.golemtargets.GolemTargetGoal;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractGolem.class)
public abstract class AbstractGolemMixin extends PathfinderMob implements GolemTargetAccessor {

    @Unique
    private SimpleContainer golemTargets$container;

    protected AbstractGolemMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @NonNull SimpleContainer golemTargets$getContainer() {
        return this.golemTargets$container;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void golemTargets$init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        this.golemTargets$container = new SimpleContainer(5);
        this.targetSelector.addGoal(0, new GolemTargetGoal((AbstractGolem) (Object) this));
    }

}
