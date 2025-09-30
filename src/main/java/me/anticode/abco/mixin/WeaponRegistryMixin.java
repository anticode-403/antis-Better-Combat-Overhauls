package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = WeaponRegistry.class, remap = false)
public abstract class WeaponRegistryMixin {
    @Shadow
    private static Map<Identifier, AttributesContainer> containers;

    @Inject(method = "resolveAttributes", at = @At("RETURN"))
    private static void resolveAttributesMixin(Identifier itemId, AttributesContainer container, CallbackInfoReturnable<WeaponAttributes> cir) {
        BCOverhauls.LOGGER.debug("ABCO GET ID: " + itemId.toString());
        BCOverhauls.LOGGER.debug("ABCO GET VERSATILE: " + ((ExpandedWeaponAttributes)(Object)cir.getReturnValue()).antisBetterCombatOverhauls$getVersatile());
    }
}
