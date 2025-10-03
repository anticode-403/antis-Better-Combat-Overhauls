package me.anticode.abco.logic;

import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedAttackHand;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.mixin.PlayerAttackHelperMixin;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class ExpandedPlayerAttackHelper {
    public static AttackHand getCurrentHeavyAttack(PlayerEntity player, int heavyComboCount) {
        ItemStack itemStack = player.getMainHandStack();
        if (itemStack.isEmpty()) return null;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) return null;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (attributes.isTwoHanded() || (expandedAttributes.antisBetterCombatOverhauls$getVersatile() && player.getOffHandStack().isEmpty())) {
            if (expandedAttributes.antisBetterCombatOverhauls$hasHeavyAttacks()) {
                AttackSelection attackSelection = selectHeavyAttack(heavyComboCount, attributes, expandedAttributes, player, false);
                if (attackSelection == null) return null;
                WeaponAttributes.Attack attack = attackSelection.attack;
                ComboState combo = attackSelection.comboState;
                AttackHand attackHand = new AttackHand(attack, combo, false, attributes, itemStack);
                ExpandedAttackHand expandedAttackHand = (ExpandedAttackHand)(Object)attackHand;
                expandedAttackHand.antisBetterCombatOverhauls$setSpecialAttack(true);
                return attackHand;
            }
        }
        return null;
    }

    private static AttackSelection selectHeavyAttack(int comboCount, WeaponAttributes attributes, ExpandedWeaponAttributes expandedAttributes, PlayerEntity player, boolean isOffHandAttack) {
        WeaponAttributes.Attack[] attacks = expandedAttributes.antisBetterCombatOverhauls$getHeavyAttacks();
        if (attacks.length == 0) return null;
        attacks = Arrays.stream(attacks).filter((attack) -> attack.conditions() == null || attack.conditions().length == 0 || PlayerAttackHelperMixin.invokeEvaluateConditions(attack.conditions(), player, isOffHandAttack)).toArray((x$0) -> new WeaponAttributes.Attack[x$0]);
        if (comboCount < 0) {
            comboCount = 0;
        }

        int index = comboCount % attacks.length;
        return new AttackSelection(attacks[index], new ComboState(index + 1, attacks.length));
    }

    public static float getAttackCooldownTicksCapped(PlayerEntity player, WeaponAttributes.Attack attack) {
        ExpandedAttack expandedAttack = (ExpandedAttack)(Object)attack;
        return Math.max((float)(1D / (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * expandedAttack.antisBetterCombatOverhauls$getAttackSpeedMultiplier()) * 20D), (float) BetterCombat.config.attack_interval_cap);
    }

    private static record AttackSelection(WeaponAttributes.Attack attack, ComboState comboState) {
    }
}
