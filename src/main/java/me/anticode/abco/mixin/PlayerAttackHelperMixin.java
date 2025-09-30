package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerAttackHelper.class, remap = false)
public class PlayerAttackHelperMixin {

    @Redirect(method = "selectAttack", at = @At(value = "INVOKE", target = "Lnet/bettercombat/api/WeaponAttributes;attacks()[Lnet/bettercombat/api/WeaponAttributes$Attack;"))
    private static WeaponAttributes.Attack[] replaceAttacksList(WeaponAttributes instance, @Local(argsOnly = true) PlayerEntity player) {
        ExpandedWeaponAttributes exInstance = (ExpandedWeaponAttributes)(Object)instance;
        if (exInstance.antisBetterCombatOverhauls$getVersatile() && player.getOffHandStack().isEmpty()) {
            return exInstance.antisBetterCombatOverhauls$getVersatileAttacks();
        }
        return instance.attacks();
    }
}