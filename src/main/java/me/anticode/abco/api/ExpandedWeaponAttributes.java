package me.anticode.abco.api;

import net.bettercombat.api.WeaponAttributes;

public interface ExpandedWeaponAttributes {
    boolean antisBetterCombatOverhauls$getVersatile();

    void antisBetterCombatOverhauls$setVersatile(boolean versatile);

    boolean antisBetterCombatOverhauls$hasVersatile();

    double antisBetterCombatOverhauls$getVersatileDamage();

    void antisBetterCombatOverhauls$setVersatileDamage(double versatile);

    double antisBetterCombatOverhauls$getCriticalMultiplier();

    void antisBetterCombatOverhauls$setCriticalMultiplier(double multiplier
    );

    void antisBetterCombatOverhauls$setVersatileAttacks(WeaponAttributes.Attack[] attacks);

    WeaponAttributes.Attack[] antisBetterCombatOverhauls$getVersatileAttacks();

    boolean antisBetterCombatOverhauls$hasVersatileAttacks();

    void antisBetterCombatOverhauls$setHeavyAttacks(WeaponAttributes.Attack[] attacks);

    WeaponAttributes.Attack[] antisBetterCombatOverhauls$getHeavyAttacks();

    boolean antisBetterCombatOverhauls$hasHeavyAttacks();
}
