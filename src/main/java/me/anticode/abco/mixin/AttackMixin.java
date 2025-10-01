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
    private double kbMultiplier = 0D;

    public void antisBetterCombatOverhauls$setCritical(boolean value) {
        critical = value;
    }
    public boolean antisBetterCombatOverhauls$getCritical() {
        return critical;
    }

    public void antisBetterCombatOverhauls$setKnockbackMultiplier(double value) {
        kbMultiplier = value;
    }
    public double antisBetterCombatOverhauls$getKnockbackMultiplier() {
        return kbMultiplier;
    }
}
