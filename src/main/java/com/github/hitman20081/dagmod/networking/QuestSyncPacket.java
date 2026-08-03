package com.github.hitman20081.dagmod.networking;

import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record QuestSyncPacket(
        QuestData.QuestBookTier tier,
        int activeQuestCount,
        int maxActiveQuests,
        int totalCompleted,
        List<QuestInfo> activeQuests,
        List<QuestInfo> availableQuests  // NEW: Added available quests
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<QuestSyncPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("dagmod", "quest_sync"));

    public static final StreamCodec<FriendlyByteBuf, QuestSyncPacket> CODEC =
            CustomPacketPayload.codec(QuestSyncPacket::write, QuestSyncPacket::new);

    public QuestSyncPacket(FriendlyByteBuf buf) {
        this(
                QuestData.QuestBookTier.values()[buf.readInt()],
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readList(QuestInfo::new),
                buf.readList(QuestInfo::new)  // NEW: Read available quests
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tier.getTier() - 1);
        buf.writeInt(activeQuestCount);
        buf.writeInt(maxActiveQuests);
        buf.writeInt(totalCompleted);
        buf.writeCollection(activeQuests, (buffer, quest) -> quest.write(buffer));
        buf.writeCollection(availableQuests, (buffer, quest) -> quest.write(buffer));  // NEW: Write available quests
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static record QuestInfo(
            String id,
            String name,
            String description,
            Quest.QuestDifficulty difficulty,
            Quest.QuestCategory category,
            int objectivesComplete,
            int totalObjectives,
            boolean isCompleted,
            List<String> objectiveDescriptions
    ) {
        public QuestInfo(FriendlyByteBuf buf) {
            this(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    Quest.QuestDifficulty.values()[buf.readInt()],
                    Quest.QuestCategory.values()[buf.readInt()],
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readList(FriendlyByteBuf::readUtf)
            );
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(id);
            buf.writeUtf(name);
            buf.writeUtf(description);
            buf.writeInt(difficulty.ordinal());
            buf.writeInt(category.ordinal());
            buf.writeInt(objectivesComplete);
            buf.writeInt(totalObjectives);
            buf.writeBoolean(isCompleted);
            buf.writeCollection(objectiveDescriptions, FriendlyByteBuf::writeUtf);
        }
    }
}