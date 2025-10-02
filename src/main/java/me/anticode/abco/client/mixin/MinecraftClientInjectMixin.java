package me.anticode.abco.client.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.ABCOPlayerEntity;
import me.anticode.abco.api.ExpandedAttack;
import me.anticode.abco.api.ExpandedAttackHand;
import me.anticode.abco.api.HeavyAttackComboApi;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import me.anticode.abco.network.AbcoPackets;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.mixin.client.MinecraftClientAccessor;
import net.bettercombat.mixin.client.MinecraftClientInject;
import net.bettercombat.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 1500)
public abstract class MinecraftClientInjectMixin implements HeavyAttackComboApi {
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
            name = "isTargetingMineableBlock()Z"
    )
    @ModifyReturnValue(
            method = "@MixinSquared:Handler",
            at = @At(value = "RETURN")
    )
    private boolean overrideTargetingMineableBlockAttack(boolean original) {
        if (original)
            return targetingMineableBlockTrue();
        else
            return false;
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
            return itemStack.getItem().isSuitableFor(target);
        }
        return false;
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.client.MinecraftClientInject",
            name = "startUpswing(Lnet/bettercombat/api/WeaponAttributes;)V"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking;send(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V"),
            cancellable = true
    )
    private void overwriteBCAttackPackets(WeaponAttributes attributes, CallbackInfo ci, @Local AnimatedHand animatedHand, @Local String animationName, @Local boolean isOffHand, @Local(ordinal=0) float upswingRate, @Local(ordinal=1) float attackCooldownTicksFloat, @Local AttackHand hand) {
        MinecraftClient client = ((MinecraftClient)(Object)this);
        ClientPlayerEntity player = client.player;
        ((PlayerAttackAnimatable)player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);
        ClientPlayNetworking.send(AbcoPackets.C2S_PlayerUpdaterRequest.ID, (new AbcoPackets.C2S_PlayerUpdaterRequest(player.getId(), false, 0, animatedHand, animationName, attackCooldownTicksFloat, upswingRate)).write());
        BetterCombatClientEvents.ATTACK_START.invoke((handler) -> handler.onPlayerAttackStart(player, hand));
        ci.cancel();
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.client.MinecraftClientInject",
            name = "startUpswing(Lnet/bettercombat/api/WeaponAttributes;)V"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(value = "HEAD")
    )
    private void injectInvertWeaponAttack(WeaponAttributes attributes, CallbackInfo ci) {
        try {
            MinecraftClient client = ((MinecraftClient)(Object)this);
            ClientPlayerEntity player = client.player;
            if (player.isRiding()) return;
            ABCOPlayerEntity abcoPlayerEntity = (ABCOPlayerEntity)player;
            assert abcoPlayerEntity != null;
            AttackHand hand = (AttackHand)(MinecraftClient.class.getDeclaredMethod("getCurrentHand").invoke(client));
            float upswingRate = (float)hand.upswingRate();
            if (((int)MinecraftClient.class.getDeclaredField("upswingTicks").get(client)) > 0 || attackCooldown > 0 || player.isUsingItem() || player.getAttackCooldownProgress(0) < (1 - upswingRate)) return;
            abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(false);
        }  catch (Throwable throwable) {
            BCOverhauls.LOGGER.error(throwable.getMessage(), throwable);
        }
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.client.MinecraftClientInject",
            name = "performAttack()V"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking;send(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V")
    )
    private void serverPacketReplace(Identifier identifier, PacketByteBuf buf, @Local AttackHand attackHand) {
        ExpandedAttackHand expandedHand = (ExpandedAttackHand)(Object)attackHand;
        Packets.C2S_AttackRequest attackRequest = Packets.C2S_AttackRequest.read(buf);
        boolean is_heavy = expandedHand.antisBetterCombatOverhauls$isSpecialAttack();
        int combo = is_heavy ? heavyCombo : attackRequest.comboCount();
        ClientPlayNetworking.send(AbcoPackets.C2S_AttackRequest.ID, (new AbcoPackets.C2S_AttackRequest(is_heavy, combo, attackRequest.isSneaking(), attackRequest.selectedSlot(), attackRequest.entityIds()).write()));
    }

    @TargetHandler(
            mixin = "net.bettercombat.mixin.client.MinecraftClientInject",
            name = "setComboCount(I)V"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void redirectComboSetter(int comboCount, CallbackInfo ci) {
        ABCOPlayerEntity abcoPlayer = ((ABCOPlayerEntity)player);
        if (comboCount == 0) return;
        if (abcoPlayer.antisBetterCombatOverhauls$wasLastAttackSpecial()) {
            heavyCombo++;
        } else {
            try {
                ((PlayerAttackProperties) player).setComboCount(comboCount);
            } catch (Throwable throwable) {
                BCOverhauls.LOGGER.error(throwable.getMessage(), throwable);
            }
        }
        ci.cancel();
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
        if (player.isRiding()) return;
        AttackHand hand = ExpandedPlayerAttackHelper.getCurrentHeavyAttack(player, heavyCombo);
        if (hand == null) return;
        float upswingRate = (float)hand.upswingRate();
        try {
            if (((int)MinecraftClient.class.getDeclaredField("upswingTicks").get(client)) > 0 || attackCooldown > 0 || player.isUsingItem() || player.getAttackCooldownProgress(0) < (1 - upswingRate)) return;
            player.stopUsingItem();
            MinecraftClient.class.getDeclaredField("lastAttacked").set(client, 0);
            MinecraftClient.class.getDeclaredField("upswingStack").set(client, player.getMainHandStack());
            float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(player) * (float)((double)1.0F / ((ExpandedAttack)(Object)hand.attack()).antisBetterCombatOverhauls$getAttackSpeedMultiplier());
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
            ClientPlayNetworking.send(AbcoPackets.C2S_PlayerUpdaterRequest.ID, (new AbcoPackets.C2S_PlayerUpdaterRequest(player.getId(), true, heavyCombo, animatedHand, animationName, attackCooldownTicksFloat, upswingRate)).write());
            BetterCombatClientEvents.ATTACK_START.invoke((handler) -> handler.onPlayerAttackStart(player, hand));
            ABCOPlayerEntity abcoPlayerEntity = (ABCOPlayerEntity)player;
            assert abcoPlayerEntity != null;
            abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(true);
        } catch (Throwable throwable) {
            BCOverhauls.LOGGER.error(throwable.getMessage(), throwable);
        }
    }
}
