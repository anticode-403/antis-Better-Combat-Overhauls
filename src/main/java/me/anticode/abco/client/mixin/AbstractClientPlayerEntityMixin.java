package me.anticode.abco.client.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import me.anticode.abco.api.VersatileAnimatedPlayer;
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
public abstract class AbstractClientPlayerEntityMixin implements VersatileAnimatedPlayer {
    private final PoseSubStack versatileBodyPose = new PoseSubStack((AbstractModifier) null, true, true);
    private final PoseSubStack versatileHandPose = new PoseSubStack((AbstractModifier) null, false, true);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addVersatilePoseStacks(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        AnimationStack stack = ((IAnimatedPlayer)this).getAnimationStack();
        stack.addAnimLayer(5, versatileHandPose.base);
        stack.addAnimLayer(6, versatileBodyPose.base);
    }

    public void antisBetterCombatOverhauls$updateVersatilePose() {
        PlayerEntity player = (PlayerEntity)(Object) this;
        boolean isLeftHanded = player.getMainArm() == Arm.LEFT;
        BCOverhauls.LOGGER.debug("Checking versatile pose!");
        ItemStack itemStack = player.getMainHandStack();
        if (player.handSwinging || player.isSwimming() || player.isUsingItem() || Platform.isCastingSpell(player) || itemStack == null) {
            versatileBodyPose.setPose(null, isLeftHanded);
            versatileHandPose.setPose(null, isLeftHanded);
        } else {
            WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
            if (attributes == null) {
                versatileBodyPose.setPose(null, isLeftHanded);
                versatileHandPose.setPose(null, isLeftHanded);
                return;
            }
            ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
            if (expandedAttributes.antisBetterCombatOverhauls$getVersatile() && expandedAttributes.antisBetterCombatOverhauls$hasVersatilePose()) {
                BCOverhauls.LOGGER.debug("Adding versatile pose");
                if (player.getOffHandStack().isEmpty()) {
                    versatileBodyPose.setPose(AnimationRegistry.animations.get(expandedAttributes.antisBetterCombatOverhauls$getVersatilePose()), isLeftHanded);
                    versatileHandPose.setPose(AnimationRegistry.animations.get(expandedAttributes.antisBetterCombatOverhauls$getVersatilePose()), isLeftHanded);
                }
            }
        }
    }

    private void addVersatilePose(PoseSubStack instance, KeyframeAnimation animation, boolean isLeftHanded, @Local PlayerEntity player) {

    }
}
