package me.anticode.abco.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.abco.api.ExpandedWeaponAttributes;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = WeaponAttributeTooltip.class, remap = false)
public class WeaponAttributeTooltipMixin {
    @Inject(method = "lambda$initialize$0", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    private static void test(ItemStack itemStack, TooltipContext context, List lines, CallbackInfo ci, @Local WeaponAttributes attributes, @Local(ordinal = 0) int index, @Local(ordinal = 2) int lines2) {
        ExpandedWeaponAttributes expandedAttributes = (ExpandedWeaponAttributes)(Object)attributes;
        if (expandedAttributes.antisBetterCombatOverhauls$getVersatile()) {
            double versatile_damage = expandedAttributes.antisBetterCombatOverhauls$getVersatileDamage();
            if (versatile_damage > 0)
                lines.add(index, Text.literal(" ").append(Text.translatable("attribute.modifier.equals.0", new Object[]{ItemStack.MODIFIER_FORMAT.format(versatile_damage), Text.translatable("weapon_attributes.name.versatile_damage")}).formatted(Formatting.DARK_GREEN)));
            else
                lines.add(index, Text.literal(" ").append(Text.translatable("weapon_attributes.name.versatile").formatted(Formatting.DARK_GREEN)));
            double critical_multiplier = expandedAttributes.antisBetterCombatOverhauls$getCriticalMultiplier();
            if (critical_multiplier == 0) critical_multiplier = 1.5F;
            lines.add(index + 1, Text.literal(" ").append(Text.translatable("attribute.modifier.equals.0", new Object[]{ItemStack.MODIFIER_FORMAT.format(critical_multiplier), Text.translatable("weapon_attributes.name.critical_multiplier")}).formatted(Formatting.DARK_GREEN)));
        }
        double critical_multiplier = expandedAttributes.antisBetterCombatOverhauls$getCriticalMultiplier();
        if (critical_multiplier == 0) critical_multiplier = 1.5F;
        lines.add(index + 1, Text.literal(" ").append(Text.translatable("attribute.modifier.equals.0", new Object[]{ItemStack.MODIFIER_FORMAT.format(critical_multiplier), Text.translatable("weapon_attributes.name.critical_multiplier")}).formatted(Formatting.DARK_GREEN)));
    }
}
