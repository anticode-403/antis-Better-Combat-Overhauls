package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedAttackHand;
import net.bettercombat.api.AttackHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AttackHand.class)
public class AttackHandMixin implements ExpandedAttackHand {
    @Unique
    private boolean special_attack = false;
    public boolean antisBetterCombatOverhauls$isSpecialAttack() {
        return special_attack;
    }
    public void antisBetterCombatOverhauls$setSpecialAttack(boolean value) {
        special_attack = value;
    }
}
