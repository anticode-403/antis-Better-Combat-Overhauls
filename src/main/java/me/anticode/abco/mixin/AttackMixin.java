package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedAttack;
import net.bettercombat.api.WeaponAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WeaponAttributes.Attack.class)
public abstract class AttackMixin implements ExpandedAttack {
    @Unique
    private boolean critical = false;
    @Unique
    private double knockback_multiplier = 1;
    @Unique
    private double attack_speed_multiplier = 1;

    public void antisBetterCombatOverhauls$setCritical(boolean value) {
        critical = value;
    }
    public boolean antisBetterCombatOverhauls$getCritical() {
        return critical;
    }

    public void antisBetterCombatOverhauls$setKnockback(double value) {
        knockback_multiplier = value;
    }
    public double antisBetterCombatOverhauls$getKnockback() {
        return knockback_multiplier;
    }

    public void antisBetterCombatOverhauls$setAttackSpeedMultiplier(double speed) {
        attack_speed_multiplier = speed;
    }

    public double antisBetterCombatOverhauls$getAttackSpeedMultiplier() {
        return attack_speed_multiplier;
    }
}
