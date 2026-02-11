package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = WeaponAttributes.class, remap = false)
public abstract class WeaponAttributesMixin implements ExpandedWeaponAttributes {
    @Unique
    private double critical_multiplier = 1.5F;
    @Unique
    private @Nullable Boolean versatile;
    @Unique
    private @Nullable String alternate_pose;
    @Unique
    private double versatile_damage = 0;
    @Unique
    private @Nullable WeaponAttributes.Attack[] versatile_attacks;
    @Unique
    private @Nullable Boolean finesse;
    @Unique
    private @Nullable Boolean paired;
    @Unique
    private @Nullable WeaponAttributes.Attack[] special_attacks;
    @Unique
    private @Nullable String parry_pose;
    @Unique
    private double parry_resistance = 0;
    @Unique
    private int parry_duration = 0;
    @Unique
    private int parry_punishment = 0;


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
    public void antisBetterCombatOverhauls$setAlternatePose(String pose) {
        alternate_pose = pose;
    }

    @Override
    public String antisBetterCombatOverhauls$getAlternatePose() {
        return alternate_pose;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasAlternatePose() {
        return alternate_pose != null && !alternate_pose.isEmpty();
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
    public boolean antisBetterCombatOverhauls$hasFinesse() {
        return finesse != null;
    }

    @Override
    public void antisBetterCombatOverhauls$setFinesse(boolean finesse) {
        this.finesse = finesse;
    }

    @Override
    public boolean antisBetterCombatOverhauls$getFinesse() {
        if (finesse == null) return false;
        return finesse;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasPaired() {
        return paired != null;
    }

    @Override
    public void antisBetterCombatOverhauls$setPaired(boolean paired) {
        this.paired = paired;
    }

    @Override
    public boolean antisBetterCombatOverhauls$getPaired() {
        if (paired == null) return false;
        return paired;
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

    @Override
    public double antisBetterCombatOverhauls$getParryResistance() {
        if (parry_resistance == 0) return 1;
        return parry_resistance;
    }

    @Override
    public double antisBetterCombatOverhauls$getRawParryResistance() {
        return parry_resistance;
    }

    @Override
    public void antisBetterCombatOverhauls$setParryResistance(double resistance) {
        this.parry_resistance = resistance;
    }

    @Override
    public int antisBetterCombatOverhauls$getParryDuration() {
        if (parry_duration == 0) return 15;
        return parry_duration;
    }

    @Override
    public int antisBetterCombatOverhauls$getRawParryDuration() {
        return parry_duration;
    }

    @Override
    public void antisBetterCombatOverhauls$setParryDuration(int duration) {
        this.parry_duration = duration;
    }

    @Override
    public int antisBetterCombatOverhauls$getParryPunishment() {
        if (parry_punishment == 0) return 20;
        return parry_punishment;
    }

    @Override
    public int antisBetterCombatOverhauls$getRawParryPunishment() {
        return parry_punishment;
    }

    @Override
    public void antisBetterCombatOverhauls$setParryPunishment(int duration) {
        this.parry_punishment = duration;
    }

    @Override
    public boolean antisBetterCombatOverhauls$hasParryPose() {
        return parry_pose != null && !parry_pose.isEmpty();
    }

    @Override
    public String antisBetterCombatOverhauls$getParryPose() {
        return parry_pose;
    }

    @Override
    public void antisBetterCombatOverhauls$setParryPose(String pose) {
        parry_pose = pose;
    }
}
