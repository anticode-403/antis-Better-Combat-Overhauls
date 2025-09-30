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
    private final @Nullable WeaponAttributes.Attack[] versatile_attacks;
    private final @Nullable WeaponAttributes.Attack[] special_attacks;

    public FakeWeaponAttributes(double attack_range, @Nullable String pose, @Nullable String off_hand_pose, Boolean isTwoHanded, String category, WeaponAttributes.Attack[] attacks, @Nullable Boolean versatile, @Nullable WeaponAttributes.Attack[] versatile_attacks, @Nullable WeaponAttributes.Attack[] special_attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
        this.versatile = versatile;
        this.versatile_attacks = versatile_attacks;
        this.special_attacks = special_attacks;
    }

    public WeaponAttributes convert() {
        WeaponAttributes attributes = new WeaponAttributes(attack_range, pose, off_hand_pose, two_handed, category, attacks);
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        BCOverhauls.LOGGER.debug("VERSATILE: " +  versatile);
        BCOverhauls.LOGGER.debug("HEAVY_ATTACKS: " + (special_attacks != null ? "PRESENT" : "NA"));
        BCOverhauls.LOGGER.debug("VERSATILE_ATTACKS: " + (versatile_attacks != null ? "PRESENT" : "NA"));
        if (versatile != null) {
            expandedAttributes.antisBetterCombatOverhauls$setVersatile(versatile);
        }
        expandedAttributes.antisBetterCombatOverhauls$setHeavyAttacks(special_attacks);
        expandedAttributes.antisBetterCombatOverhauls$setVersatileAttacks(versatile_attacks);
        return attributes;
    }
}
