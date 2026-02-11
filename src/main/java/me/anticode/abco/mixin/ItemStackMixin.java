package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// TODO: DRY this class
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyReturnValue(method = "getUseAction", at = @At("RETURN"))
    private UseAction getUseAction(UseAction original) {
        ItemStack item = (ItemStack)(Object)this;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(item);
        if (attributes == null) return original;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (expandedAttributes.antisBetterCombatOverhauls$getFinesse()) {
            BCOverhauls.LOGGER.debug("Finesse!");
            return UseAction.BLOCK;
        }
        return original;
    }

    @ModifyReturnValue(method = "getMaxUseTime", at = @At("RETURN"))
    private int getMaxUseTime(int original) {
        ItemStack item = (ItemStack)(Object)this;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(item);
        if (attributes == null) return original;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        assert expandedAttributes != null;
        if (expandedAttributes.antisBetterCombatOverhauls$getFinesse())
            return expandedAttributes.antisBetterCombatOverhauls$getParryDuration();
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
}
