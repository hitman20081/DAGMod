package com.github.hitman20081.dagmod.gui;

import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import com.github.hitman20081.dagmod.quest.Quest;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.Font;

import java.util.ArrayList;
import java.util.List;

public class QuestBookScreen extends Screen {
    private final QuestData.QuestBookTier tier;
    private int currentPage = 0;
    private int selectedQuestIndex = 0;
    private static final int TOTAL_PAGES = 6;

    private Button prevQuestButton;
    private Button nextQuestButton;

    private static final String[] PAGE_TITLES = {
            "Quest Book Overview",
            "Active Quests",
            "Available Quests",
            "Quest Statistics",
            "Quest Chains",
            "Achievements"
    };

    public QuestBookScreen(QuestData.QuestBookTier tier) {
        super(Component.literal(tier.getDisplayName()));
        this.tier = tier;
    }

    @Override
    protected void init() {
        super.init();

        int bookX = (this.width - 192) / 2;
        int bookY = (this.height - 192) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("< Previous"), button -> {
            if (currentPage > 0) {
                currentPage--;
                selectedQuestIndex = 0;
            }
        }).bounds(this.width / 2 - 150, this.height - 30, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Next >"), button -> {
            if (currentPage < TOTAL_PAGES - 1) {
                currentPage++;
                selectedQuestIndex = 0;
            }
        }).bounds(this.width / 2 - 40, this.height - 30, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> {
            this.onClose();
        }).bounds(this.width / 2 + 70, this.height - 30, 80, 20).build());

        // Quest navigation buttons — only visible on the Active Quests page
        prevQuestButton = Button.builder(Component.literal("<"), button -> {
            if (selectedQuestIndex > 0) selectedQuestIndex--;
        }).bounds(bookX + 12, bookY + 168, 20, 12).build();

        nextQuestButton = Button.builder(Component.literal(">"), button -> {
            int size = ClientQuestData.getInstance().getActiveQuests().size();
            if (selectedQuestIndex < size - 1) selectedQuestIndex++;
        }).bounds(bookX + 160, bookY + 168, 20, 12).build();

        prevQuestButton.visible = false;
        nextQuestButton.visible = false;
        this.addRenderableWidget(prevQuestButton);
        this.addRenderableWidget(nextQuestButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int bookX = (this.width - 192) / 2;
        int bookY = (this.height - 192) / 2;

        // Book background
        context.fill(bookX, bookY, bookX + 192, bookY + 192, 0xFFF5DEB3);
        drawBorder(context, bookX, bookY, 192, 192, 0xFF8B4513);

        // Inner page
        context.fill(bookX + 10, bookY + 10, bookX + 182, bookY + 182, 0xFFFFFFF8);

        var font = this.font;
        int textX = bookX + 20;
        int textY = bookY + 20;

        // Page title
        String pageTitle = PAGE_TITLES[currentPage];
        context.text(font, Component.literal(pageTitle), textX, textY, 0xFF000000, false);
        context.text(font, Component.literal("Page " + (currentPage + 1) + "/" + TOTAL_PAGES),
                bookX + 120, textY, 0xFF666666, false);
        textY += 25;

        if (currentPage >= TOTAL_PAGES) {
            currentPage = TOTAL_PAGES - 1;
        }

        // Show quest nav buttons only on the Active Quests page
        boolean onActiveQuests = currentPage == 1;
        if (prevQuestButton != null) prevQuestButton.visible = onActiveQuests;
        if (nextQuestButton != null) nextQuestButton.visible = onActiveQuests;

        switch (currentPage) {
            case 0 -> renderOverviewPage(context, font, textX, textY);
            case 1 -> renderActiveQuestsPage(context, font, textX, textY);
            case 2 -> renderAvailableQuestsPage(context, font, textX, textY);
            case 3 -> renderStatisticsPage(context, font, textX, textY);
            case 4 -> renderQuestChainsPage(context, font, textX, textY);
            case 5 -> renderAchievementsPage(context, font, textX, textY);
        }

        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    private void renderOverviewPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.text(font, Component.literal("Quest Book Information:"), textX, textY, 0xFF444444, false);
        textY += 15;

        context.text(font, Component.literal("Tier: " + data.getTier().getDisplayName()), textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("Active: " + data.getActiveQuestCount() + "/" + data.getMaxActiveQuests()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("Completed: " + data.getTotalCompleted()),
                textX, textY, 0xFF000000, false);
        textY += 20;

        context.text(font, Component.literal("Available Difficulties:"), textX, textY, 0xFF444444, false);
        textY += 15;

        for (var difficulty : data.getTier().getAllowedDifficulties()) {
            context.text(font, Component.literal("- " + difficulty.getDisplayName()), textX, textY, 0xFF000000, false);
            textY += 12;
        }

        textY += 15;
        context.text(font, Component.literal("Navigation:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.text(font, Component.literal("Use Previous/Next buttons"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.text(font, Component.literal("to browse quest pages"), textX, textY, 0xFF666666, false);
    }

    private void renderActiveQuestsPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();
        List<QuestSyncPacket.QuestInfo> activeQuests = data.getActiveQuests();
        int maxY = textY + 140; // bottom boundary — leaves room for nav buttons

        // Clamp index in case a quest was completed while the book was open
        if (selectedQuestIndex >= activeQuests.size()) {
            selectedQuestIndex = Math.max(0, activeQuests.size() - 1);
        }

        if (activeQuests.isEmpty()) {
            context.text(font, Component.literal("No active quests"), textX, textY, 0xFF666666, false);
            textY += 15;
            context.text(font, Component.literal("Visit a Quest Block to"), textX, textY, 0xFF888888, false);
            textY += 10;
            context.text(font, Component.literal("accept new quests!"), textX, textY, 0xFF888888, false);
            return;
        }

        QuestSyncPacket.QuestInfo quest = activeQuests.get(selectedQuestIndex);

        // Quest counter and name
        String counter = "Quest " + (selectedQuestIndex + 1) + " of " + activeQuests.size();
        context.text(font, Component.literal(counter), textX, textY, 0xFF888888, false);
        textY += 11;

        String categoryTag = categoryTag(quest.category());
        if (categoryTag != null) {
            context.text(font, Component.literal(categoryTag), textX, textY, categoryColor(quest.category()), false);
            textY += 10;
        }

        context.text(font, Component.literal(quest.name()), textX, textY, quest.difficulty().getColor(), false);
        textY += 11;

        // Description
        if (!quest.description().isBlank()) {
            textY = drawWrappedText(context, font, quest.description(), textX, textY, 152, 2, 0xFF555555);
            textY += 4;
        }

        // Progress
        String progress = quest.objectivesComplete() + "/" + quest.totalObjectives() + " objectives";
        int progressColor = quest.isCompleted() ? 0xFF00AA00 : 0xFF666666;
        if (quest.isCompleted()) progress += " - Ready to turn in!";
        context.text(font, Component.literal(progress), textX, textY, progressColor, false);
        textY += 12;

        // Divider
        context.fill(textX, textY, textX + 152, textY + 1, 0xFFCCBBA0);
        textY += 5;

        // All objectives
        for (String obj : quest.objectiveDescriptions()) {
            if (textY + 8 > maxY) break;
            textY = drawWrappedText(context, font, "- " + obj, textX + 2, textY, 150, 2, 0xFF222222);
            textY += 2;
        }
    }

    private void renderAvailableQuestsPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();
        List<QuestSyncPacket.QuestInfo> availableQuests = data.getAvailableQuests();
        int maxY = textY + 120; // bottom boundary for content

        context.text(font, Component.literal("Available Quests:"), textX, textY, 0xFF444444, false);
        textY += 18;

        if (availableQuests.isEmpty()) {
            context.text(font, Component.literal("No quests available"), textX, textY, 0xFF666666, false);
            textY += 15;
            context.text(font, Component.literal("Complete more quests to"), textX, textY, 0xFF888888, false);
            textY += 10;
            context.text(font, Component.literal("unlock new ones!"), textX, textY, 0xFF888888, false);
            return;
        }

        int questsShown = 0;
        for (int i = 0; i < availableQuests.size(); i++) {
            if (textY + 10 > maxY) break;
            QuestSyncPacket.QuestInfo quest = availableQuests.get(i);

            // Quest name with difficulty color (truncate if too long)
            int difficultyColor = quest.difficulty().getColor();
            String tag = categoryTag(quest.category());
            String questName = tag != null ? tag + " " + quest.name() : quest.name();
            if (font.width(questName) > 150) {
                while (font.width(questName + "...") > 150 && questName.length() > 10) {
                    questName = questName.substring(0, questName.length() - 1);
                }
                questName = questName + "...";
            }
            context.text(font, Component.literal(questName), textX, textY, difficultyColor, false);
            textY += 10;

            // Description - 1 line only to save space
            if (textY + 8 <= maxY) {
                textY = drawWrappedText(context, font, quest.description(), textX + 5, textY, 140, 1, 0xFF666666);
            }

            // Objective count
            if (textY + 8 <= maxY) {
                String objectives = quest.totalObjectives() + " objective" + (quest.totalObjectives() != 1 ? "s" : "");
                context.text(font, Component.literal(objectives), textX + 5, textY, 0xFF888888, false);
                textY += 10;
            }

            textY += 4;
            questsShown++;
        }

        if (questsShown < availableQuests.size() && textY + 8 <= maxY) {
            context.text(font, Component.literal("+" + (availableQuests.size() - questsShown) + " more available"),
                    textX, textY, 0xFF888888, false);
        }
    }

    /** Short bracketed label shown next to Daily/Job quests so they're distinguishable from story quests. Null for other categories. */
    private static String categoryTag(Quest.QuestCategory category) {
        return switch (category) {
            case DAILY -> "[DAILY]";
            case JOB -> "[JOB]";
            default -> null;
        };
    }

    private static int categoryColor(Quest.QuestCategory category) {
        return switch (category) {
            case DAILY -> 0xFF0099CC;
            case JOB -> 0xFF996633;
            default -> 0xFF666666;
        };
    }

    private void renderStatisticsPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.text(font, Component.literal("Quest Statistics:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.text(font, Component.literal("Total Completed: " + data.getTotalCompleted()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("Currently Active: " + data.getActiveQuestCount()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("Quest Slots: " + data.getActiveQuestCount() + "/" + data.getMaxActiveQuests()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("Book Tier: " + data.getTier().getDisplayName()),
                textX, textY, 0xFF000000, false);
        textY += 25;

        // Calculate success rate
        int total = data.getTotalCompleted() + data.getActiveQuestCount();
        int successRate = total > 0 ? (data.getTotalCompleted() * 100 / total) : 100;
        context.text(font, Component.literal("Success Rate: " + successRate + "%"),
                textX, textY, 0xFF00AA00, false);
    }

    private void renderQuestChainsPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        context.text(font, Component.literal("Quest Chains:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.text(font, Component.literal("Quest chains are series of"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.text(font, Component.literal("connected quests that tell"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.text(font, Component.literal("a story and grant rewards."), textX, textY, 0xFF666666, false);
        textY += 20;

        context.text(font, Component.literal("Complete chains to unlock:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.text(font, Component.literal("- Higher quest book tiers"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("- Special rewards"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.text(font, Component.literal("- New quest lines"), textX, textY, 0xFF000000, false);
    }

    private void renderAchievementsPage(GuiGraphicsExtractor context, Font font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.text(font, Component.literal("Achievements:"), textX, textY, 0xFF444444, false);
        textY += 20;

        // Achievement 1: First Steps
        boolean hasCompleted = data.getTotalCompleted() > 0;
        int color = hasCompleted ? 0xFF00AA00 : 0xFF666666;
        String check = hasCompleted ? "✓" : "✗";

        context.text(font, Component.literal("First Steps"), textX, textY, color, false);
        textY += 12;
        context.text(font, Component.literal(check + " Complete first quest"), textX + 5, textY, color, false);
        textY += 18;

        // Achievement 2: Novice Complete
        boolean hasFive = data.getTotalCompleted() >= 5;
        color = hasFive ? 0xFF00AA00 : 0xFF666666;
        check = hasFive ? "✓" : "✗";

        context.text(font, Component.literal("Novice Complete"), textX, textY, color, false);
        textY += 12;
        context.text(font, Component.literal(check + " Complete 5 quests"), textX + 5, textY, color, false);
        textY += 18;

        // Achievement 3: Apprentice Complete
        boolean hasTen = data.getTotalCompleted() >= 10;
        color = hasTen ? 0xFF00AA00 : 0xFF666666;
        check = hasTen ? "✓" : "✗";

        context.text(font, Component.literal("Apprentice Complete"), textX, textY, color, false);
        textY += 12;
        context.text(font, Component.literal(check + " Complete 10 quests"), textX + 5, textY, color, false);
    }

    // Add this helper method at the end of the class, before the closing brace:

    private void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color); // Top
        context.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        context.fill(x, y, x + 1, y + height, color); // Left
        context.fill(x + width - 1, y, x + width, y + height, color); // Right
    }


    // ========== TEXT WRAPPING UTILITY METHODS ==========

    /**
     * Wraps text to fit within a maximum width, returning a list of lines.
     * @param font The text renderer
     * @param text The text to wrap
     * @param maxWidth Maximum width in pixels
     * @return List of wrapped text lines
     */
    private List<String> wrapText(Font font, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() > 0
                    ? currentLine.toString() + " " + word
                    : word;

            if (font.width(testLine) <= maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // Current line is full, start new line
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Single word is too long, add it anyway and truncate
                    lines.add(word);
                }
            }
        }

        // Add the last line
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    /**
     * Draws wrapped text with a maximum number of lines.
     * If text exceeds maxLines, the last line ends with "..."
     * @return The final Y position after drawing all lines
     */
    private int drawWrappedText(GuiGraphicsExtractor context, Font font, String text,
                                int x, int y, int maxWidth, int maxLines, int color) {
        List<String> lines = wrapText(font, text, maxWidth);
        int linesDrawn = 0;

        for (int i = 0; i < lines.size() && linesDrawn < maxLines; i++) {
            String line = lines.get(i);

            // If this is the last line we can draw and there are more lines, add "..."
            if (i == maxLines - 1 && lines.size() > maxLines) {
                // Truncate to fit "..."
                while (font.width(line + "...") > maxWidth && line.length() > 3) {
                    line = line.substring(0, line.length() - 1);
                }
                line = line + "...";
            }

            context.text(font, Component.literal(line), x, y, color, false);
            y += 8; // Line height
            linesDrawn++;
        }

        return y;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}