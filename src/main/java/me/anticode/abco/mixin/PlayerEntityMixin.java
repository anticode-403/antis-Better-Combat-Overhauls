package me.anticode.abco.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.api.*;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class, priority = 1500)
public abstract class PlayerEntityMixin implements ABCOPlayerEntity, HeavyAttackComboApi {
    @Unique
    private boolean wasLastAttackSpecial = false;
    @Unique
    private int heavyCombo = 0;

    @Override
    public boolean antisBetterCombatOverhauls$wasLastAttackSpecial() {
        return wasLastAttackSpecial;
    }
    @Override
    public void antisBetterCombatOverhauls$setLastAttackSpecial(boolean attack) {
        wasLastAttackSpecial = attack;
    }

    @Override
    public int antisBetterCombatOverhauls$getHeavyCombo() {
        return heavyCombo;
    }
    @Override
    public void antisBetterCombatOverhauls$setHeavyCombo(int combo) {
        heavyCombo = combo;
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.PlayerEntityMixin",
            name = "getCurrentAttack()Lnet/bettercombat/api/AttackHand;"
    )
    @ModifyReturnValue(
            method = "@MixinSquared:Handler",
            at = @At(value = "RETURN")
    )
    private @Nullable AttackHand overrideGetCurrentAttack(AttackHand original) {
        if (wasLastAttackSpecial) {
            return ExpandedPlayerAttackHelper.getCurrentHeavyAttack((PlayerEntity)(Object)this, heavyCombo);
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 0)
    private float injectVersatileDamage(float value) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityPlayer_BetterCombat player_bc = (EntityPlayer_BetterCombat)player;
        if (player_bc.getCurrentAttack() == null) return value;
        WeaponAttributes attributes = player_bc.getCurrentAttack().attributes();
        if (attributes == null) return value;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (!expandedAttributes.antisBetterCombatOverhauls$getVersatile()) return value;
        if (!player.getOffHandStack().isEmpty()) return value; // Not in versatile stance
        float versatile_damage = (float) expandedAttributes.antisBetterCombatOverhauls$getVersatileDamage();
        return value + versatile_damage;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean injectNewCritSystem(boolean value) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityPlayer_BetterCombat player_bc = (EntityPlayer_BetterCombat)player;
        AttackHand hand = player_bc.getCurrentAttack();
        if (hand == null) return false;
        ExpandedAttack expandedAttack = (ExpandedAttack)(Object)hand.attack();
        return expandedAttack.antisBetterCombatOverhauls$getCritical();
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 0)
    private int injectNewKBAttribute(int value) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityPlayer_BetterCombat player_bc = (EntityPlayer_BetterCombat)player;
        AttackHand hand = player_bc.getCurrentAttack();
        if (hand == null) return value;
        ExpandedAttack expandedAttack = (ExpandedAttack)(Object)hand.attack();
        return value + expandedAttack.antisBetterCombatOverhauls$getKnockback();
    }

    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
    private float injectNewDamage(float value) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityPlayer_BetterCombat player_bc = (EntityPlayer_BetterCombat)player;
        AttackHand hand = player_bc.getCurrentAttack();
        if (hand == null) return value;
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)hand.attributes();
        if (expandedAttributes.antisBetterCombatOverhauls$getCriticalMultiplier() == 0) return value;
        return (float) expandedAttributes.antisBetterCombatOverhauls$getCriticalMultiplier();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void postTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getWorld().isClient()) {
            ((VersatileAnimatedPlayer)player).antisBetterCombatOverhauls$updateVersatilePose();
        }
    }
}
