package me.anticode.abco.client.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.abco.BCOverhauls;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MinecraftClient.class, priority = 1500)
public abstract class MinecraftClientInjectMixin {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

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
        BCOverhauls.LOGGER.debug("HEEELLLLP!! THE HOUSE IS ON FIIIRRREEEE!!!!");
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
}
