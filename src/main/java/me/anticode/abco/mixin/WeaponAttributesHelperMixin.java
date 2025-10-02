package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = WeaponAttributesHelper.class, remap = false)
public abstract class WeaponAttributesHelperMixin {
    @ModifyReturnValue(method = "override", at = @At("TAIL"))
    private static WeaponAttributes addExpandedWeaponAttributes(WeaponAttributes original, @Local(argsOnly = true, ordinal = 0) WeaponAttributes a, @Local(argsOnly = true, ordinal = 1) WeaponAttributes b) {

        ExpandedWeaponAttributes exa = (ExpandedWeaponAttributes)(Object)a;
        ExpandedWeaponAttributes exb = (ExpandedWeaponAttributes)(Object)b;
        ExpandedWeaponAttributes exo = (ExpandedWeaponAttributes)(Object)original;
        assert exa != null;
        assert exb != null;
        assert exo != null;
        // Critical Multiplier
        if (exb.antisBetterCombatOverhauls$getCriticalMultiplier() != 0) {
            exo.antisBetterCombatOverhauls$setCriticalMultiplier(exb.antisBetterCombatOverhauls$getCriticalMultiplier());
        }
        else {
            exo.antisBetterCombatOverhauls$setCriticalMultiplier(exa.antisBetterCombatOverhauls$getCriticalMultiplier());
        }
        // Versatile
        if (exb.antisBetterCombatOverhauls$hasVersatile()) {
            exo.antisBetterCombatOverhauls$setVersatile(exb.antisBetterCombatOverhauls$getVersatile());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatile(exa.antisBetterCombatOverhauls$getVersatile());
        }
        // Versatile Pose
        if (exb.antisBetterCombatOverhauls$hasVersatilePose()) {
            exo.antisBetterCombatOverhauls$setVersatilePose(exb.antisBetterCombatOverhauls$getVersatilePose());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatilePose(exa.antisBetterCombatOverhauls$getVersatilePose());
        }
        // Versatile Damage
        if (exb.antisBetterCombatOverhauls$getVersatileDamage() != 0) {
            exo.antisBetterCombatOverhauls$setVersatileDamage(exb.antisBetterCombatOverhauls$getVersatileDamage());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatileDamage(exa.antisBetterCombatOverhauls$getVersatileDamage());
        }
        // Versatile Attacks
        if (exb.antisBetterCombatOverhauls$hasVersatileAttacks() && exb.antisBetterCombatOverhauls$getVersatileAttacks().length > 0) {
            exo.antisBetterCombatOverhauls$setVersatileAttacks(exb.antisBetterCombatOverhauls$getVersatileAttacks());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatileAttacks(exa.antisBetterCombatOverhauls$getVersatileAttacks());
        }
        // Heavy Attacks
        if (exb.antisBetterCombatOverhauls$hasHeavyAttacks() && exb.antisBetterCombatOverhauls$getHeavyAttacks().length > 0) {
            exo.antisBetterCombatOverhauls$setHeavyAttacks(exb.antisBetterCombatOverhauls$getHeavyAttacks());
        }
        else {
            exo.antisBetterCombatOverhauls$setHeavyAttacks(exa.antisBetterCombatOverhauls$getHeavyAttacks());
        }
        return original;
    }

    @ModifyVariable(method = "override", at = @At("STORE"), ordinal = 2)
    private static WeaponAttributes.Attack redirectAttackOverride(WeaponAttributes.Attack original, @Local(ordinal = 1)WeaponAttributes.Attack override) {
        ExpandedAttack exOverride = (ExpandedAttack)(Object)override;
        ExpandedAttack exOriginal = (ExpandedAttack)(Object)original;
        assert exOverride != null;
        assert exOriginal != null;
        exOriginal.antisBetterCombatOverhauls$setKnockback(exOverride.antisBetterCombatOverhauls$getKnockback());
        exOriginal.antisBetterCombatOverhauls$setCritical(exOverride.antisBetterCombatOverhauls$getCritical());
        exOriginal.antisBetterCombatOverhauls$setAttackSpeedMultiplier(exOverride.antisBetterCombatOverhauls$getAttackSpeedMultiplier());
        return original;
    }
}
