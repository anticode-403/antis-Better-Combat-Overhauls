package me.anticode.abco.api;

import com.google.gson.annotations.SerializedName;
import me.anticode.abco.BCOverhauls;
import net.bettercombat.api.WeaponAttributes;
import org.jetbrains.annotations.Nullable;

public final class FakeWeaponAttributes {
    @SerializedName("attack_range")
    private final double attack_range;
    @SerializedName("pose")
    private final @Nullable String pose;
    @SerializedName("off_hand_pose")
    private final @Nullable String off_hand_pose;
    @SerializedName("two_handed")
    private final Boolean two_handed;
    @SerializedName("category")
    private final @Nullable String category;
    @SerializedName("attacks")
    private final WeaponAttributes.Attack[] attacks;
    @SerializedName("versatile")
    private final @Nullable Boolean versatile;
    @SerializedName("versatile_damage")
    private final double versatile_damage;
    @SerializedName("versatile_attacks")
    private final WeaponAttributes.Attack[] versatile_attacks;
    @SerializedName("special_attacks")
    private final WeaponAttributes.Attack[] special_attacks;

    public FakeWeaponAttributes(double attack_range, @Nullable String pose, @Nullable String off_hand_pose, Boolean isTwoHanded, String category, WeaponAttributes.Attack[] attacks, @Nullable Boolean versatile, double versatile_damage, @Nullable WeaponAttributes.Attack[] versatile_attacks, @Nullable WeaponAttributes.Attack[] special_attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
        this.versatile = versatile;
        this.versatile_damage = versatile_damage;
        this.versatile_attacks = versatile_attacks;
        this.special_attacks = special_attacks;
    }

    public WeaponAttributes convert() {
        WeaponAttributes attributes = new WeaponAttributes(attack_range, pose, off_hand_pose, two_handed, category, attacks);
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        BCOverhauls.LOGGER.debug("category: " + category);
        if (versatile != null) {
            BCOverhauls.LOGGER.debug("versatile: " + versatile);
            expandedAttributes.antisBetterCombatOverhauls$setVersatile(versatile);
        }
        BCOverhauls.LOGGER.debug("versatile_damage: " + versatile_damage);
        expandedAttributes.antisBetterCombatOverhauls$setVersatileDamage(versatile_damage);
        if (versatile_attacks != null) {
            BCOverhauls.LOGGER.debug("versatile_attacks: " + versatile_attacks.length);
        }
        expandedAttributes.antisBetterCombatOverhauls$setVersatileAttacks(versatile_attacks);
        if (special_attacks != null) {
            BCOverhauls.LOGGER.debug("special_attacks: " + special_attacks.length);
        }
        expandedAttributes.antisBetterCombatOverhauls$setHeavyAttacks(special_attacks);
        return attributes;
    }
}
