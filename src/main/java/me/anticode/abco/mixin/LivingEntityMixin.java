package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.ParryableWeaponItemStack;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
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
    public abstract ItemStack getMainHandStack();

    @Shadow
    public abstract Random getRandom();

    @Shadow
    public abstract ItemStack getOffHandStack();

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isParryingOrBlocking(CallbackInfoReturnable<Boolean> cir){
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity)) return; // zombies can't parry, so sayeth i
        if (!isUsingItem() || getMainHandStack().isEmpty()) return;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(getMainHandStack());
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

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void damageItemsAndFlagSuccessfulParries(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (!(((LivingEntity)(Object)this) instanceof PlayerEntity)) return;
        if (!isUsingItem() || getMainHandStack().isEmpty()) return;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(getMainHandStack());
        if (attributes == null) return;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes) (Object) attributes;
        assert expandedAttributes != null;
        if (ExpandedPlayerAttackHelper.isCurrentlyFinesse((PlayerEntity)(Object)this, attributes, expandedAttributes)) {
            ItemStack damageTarget = getMainHandStack();
            if (expandedAttributes.antisBetterCombatOverhauls$getPaired()) {
                damageTarget = getRandom().nextBoolean() ? getMainHandStack() : getOffHandStack();
            }
            if (damageTarget.isDamageable()) damageTarget.damage(3, getRandom(), (ServerPlayerEntity)(Object)this);

            ((ParryableWeaponItemStack)(Object)getMainHandStack()).antisBetterCombatOverhauls$shieldBlockedDamage();
            ((ParryableWeaponItemStack)(Object)getOffHandStack()).antisBetterCombatOverhauls$shieldBlockedDamage();
        }
    }
}
