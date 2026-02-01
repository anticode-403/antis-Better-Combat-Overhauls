package me.anticode.abco.api;

import net.bettercombat.api.WeaponAttributes;

public interface ExpandedWeaponAttributes {
    // Versatile
    boolean antisBetterCombatOverhauls$getVersatile();

    void antisBetterCombatOverhauls$setVersatile(boolean versatile);

    boolean antisBetterCombatOverhauls$hasVersatile();

    // Versatile Pose
    String antisBetterCombatOverhauls$getAlternatePose();

    void antisBetterCombatOverhauls$setAlternatePose(String pose);

    boolean antisBetterCombatOverhauls$hasAlternatePose();

    // Versatile Damage
    double antisBetterCombatOverhauls$getVersatileDamage();

    void antisBetterCombatOverhauls$setVersatileDamage(double versatile);

    // Finesse
    boolean antisBetterCombatOverhauls$hasFinesse();

    void antisBetterCombatOverhauls$setFinesse(boolean finesse);

    boolean antisBetterCombatOverhauls$getFinesse();

    // Paired
    boolean antisBetterCombatOverhauls$hasPaired();

    void antisBetterCombatOverhauls$setPaired(boolean paired);

    boolean antisBetterCombatOverhauls$getPaired();

    // Critical Multiplier
    double antisBetterCombatOverhauls$getCriticalMultiplier();

    void antisBetterCombatOverhauls$setCriticalMultiplier(double multiplier);

    // Versatile Attacks
    void antisBetterCombatOverhauls$setVersatileAttacks(WeaponAttributes.Attack[] attacks);

    WeaponAttributes.Attack[] antisBetterCombatOverhauls$getVersatileAttacks();

    boolean antisBetterCombatOverhauls$hasVersatileAttacks();

    // Heavy/Special Attacks
    void antisBetterCombatOverhauls$setHeavyAttacks(WeaponAttributes.Attack[] attacks);

    WeaponAttributes.Attack[] antisBetterCombatOverhauls$getHeavyAttacks();

    boolean antisBetterCombatOverhauls$hasHeavyAttacks();
}
