package me.anticode.abco.mixin;

import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = WeaponRegistry.class, remap = false)
public abstract class WeaponRegistryMixin {

    @Redirect(method = "loadContainers", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;findResources(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> loadContainers(ResourceManager instance, String s, Predicate<Identifier> identifierPredicate) {
        Map<Identifier, Resource> resources = instance.findResources("weapon_attributes", (fileName) -> fileName.getPath().endsWith(".json"));
        Map<Identifier, Resource> result = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            if (entry.getKey().getNamespace().equals("abco")) {
                Identifier overwrite = new Identifier("bettercombat", entry.getKey().getPath());
                result.put(overwrite, entry.getValue());
            }
        }
        return result;
    }
}
