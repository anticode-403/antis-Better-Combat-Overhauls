package me.anticode.abco.network;

import com.google.common.collect.Iterables;
import me.anticode.abco.api.ABCOPlayerEntity;
import me.anticode.abco.api.AbcoPlayerUpdate;
import me.anticode.abco.api.HeavyAttackComboApi;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;

public class AbcoServerNetwork {
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(AbcoPackets.C2S_PlayerUpdaterRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.getWorld()).orNull();
            if (world == null || world.isClient) return;
            final AbcoPlayerUpdate packet = AbcoPackets.C2S_PlayerUpdaterRequest.read(buf);
            world.getServer().executeSync(() -> {
                ABCOPlayerEntity abcoPlayerEntity = (ABCOPlayerEntity)player;
                HeavyAttackComboApi comboPlayer = (HeavyAttackComboApi)player;
                if (packet.heavyAttack) {
                    comboPlayer.antisBetterCombatOverhauls$setHeavyCombo(packet.comboCount);
                    abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(true);
                } else {
                    abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(false);
                }
            });
        });
    }
}
