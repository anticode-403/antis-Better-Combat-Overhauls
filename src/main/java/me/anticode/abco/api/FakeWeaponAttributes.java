package me.anticode.abco.api;

import me.anticode.abco.BCOverhauls;
import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;

public final class FakeWeaponAttributes {
    private final double attack_range;
    private final @Nullable String pose;
    private final @Nullable String off_hand_pose;
    private final Boolean two_handed;
    private final @Nullable String category;
    private final WeaponAttributes.Attack[] attacks;
    private final @Nullable Boolean versatile;
    private final double versatile_damage;
    private final WeaponAttributes.Attack[] versatile_attacks;
    private final WeaponAttributes.Attack[] special_attacks;

    public FakeWeaponAttributes(double attack_range, @Nullable String pose, @Nullable String off_hand_pose, Boolean isTwoHanded, String category, WeaponAttributes.Attack[] attacks, @Nullable Boolean versatile, double versatile_damage, @Nullable WeaponAttributes.Attack[] versatile_attacks, @Nullable WeaponAttributes.Attack[] special_attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
        BCOverhauls.LOGGER.debug("versatile: " + versatile);
        this.versatile = versatile;
        BCOverhauls.LOGGER.debug("versatile_damage: " + versatile_damage);
        this.versatile_damage = versatile_damage;
        BCOverhauls.LOGGER.debug("versatile_attacks: " + versatile_attacks.length);
        this.versatile_attacks = versatile_attacks;
        BCOverhauls.LOGGER.debug("special_attacks: " + special_attacks.length);
        this.special_attacks = special_attacks;
    }

    public WeaponAttributes convert() {
        WeaponAttributes attributes = new WeaponAttributes(attack_range, pose, off_hand_pose, two_handed, category, attacks);
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (versatile != null) {
            expandedAttributes.antisBetterCombatOverhauls$setVersatile(versatile);
        }
        expandedAttributes.antisBetterCombatOverhauls$setVersatileDamage(versatile_damage);
        expandedAttributes.antisBetterCombatOverhauls$setVersatileAttacks(versatile_attacks);
        expandedAttributes.antisBetterCombatOverhauls$setHeavyAttacks(special_attacks);
        return attributes;
    }
}
