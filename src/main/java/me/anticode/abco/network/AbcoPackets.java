package me.anticode.abco.network;

import me.anticode.abco.BCOverhauls;
import net.bettercombat.logic.AnimatedHand;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class AbcoPackets {
    public record C2S_AttackRequest (boolean heavy, int comboCount, boolean isSneaking, int selectedSlot, int[] entityIds) {
        public static Identifier ID = new Identifier(BCOverhauls.modID, "request_attack");

        private static int[] convertEntityList(List<Entity> entities) {
            int[] ids = new int[entities.size()];

            for(int i = 0; i < entities.size(); ++i) {
                ids[i] = ((Entity)entities.get(i)).getId();
            }

            return ids;
        }

        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(this.heavy);
            buffer.writeInt(this.comboCount);
            buffer.writeBoolean(this.isSneaking);
            buffer.writeInt(this.selectedSlot);
            buffer.writeIntArray(this.entityIds);
            return buffer;
        }

        public static AbcoPackets.C2S_AttackRequest read(PacketByteBuf buffer) {
            boolean heavy = buffer.readBoolean();
            int comboCount = buffer.readInt();
            boolean isSneaking = buffer.readBoolean();
            int selectedSlot = buffer.readInt();
            int[] ids = buffer.readIntArray();
            return new AbcoPackets.C2S_AttackRequest(heavy, comboCount, isSneaking, selectedSlot, ids);
        }
    }

    public record C2S_PlayerUpdaterRequest(int playerId, boolean heavyAttack, int comboCount, AnimatedHand animatedHand, String animationName, float length, float upswing) {
        public static Identifier ID = new Identifier(BCOverhauls.modID, "player_update");

        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeBoolean(heavyAttack);
            buffer.writeInt(comboCount);
            buffer.writeInt(animatedHand.ordinal());
            buffer.writeString(animationName);
            buffer.writeFloat(length);
            buffer.writeFloat(upswing);
            return buffer;
        }

        public static C2S_PlayerUpdaterRequest read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            boolean heavyAttack = buffer.readBoolean();
            int comboCount = buffer.readInt();
            var animatedHand = AnimatedHand.values()[buffer.readInt()];
            String animationName = buffer.readString();
            float length = buffer.readFloat();
            float upswing = buffer.readFloat();
            return new C2S_PlayerUpdaterRequest(playerId, heavyAttack, comboCount, animatedHand, animationName, length, upswing);
        }
    }

    public record C2S_ParryRequest (int playerId, String animationName, float length) {
        public static Identifier ID = new Identifier(BCOverhauls.modID, "parry_request");

        public PacketByteBuf write() {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(playerId);
            buffer.writeString(animationName);
            buffer.writeFloat(length);
            return buffer;
        }

        public static C2S_ParryRequest read(PacketByteBuf buffer) {
            int playerId = buffer.readInt();
            String animationName = buffer.readString();
            float length = buffer.readFloat();
            return new C2S_ParryRequest(playerId, animationName, length);
        }
    }
}
