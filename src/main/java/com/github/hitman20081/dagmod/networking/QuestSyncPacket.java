package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record QuestSyncPacket(
        QuestData.QuestBookTier tier,
        int activeQuestCount,
        int maxActiveQuests,
        int totalCompleted,
        List<QuestInfo> activeQuests
) implements CustomPayload {

    public static final CustomPayload.Id<QuestSyncPacket> ID =
            new CustomPayload.Id<>(Identifier.of("dagmod", "quest_sync"));

    public static final PacketCodec<PacketByteBuf, QuestSyncPacket> CODEC =
            CustomPayload.codecOf(QuestSyncPacket::write, QuestSyncPacket::new);

    public QuestSyncPacket(PacketByteBuf buf) {
        this(
                QuestData.QuestBookTier.values()[buf.readInt()],
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readList(QuestInfo::new)
        );
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(tier.getTier() - 1);
        buf.writeInt(activeQuestCount);
        buf.writeInt(maxActiveQuests);
        buf.writeInt(totalCompleted);
        buf.writeCollection(activeQuests, (buffer, quest) -> quest.write(buffer));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static record QuestInfo(
            String id,
            String name,
            String description,
            Quest.QuestDifficulty difficulty,
            int objectivesComplete,
            int totalObjectives,
            boolean isCompleted,
            List<String> objectiveDescriptions
    ) {
        public QuestInfo(PacketByteBuf buf) {
            this(
                    buf.readString(),
                    buf.readString(),
                    buf.readString(),
                    Quest.QuestDifficulty.values()[buf.readInt()],
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readList(PacketByteBuf::readString)
            );
        }

        public void write(PacketByteBuf buf) {
            buf.writeString(id);
            buf.writeString(name);
            buf.writeString(description);
            buf.writeInt(difficulty.ordinal());
            buf.writeInt(objectivesComplete);
            buf.writeInt(totalObjectives);
            buf.writeBoolean(isCompleted);
            buf.writeCollection(objectiveDescriptions, PacketByteBuf::writeString);
        }
    }
}