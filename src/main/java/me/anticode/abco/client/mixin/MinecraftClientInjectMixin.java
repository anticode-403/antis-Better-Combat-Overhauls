package me.anticode.abco.client.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.HeavyAttackClientApi;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.mixin.client.MinecraftClientAccessor;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = MinecraftClient.class, priority = 1500)
public abstract class MinecraftClientInjectMixin implements HeavyAttackClientApi {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    public int attackCooldown;
    @Shadow
    private int itemUseCooldown;

    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private int heavyCombo = 0;

    public int antisBetterCombatOverhauls$getHeavyCombo() {
        return this.heavyCombo;
    }
    public void antisBetterCombatOverhauls$setHeavyCombo(int heavyCombo) {
        this.heavyCombo = heavyCombo;
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.client.MinecraftClientInject",
            name = "Lnet/bettercombat/mixin/client/MinecraftClientInject;isTargetingMineableBlock()Z"
    )
    @ModifyReturnValue(
            method = "@MixinSquared:Handler",
            at = @At(value = "RETURN")
    )
    private boolean overrideTargetingMineableBlockAttack(boolean original) {
        return targetingMineableBlockTrue();
    }

    @Unique
    private boolean targetingMineableBlockTrue() {
        MinecraftClient client = (MinecraftClient)(Object)this;
        HitResult crosshairTarget = client.crosshairTarget;
        if (crosshairTarget != null && crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
            BlockState target = world.getBlockState(blockHitResult.getBlockPos());
            if (player == null) return false;
            ItemStack itemStack = player.getMainHandStack();
            if (itemStack == null) return false;
            return itemStack.getItem().getMiningSpeedMultiplier(itemStack, target) != 1;
        }
        return false;
    }

    @Inject(method = "doItemUse", at = @At(value = "HEAD"), cancellable = true)
    private void injectSpecialAttacks(CallbackInfo ci) {
        if (BetterCombatClient.ENABLED) {
            MinecraftClient client = ((MinecraftClient)(Object)this);
            WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandStack());
            if (attributes != null && attributes.attacks() != null) {
                startHeavyUpswing(attributes);
                ci.cancel();
            }
        }
    }

    @Unique
    private void startHeavyUpswing(WeaponAttributes attributes) {
        MinecraftClient client = ((MinecraftClient)(Object)this);
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        BCOverhauls.LOGGER.debug("Starting heavy upswing.");
        if (player.isRiding()) return;
        BCOverhauls.LOGGER.debug("Passed riding check.");
        AttackHand hand = ExpandedPlayerAttackHelper.getCurrentHeavyAttack(player, heavyCombo);
        if (hand == null) return;
        BCOverhauls.LOGGER.debug("Passed hand check.");
        float upswingRate = (float)hand.upswingRate();
        try {
            if (((int)MinecraftClient.class.getDeclaredField("upswingTicks").get(client)) > 0 || attackCooldown > 0 || player.isUsingItem() || player.getAttackCooldownProgress(0) < (1 - upswingRate)) return;
            BCOverhauls.LOGGER.debug("Starting heavy upswing.");
            player.stopUsingItem();
            MinecraftClient.class.getDeclaredField("lastAttacked").set(client, 0);
            MinecraftClient.class.getDeclaredField("upswingStack").set(client, player.getMainHandStack());
            float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
            int attackCooldownTicks = Math.round(attackCooldownTicksFloat);
            MinecraftClient.class.getDeclaredField("comboReset").set(client, Math.round(attackCooldownTicksFloat * BetterCombat.config.combo_reset_rate));
            MinecraftClient.class.getDeclaredField("upswingTicks").set(client, Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1));
            MinecraftClient.class.getDeclaredField("lastSwingDuration").set(client, attackCooldownTicksFloat);
            itemUseCooldown = attackCooldownTicks;
            ((MinecraftClientAccessor)client).setAttackCooldown(attackCooldownTicks);
            String animationName = hand.attack().animation();
            boolean isOffHand = hand.isOffHand();
            AnimatedHand animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());
            ((PlayerAttackAnimatable)player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);
            ClientPlayNetworking.send(Packets.AttackAnimation.ID, (new Packets.AttackAnimation(player.getId(), animatedHand, animationName, attackCooldownTicksFloat, upswingRate)).write());
            BetterCombatClientEvents.ATTACK_START.invoke((handler) -> handler.onPlayerAttackStart(player, hand));
        } catch (Throwable throwable) {
            BCOverhauls.LOGGER.error(throwable.getMessage(), throwable);
        }
    }
}
