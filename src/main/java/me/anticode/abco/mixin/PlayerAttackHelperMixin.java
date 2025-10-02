package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ABCOPlayerEntity;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.HeavyAttackComboApi;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerAttackHelper.class, remap = false)
public abstract class PlayerAttackHelperMixin {
    @Redirect(method = "selectAttack", at = @At(value = "INVOKE", target = "Lnet/bettercombat/api/WeaponAttributes;attacks()[Lnet/bettercombat/api/WeaponAttributes$Attack;"))
    private static WeaponAttributes.Attack[] replaceAttacksList(WeaponAttributes instance, @Local(argsOnly = true) PlayerEntity player) {
        ExpandedWeaponAttributes exInstance = (ExpandedWeaponAttributes)(Object)instance;
        if (exInstance.antisBetterCombatOverhauls$getVersatile() && player.getOffHandStack().isEmpty()) {
            return exInstance.antisBetterCombatOverhauls$getVersatileAttacks();
        }
        return instance.attacks();
    }

    /*
     * Better Combat uses this function to get the current attack AND the NEXT attack, for some reason.
     * So we check if the combo count is the player's current combo count, in which case we know it's actually the
     * current attack.
     */
    @Inject(method = "getCurrentAttack", at = @At("HEAD"), cancellable = true)
    private static void overrideGetCurrentAttack(PlayerEntity player, int comboCount, CallbackInfoReturnable<AttackHand> cir) {
        if (!(((PlayerAttackProperties)player).getComboCount() == comboCount)) return;
        if (((ABCOPlayerEntity)player).antisBetterCombatOverhauls$wasLastAttackSpecial()) {
            cir.setReturnValue(ExpandedPlayerAttackHelper.getCurrentHeavyAttack(player, ((HeavyAttackComboApi)player).antisBetterCombatOverhauls$getHeavyCombo()));
            cir.cancel();
        }
    }

    @Invoker("evaluateConditions")
    public static boolean invokeEvaluateConditions(WeaponAttributes.Condition[] conditions, PlayerEntity player, boolean isOffHandAttack) {
        throw new AssertionError();
    }
}