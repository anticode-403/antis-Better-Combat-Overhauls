package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WeaponAttributes.class, remap = false)
public abstract class WeaponAttributesMixin implements ExpandedWeaponAttributes {
    @Unique
    private double critical_multiplier = 1.5F;
    @Unique
    private @Nullable Boolean versatile;
    @Unique
    private double versatile_damage = 0;
    @Unique
    private @Nullable WeaponAttributes.Attack[] special_attacks;
    @Unique
    private @Nullable WeaponAttributes.Attack[] versatile_attacks;

    @Override
    public void antisBetterCombatOverhauls$setVersatile(boolean versatility) {
        versatile = versatility;
    }

    @Override
    public boolean antisBetterCombatOverhauls$getVersatile() {
        if (versatile == null) return false;
        return versatile;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasVersatile() {
        return versatile != null;
    }

    @Override
    public void antisBetterCombatOverhauls$setVersatileDamage(double damage) {
        versatile_damage = damage;
    }

    @Override
    public double antisBetterCombatOverhauls$getVersatileDamage() {
        return versatile_damage;
    }

    @Override
    public void antisBetterCombatOverhauls$setCriticalMultiplier(double damage) {
        critical_multiplier = damage;
    }

    @Override
    public double antisBetterCombatOverhauls$getCriticalMultiplier() {
        return critical_multiplier;
    }

    @Override
    public void antisBetterCombatOverhauls$setVersatileAttacks(WeaponAttributes.Attack[] attacks) {
        versatile_attacks = attacks;
    }

    @Override
    public WeaponAttributes.Attack[] antisBetterCombatOverhauls$getVersatileAttacks() {
        return versatile_attacks;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasVersatileAttacks() {
        return versatile_attacks != null;
    }

    @Override
    public void antisBetterCombatOverhauls$setHeavyAttacks(WeaponAttributes.Attack[] attacks) {
        special_attacks = attacks;
    }

    @Override
    public WeaponAttributes.Attack[] antisBetterCombatOverhauls$getHeavyAttacks() {
        return special_attacks;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasHeavyAttacks() {
        return special_attacks != null;
    }
}
