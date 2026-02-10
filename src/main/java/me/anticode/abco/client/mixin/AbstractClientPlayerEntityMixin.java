package me.anticode.abco.client.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import me.anticode.abco.api.AbcoPlayerEntity;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.AbcoAnimatedPlayer;
import net.bettercombat.Platform;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.animation.AnimationRegistry;
import net.bettercombat.client.animation.PoseSubStack;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractClientPlayerEntity.class, priority = 1500)
public abstract class AbstractClientPlayerEntityMixin implements AbcoAnimatedPlayer {
    private final PoseSubStack alternateBodyPose = new PoseSubStack((AbstractModifier) null, true, true);
    private final PoseSubStack alternateHandPose = new PoseSubStack((AbstractModifier) null, false, true);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addAlternatePoseStacks(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        AnimationStack stack = ((IAnimatedPlayer)this).getAnimationStack();
        stack.addAnimLayer(5, alternateHandPose.base);
        stack.addAnimLayer(6, alternateBodyPose.base);
    }

    public void antisBetterCombatOverhauls$updateAlternatePose() {
        PlayerEntity player = (PlayerEntity)(Object) this;
        boolean isLeftHanded = player.getMainArm() == Arm.LEFT;
        ItemStack itemStack = player.getMainHandStack();
        if (player.handSwinging || player.isSwimming() || player.isUsingItem() || Platform.isCastingSpell(player) || itemStack == null) {
            alternateBodyPose.setPose(null, isLeftHanded);
            alternateHandPose.setPose(null, isLeftHanded);
            return;
        }
        WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
        if (attributes == null) {
            alternateBodyPose.setPose(null, isLeftHanded);
            alternateHandPose.setPose(null, isLeftHanded);
            return;
        }
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (expandedAttributes.antisBetterCombatOverhauls$getVersatile() && expandedAttributes.antisBetterCombatOverhauls$hasAlternatePose() && player.getOffHandStack().isEmpty()) {
            if (player.getOffHandStack().isEmpty()) {
                alternateBodyPose.setPose(AnimationRegistry.animations.get(expandedAttributes.antisBetterCombatOverhauls$getAlternatePose()), isLeftHanded);
                alternateHandPose.setPose(AnimationRegistry.animations.get(expandedAttributes.antisBetterCombatOverhauls$getAlternatePose()), isLeftHanded);
            }
        } else {
            alternateBodyPose.setPose(null, isLeftHanded);
            alternateHandPose.setPose(null, isLeftHanded);
        }
    }
}
