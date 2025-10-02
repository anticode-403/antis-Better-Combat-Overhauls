package me.anticode.abco.network;

import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.AbcoPlayerUpdate;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AbcoPackets {
    public record C2S_PlayerUpdaterRequest(int playerId, boolean heavyAttack, int comboCount) {
        public static Identifier ID = new Identifier(BCOverhauls.modID, "player_update");

        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeBoolean(heavyAttack);
            buffer.writeInt(comboCount);
            return buffer;
        }

        public static AbcoPlayerUpdate read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            boolean heavyAttack = buffer.readBoolean();
            int comboCount = buffer.readInt();
            return new AbcoPlayerUpdate(playerId, heavyAttack, comboCount);
        }
    }
}
