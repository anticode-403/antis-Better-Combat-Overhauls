package me.anticode.abco.api;

import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;

public final class FakeWeaponAttributes {
    private final @Nullable Boolean versatile;
    private final @Nullable WeaponAttributes.Attack[] heavy_attacks;
    private final @Nullable WeaponAttributes.Attack[] versatile_attacks;
    private final double attack_range;
    private final @Nullable String pose;
    private final @Nullable String off_hand_pose;
    private final Boolean two_handed;
    private final @Nullable String category;
    private final WeaponAttributes.Attack[] attacks;

    public FakeWeaponAttributes(@Nullable Boolean versatile, @Nullable WeaponAttributes.Attack[] heavy_attacks, @Nullable WeaponAttributes.Attack[] versatile_attacks, double attack_range, @Nullable String pose, @Nullable String off_hand_pose, Boolean isTwoHanded, String category, WeaponAttributes.Attack[] attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
        this.versatile = versatile;
        this.heavy_attacks = heavy_attacks;
        this.versatile_attacks = versatile_attacks;
    }

    public WeaponAttributes convert() {
        WeaponAttributes attributes = new WeaponAttributes(attack_range, pose, off_hand_pose, two_handed, category, attacks);
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (versatile != null) {
            expandedAttributes.antisBetterCombatOverhauls$setVersatile(versatile);
        }
        expandedAttributes.antisBetterCombatOverhauls$setHeavyAttacks(heavy_attacks);
        expandedAttributes.antisBetterCombatOverhauls$setVersatileAttacks(versatile_attacks);
        return attributes;
    }
}
