package me.anticode.abco;

import me.anticode.abco.api.ABCOPlayerEntity;
import me.anticode.abco.api.ExpandedAttackHand;
import me.anticode.abco.api.HeavyAttackComboApi;
import me.anticode.abco.network.AbcoServerNetwork;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BCOverhauls implements ModInitializer {

    public static String modID = "abco";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);


    @Override
    public void onInitialize() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modID);
        modContainer.ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                new Identifier(modID, "abco"), container,
                Text.translatable("pack.abco.abco"),
                ResourcePackActivationType.ALWAYS_ENABLED
        ));
        AbcoServerNetwork.initialize();
    }
}
