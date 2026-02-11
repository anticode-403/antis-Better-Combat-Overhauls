package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.api.AbcoAnimatedPlayer;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.ParryableWeaponItemStack;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// TODO: DRY this class
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ParryableWeaponItemStack {
    @Shadow
    public abstract Item getItem();

    @Unique
    private boolean didSuccessfullyBlock = false;

    @Override
    public void antisBetterCombatOverhauls$shieldBlockedDamage() {
        didSuccessfullyBlock = true;
    }

    @ModifyReturnValue(method = "getUseAction", at = @At("RETURN"))
    private UseAction getUseAction(UseAction original) {
        ItemStack item = (ItemStack)(Object)this;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(item);
        if (attributes == null) return original;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (expandedAttributes.antisBetterCombatOverhauls$getFinesse()) {
            return UseAction.BLOCK;
        }
        return original;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void replaceUseAction(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) return;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (ExpandedPlayerAttackHelper.isCurrentlyFinesse(user, attributes, expandedAttributes)) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(itemStack));
        }
    }

    @Inject(method = "usageTick", at = @At("HEAD"))
    private void useParryMechanic(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (!(user instanceof PlayerEntity)) return;
        if (world.isClient()) ((AbcoAnimatedPlayer)user).antisBetterCombatOverhauls$updateAlternatePose();
        PlayerEntity player = (PlayerEntity)user;
        ItemStack itemStack = (ItemStack)(Object)this;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) return;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (!ExpandedPlayerAttackHelper.isCurrentlyFinesse(player, attributes, expandedAttributes)) return;
        // Interestingly enough, this will just infinitely go into the negatives, so we can instead use it as our timer.
        int maxUsageTicks = expandedAttributes.antisBetterCombatOverhauls$getParryDuration();
        if (maxUsageTicks <= -1) return; // -1 is infinite blocking
        int usageTicks = remainingUseTicks * -1;
        if (usageTicks >= maxUsageTicks) {
            player.stopUsingItem();
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void parryPunishmentOnStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (!(user instanceof PlayerEntity)) return;
        if (world.isClient()) ((AbcoAnimatedPlayer)user).antisBetterCombatOverhauls$updateAlternatePose();
        PlayerEntity player = (PlayerEntity)user;
        ItemStack itemStack = (ItemStack)(Object)this;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) return;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (!didSuccessfullyBlock) {
            ItemCooldownManager itemCooldownManager = player.getItemCooldownManager();
            itemCooldownManager.set(getItem(), expandedAttributes.antisBetterCombatOverhauls$getParryPunishment());
            if (player.getOffHandStack().equals(this)) itemCooldownManager.set(player.getMainHandStack().getItem(), expandedAttributes.antisBetterCombatOverhauls$getParryPunishment());
            else itemCooldownManager.set(player.getOffHandStack().getItem(), expandedAttributes.antisBetterCombatOverhauls$getParryPunishment());
        }
        didSuccessfullyBlock = false;
    }
}
