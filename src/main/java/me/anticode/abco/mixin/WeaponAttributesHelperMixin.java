package me.anticode.abco.mixin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.FakeAttributesContainer;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.Reader;
import java.lang.reflect.Type;

@Mixin(value = WeaponAttributesHelper.class, remap = false)
public abstract class WeaponAttributesHelperMixin {
    @Unique
    private final static Type fakeAttributesContainerFormat = (new TypeToken<FakeAttributesContainer>() {
    }).getType();

    @ModifyReturnValue(method = "override", at = @At("TAIL"))
    private static WeaponAttributes addExpandedWeaponAttributes(WeaponAttributes original, @Local(argsOnly = true, ordinal = 0) WeaponAttributes a, @Local(argsOnly = true, ordinal = 1) WeaponAttributes b) {

        ExpandedWeaponAttributes exa = (ExpandedWeaponAttributes)(Object)a;
        ExpandedWeaponAttributes exb = (ExpandedWeaponAttributes)(Object)b;
        ExpandedWeaponAttributes exo = (ExpandedWeaponAttributes)(Object)original;
        assert exa != null;
        assert exb != null;
        assert exo != null;
        // Versatile
        if (exb.antisBetterCombatOverhauls$hasVersatile()) {
            exo.antisBetterCombatOverhauls$setVersatile(exb.antisBetterCombatOverhauls$getVersatile());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatile(exa.antisBetterCombatOverhauls$getVersatile());
        }
        // Versatile Damage
        if (exb.antisBetterCombatOverhauls$hasVersatileDamage()) {
            exo.antisBetterCombatOverhauls$setVersatileDamage(exb.antisBetterCombatOverhauls$getVersatileDamage());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatileDamage(exa.antisBetterCombatOverhauls$getVersatileDamage());
        }
        // Versatile Attacks
        if (exb.antisBetterCombatOverhauls$hasVersatileAttacks() && exb.antisBetterCombatOverhauls$getVersatileAttacks().length > 0) {
            exo.antisBetterCombatOverhauls$setVersatileAttacks(exb.antisBetterCombatOverhauls$getVersatileAttacks());
        }
        else {
            exo.antisBetterCombatOverhauls$setVersatileAttacks(exa.antisBetterCombatOverhauls$getVersatileAttacks());
        }
        // Heavy Attacks
        if (exb.antisBetterCombatOverhauls$hasHeavyAttacks() && exb.antisBetterCombatOverhauls$getHeavyAttacks().length > 0) {
            exo.antisBetterCombatOverhauls$setHeavyAttacks(exb.antisBetterCombatOverhauls$getHeavyAttacks());
        }
        else {
            exo.antisBetterCombatOverhauls$setHeavyAttacks(exa.antisBetterCombatOverhauls$getHeavyAttacks());
        }
        return original;
    }

    @ModifyVariable(method = "override", at = @At("STORE"), ordinal = 2)
    private static WeaponAttributes.Attack redirectAttackOverride(WeaponAttributes.Attack original, @Local(ordinal = 1)WeaponAttributes.Attack override) {
        ExpandedAttack exOverride = (ExpandedAttack)(Object)override;
        ExpandedAttack exOriginal = (ExpandedAttack)(Object)original;
        assert exOverride != null;
        assert exOriginal != null;
        exOriginal.antisBetterCombatOverhauls$setKnockback(exOverride.antisBetterCombatOverhauls$getKnockback());
        exOriginal.antisBetterCombatOverhauls$setCritical(exOverride.antisBetterCombatOverhauls$getCritical());
        return original;
    }

    @Inject(method = "decode(Lcom/google/gson/stream/JsonReader;)Lnet/bettercombat/api/AttributesContainer;", at = @At("HEAD"), cancellable = true)
    private static void attributesContainerTest(JsonReader json, CallbackInfoReturnable<AttributesContainer> cir) {
        Gson gson = new Gson();
        FakeAttributesContainer decoy = gson.fromJson(json, fakeAttributesContainerFormat);
        AttributesContainer attributesContainer = decoy.convert();
        cir.setReturnValue(attributesContainer);
    }

    @Inject(method = "decode(Ljava/io/Reader;)Lnet/bettercombat/api/AttributesContainer;", at = @At("HEAD"), cancellable = true)
    private static void attributesContainerTest(Reader reader, CallbackInfoReturnable<AttributesContainer> cir) {
        Gson gson = new Gson();
        FakeAttributesContainer decoy = gson.fromJson(reader, fakeAttributesContainerFormat);
        AttributesContainer attributesContainer = decoy.convert();
        cir.setReturnValue(attributesContainer);
    }
}
