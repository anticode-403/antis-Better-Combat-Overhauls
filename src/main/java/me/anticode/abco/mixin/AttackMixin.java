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
    private int knockback = 0;

    public void antisBetterCombatOverhauls$setCritical(boolean value) {
        critical = value;
    }
    public boolean antisBetterCombatOverhauls$getCritical() {
        return critical;
    }

    public void antisBetterCombatOverhauls$setKnockback(int value) {
        knockback = value;
    }
    public int antisBetterCombatOverhauls$getKnockback() {
        return knockback;
    }
}
