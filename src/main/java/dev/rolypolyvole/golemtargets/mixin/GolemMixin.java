package dev.rolypolyvole.golemtargets.mixin;

import dev.rolypolyvole.golemtargets.util.GolemTargetAccessor;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin({IronGolem.class, SnowGolem.class})
public abstract class GolemMixin extends AbstractGolem {

    protected GolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void golemTargets$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        if (!player.isShiftKeyDown()) return;

        UUID owner = ((GolemTargetAccessor) this).golemTargets$getOwnerUUID();
        if (owner != null && !player.getUUID().equals(owner)) return;

        SimpleContainer container = ((GolemTargetAccessor) this).golemTargets$getContainer();
        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty()) {
            if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (syncId, inv, p) -> new HopperMenu(syncId, inv, container),
                        Component.literal("Golem Targets")
                ));
            }

            info.setReturnValue(InteractionResult.SUCCESS);
        } else if (stack.is(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME)) {
            if (!this.level().isClientSide()) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    if (container.getItem(i).isEmpty()) {
                        container.setItem(i, stack.split(1));
                        break;
                    }
                }
            }

            info.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void golemTargets$saveTags(ValueOutput valueOutput, CallbackInfo ci) {
        GolemTargetAccessor accessor = (GolemTargetAccessor) this;
        UUID owner = accessor.golemTargets$getOwnerUUID();

        if (owner != null) {
            valueOutput.store("GolemTargetsOwner", UUIDUtil.CODEC, owner);
        }

        SimpleContainer container = accessor.golemTargets$getContainer();
        ValueOutput.ValueOutputList list = valueOutput.childrenList("GolemTargets");

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                ValueOutput child = list.addChild();
                child.putByte("Slot", (byte) i);
                child.store("Item", ItemStack.CODEC, stack);
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void golemTargets$loadOwner(ValueInput valueInput, CallbackInfo ci) {
        GolemTargetAccessor accessor = (GolemTargetAccessor) this;
        valueInput.read("GolemTargetsOwner", UUIDUtil.CODEC).ifPresent(accessor::golemTargets$setOwnerUUID);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void golemTargets$loadTags(ValueInput valueInput, CallbackInfo ci) {
        SimpleContainer container = ((GolemTargetAccessor) this).golemTargets$getContainer();

        for (ValueInput child : valueInput.childrenListOrEmpty("GolemTargets")) {
            int slot = child.getByteOr("Slot", (byte) 0) & 255;
            if (slot < container.getContainerSize()) {
                child.read("Item", ItemStack.CODEC).ifPresent(stack ->
                        container.setItem(slot, stack)
                );
            }
        }
    }
}
