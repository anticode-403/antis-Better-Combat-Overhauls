package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    protected ItemStack activeItemStack;

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlockingMixin(CallbackInfoReturnable<Boolean> cir){
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity)) return; // zombies can't parry, so sayeth i
        if (isUsingItem() && !activeItemStack.isEmpty()) {
            WeaponAttributes attributes = WeaponRegistry.getAttributes(activeItemStack);
            if (attributes == null) return;
            ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
            assert expandedAttributes != null;
            if (ExpandedPlayerAttackHelper.isCurrentlyFinesse((PlayerEntity)(Object)this, attributes, expandedAttributes)) {
                /*
                 * Part of the reason we're doing it like this is so that in future we can add parry delays. Additionally,
                 * we modify ItemStack.getUseAction not Item.getUseAction, and this function normally only calls
                 * Item.getUseAction, so we were modifying it anyway.
                 *
                 * Additionally, isBlocking also implements a 4 tick blocking delay for shields by default, which should
                 * not be the case for our weapons by default.
                 */
                cir.setReturnValue(true);
            }
        }
    }
}
