package me.anticode.abco.logic;

import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedAttackHand;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.mixin.PlayerAttackHelperMixin;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.ComboState;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Arrays;
import java.util.Objects;

public class ExpandedPlayerAttackHelper {
    public static AttackHand getCurrentHeavyAttack(PlayerEntity player, int heavyComboCount) {
        ItemStack itemStack = player.getMainHandStack();
        if (itemStack.isEmpty()) return null;
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) return null;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (attributes.isTwoHanded()
                || (expandedAttributes.antisBetterCombatOverhauls$getVersatile() && player.getOffHandStack().isEmpty())
                || (expandedAttributes.antisBetterCombatOverhauls$getPaired() && WeaponRegistry.getAttributes(player.getOffHandStack()) != null && Objects.equals(WeaponRegistry.getAttributes(player.getOffHandStack()).category(), attributes.category()))) {
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
        attacks = Arrays.stream(attacks).filter((attack) -> {
            BCOverhauls.LOGGER.debug("Filtering attacks");
            if (attack.conditions() != null && attack.conditions().length != 0) {
                BCOverhauls.LOGGER.debug("evaluating conditions");
                return evaluateConditions(attack.conditions(), player, isOffHandAttack);
            }
            return true;
        }).toArray(WeaponAttributes.Attack[]::new);
        BCOverhauls.LOGGER.debug("Combo count thing");
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

    private static boolean evaluateConditions(WeaponAttributes.Condition[] conditions, PlayerEntity player, boolean isOffHandAttack) {
        return Arrays.stream(conditions).allMatch((condition) -> evaluateCondition(condition, player, isOffHandAttack));
    }

    private static boolean evaluateCondition(WeaponAttributes.Condition condition, PlayerEntity player, boolean isOffHandAttack) {
        if (condition == null) {
            return true;
        } else {
            switch (condition) {
                case NOT_DUAL_WIELDING:
                    return !PlayerAttackHelper.isDualWielding(player);
                case DUAL_WIELDING_ANY:
                    return PlayerAttackHelper.isDualWielding(player);
                case DUAL_WIELDING_SAME:
                    return PlayerAttackHelper.isDualWielding(player) && player.getMainHandStack().getItem() == player.getOffHandStack().getItem();
                case DUAL_WIELDING_SAME_CATEGORY:
                    if (!PlayerAttackHelper.isDualWielding(player)) {
                        return false;
                    } else {
                        WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(player.getMainHandStack());
                        WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(player.getOffHandStack());
                        if (mainHandAttributes.category() != null && !mainHandAttributes.category().isEmpty() && offHandAttributes.category() != null && !offHandAttributes.category().isEmpty()) {
                            return mainHandAttributes.category().equals(offHandAttributes.category());
                        }

                        return false;
                    }
                case NO_OFFHAND_ITEM:
                    if (player.getOffHandStack() != null && !player.getOffHandStack().isEmpty()) {
                        return false;
                    }

                    return true;
                case OFF_HAND_SHIELD:
                    if (player.getOffHandStack() == null && !(player.getOffHandStack().getItem() instanceof ShieldItem)) {
                        return false;
                    }
                    return true;
                case MAIN_HAND_ONLY:
                    return !isOffHandAttack;
                case OFF_HAND_ONLY:
                    return isOffHandAttack;
                case MOUNTED:
                    return player.getVehicle() != null;
                case NOT_MOUNTED:
                    return player.getVehicle() == null;
                default:
                    return true;
            }
        }
    }
}
