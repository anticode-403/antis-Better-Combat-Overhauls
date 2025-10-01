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
    private final FakeAttack[] attacks;
    @SerializedName("critical_multiplier")
    private final double critical_multiplier;
    @SerializedName("versatile")
    private final @Nullable Boolean versatile;
    @SerializedName("versatile_damage")
    private final double versatile_damage;
    @SerializedName("versatile_attacks")
    private final FakeAttack[] versatile_attacks;
    @SerializedName("special_attacks")
    private final FakeAttack[] special_attacks;

    public FakeWeaponAttributes(double attack_range, @Nullable String pose, @Nullable String off_hand_pose, Boolean isTwoHanded, String category, FakeAttack[] attacks, double critical_multiplier, @Nullable Boolean versatile, double versatile_damage, @Nullable FakeAttack[] versatile_attacks, @Nullable FakeAttack[] special_attacks) {
        this.attack_range = attack_range;
        this.pose = pose;
        this.off_hand_pose = off_hand_pose;
        this.two_handed = isTwoHanded;
        this.category = category;
        this.attacks = attacks;
        this.critical_multiplier = critical_multiplier;
        this.versatile = versatile;
        this.versatile_damage = versatile_damage;
        this.versatile_attacks = versatile_attacks;
        this.special_attacks = special_attacks;
    }

    public WeaponAttributes convert() {
        WeaponAttributes attributes = new WeaponAttributes(attack_range, pose, off_hand_pose, two_handed, category, convertAttacks(attacks));
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        expandedAttributes.antisBetterCombatOverhauls$setCriticalMultiplier(critical_multiplier);
        if (versatile != null) {
            expandedAttributes.antisBetterCombatOverhauls$setVersatile(versatile);
        }
        expandedAttributes.antisBetterCombatOverhauls$setVersatileDamage(versatile_damage);
        expandedAttributes.antisBetterCombatOverhauls$setVersatileAttacks(convertAttacks(versatile_attacks));
        expandedAttributes.antisBetterCombatOverhauls$setHeavyAttacks(convertAttacks(special_attacks));
        return attributes;
    }

    private WeaponAttributes.Attack[] convertAttacks(FakeAttack[] fakeAttacks) {
        if (fakeAttacks == null) return null;
        WeaponAttributes.Attack[] attacks = new WeaponAttributes.Attack[fakeAttacks.length];
        for (int i = 0; i < fakeAttacks.length; i++) {
            attacks[i] = fakeAttacks[i].convert();
        }
        return attacks;
    }
}
