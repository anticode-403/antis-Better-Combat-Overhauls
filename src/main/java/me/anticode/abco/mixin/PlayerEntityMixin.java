package me.anticode.abco.mixin;

import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

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
        ExpandedAttack expandedAttack = (ExpandedAttack)(Object)player_bc.getCurrentAttack().attack();
        return expandedAttack.antisBetterCombatOverhauls$getCritical();
    }
}
