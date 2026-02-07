package com.github.hitman20081.dagmod.gui;

import com.github.hitman20081.dagmod.networking.QuestSyncPacket;
import com.github.hitman20081.dagmod.quest.ClientQuestData;
import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.font.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class QuestBookScreen extends Screen {
    private final QuestData.QuestBookTier tier;
    private int currentPage = 0;
    private static final int TOTAL_PAGES = 6;

    private static final String[] PAGE_TITLES = {
            "Quest Book Overview",
            "Active Quests",
            "Available Quests",
            "Quest Statistics",
            "Quest Chains",
            "Achievements"
    };

    public QuestBookScreen(QuestData.QuestBookTier tier) {
        super(Text.literal(tier.getDisplayName()));
        this.tier = tier;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("< Previous"), button -> {
            if (currentPage > 0) {
                currentPage--;
            }
        }).dimensions(this.width / 2 - 150, this.height - 30, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), button -> {
            if (currentPage < TOTAL_PAGES - 1) {
                currentPage++;
            }
        }).dimensions(this.width / 2 - 40, this.height - 30, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(this.width / 2 + 70, this.height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int bookX = (this.width - 192) / 2;
        int bookY = (this.height - 192) / 2;

        // Book background
        context.fill(bookX, bookY, bookX + 192, bookY + 192, 0xFFF5DEB3);
        drawBorder(context, bookX, bookY, 192, 192, 0xFF8B4513);

        // Inner page
        context.fill(bookX + 10, bookY + 10, bookX + 182, bookY + 182, 0xFFFFFFF8);

        var font = this.client.textRenderer;
        int textX = bookX + 20;
        int textY = bookY + 20;

        // Page title
        String pageTitle = PAGE_TITLES[currentPage];
        context.drawText(font, Text.literal(pageTitle), textX, textY, 0xFF000000, false);
        context.drawText(font, Text.literal("Page " + (currentPage + 1) + "/" + TOTAL_PAGES),
                bookX + 120, textY, 0xFF666666, false);
        textY += 25;

        if (currentPage >= TOTAL_PAGES) {
            currentPage = TOTAL_PAGES - 1;
        }

        switch (currentPage) {
            case 0 -> renderOverviewPage(context, font, textX, textY);
            case 1 -> renderActiveQuestsPage(context, font, textX, textY);
            case 2 -> renderAvailableQuestsPage(context, font, textX, textY);
            case 3 -> renderStatisticsPage(context, font, textX, textY);
            case 4 -> renderQuestChainsPage(context, font, textX, textY);
            case 5 -> renderAchievementsPage(context, font, textX, textY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderOverviewPage(DrawContext context, TextRenderer font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.drawText(font, Text.literal("Quest Book Information:"), textX, textY, 0xFF444444, false);
        textY += 15;

        context.drawText(font, Text.literal("Tier: " + data.getTier().getDisplayName()), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Active: " + data.getActiveQuestCount() + "/" + data.getMaxActiveQuests()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Completed: " + data.getTotalCompleted()),
                textX, textY, 0xFF000000, false);
        textY += 20;

        context.drawText(font, Text.literal("Available Difficulties:"), textX, textY, 0xFF444444, false);
        textY += 15;

        for (var difficulty : data.getTier().getAllowedDifficulties()) {
            context.drawText(font, Text.literal("- " + difficulty.getDisplayName()), textX, textY, 0xFF000000, false);
            textY += 12;
        }

        textY += 15;
        context.drawText(font, Text.literal("Navigation:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.drawText(font, Text.literal("Use Previous/Next buttons"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.drawText(font, Text.literal("to browse quest pages"), textX, textY, 0xFF666666, false);
    }

    private void renderActiveQuestsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();
        List<QuestSyncPacket.QuestInfo> activeQuests = data.getActiveQuests();
        int maxY = textY + 120; // bottom boundary for content

        context.drawText(font, Text.literal("Currently Active Quests:"), textX, textY, 0xFF444444, false);
        textY += 18;

        if (activeQuests.isEmpty()) {
            context.drawText(font, Text.literal("No active quests"), textX, textY, 0xFF666666, false);
            textY += 15;
            context.drawText(font, Text.literal("Visit a Quest Block to"), textX, textY, 0xFF888888, false);
            textY += 10;
            context.drawText(font, Text.literal("accept new quests!"), textX, textY, 0xFF888888, false);
            return;
        }

        int questsShown = 0;
        for (int i = 0; i < activeQuests.size(); i++) {
            if (textY + 10 > maxY) break;
            QuestSyncPacket.QuestInfo quest = activeQuests.get(i);

            int difficultyColor = quest.difficulty().getColor();
            context.drawText(font, Text.literal(quest.name()), textX, textY, difficultyColor, false);
            textY += 10;

            String progress = quest.objectivesComplete() + "/" + quest.totalObjectives() + " objectives";
            if (quest.isCompleted()) {
                progress += " - Ready!";
            }
            context.drawText(font, Text.literal(progress), textX + 5, textY, 0xFF666666, false);
            textY += 10;

            // Show first objective (1 line only)
            if (!quest.objectiveDescriptions().isEmpty() && textY + 8 <= maxY) {
                textY = drawWrappedText(context, font, "- " + quest.objectiveDescriptions().get(0), textX + 5, textY, 135, 1, 0xFF000000);
            }

            textY += 8;
            questsShown++;
        }

        if (questsShown < activeQuests.size() && textY + 8 <= maxY) {
            context.drawText(font, Text.literal("+" + (activeQuests.size() - questsShown) + " more..."),
                    textX, textY, 0xFF888888, false);
        }
    }

    private void renderAvailableQuestsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();
        List<QuestSyncPacket.QuestInfo> availableQuests = data.getAvailableQuests();
        int maxY = textY + 120; // bottom boundary for content

        context.drawText(font, Text.literal("Available Quests:"), textX, textY, 0xFF444444, false);
        textY += 18;

        if (availableQuests.isEmpty()) {
            context.drawText(font, Text.literal("No quests available"), textX, textY, 0xFF666666, false);
            textY += 15;
            context.drawText(font, Text.literal("Complete more quests to"), textX, textY, 0xFF888888, false);
            textY += 10;
            context.drawText(font, Text.literal("unlock new ones!"), textX, textY, 0xFF888888, false);
            return;
        }

        int questsShown = 0;
        for (int i = 0; i < availableQuests.size(); i++) {
            if (textY + 10 > maxY) break;
            QuestSyncPacket.QuestInfo quest = availableQuests.get(i);

            // Quest name with difficulty color (truncate if too long)
            int difficultyColor = quest.difficulty().getColor();
            String questName = quest.name();
            if (font.getWidth(questName) > 150) {
                while (font.getWidth(questName + "...") > 150 && questName.length() > 10) {
                    questName = questName.substring(0, questName.length() - 1);
                }
                questName = questName + "...";
            }
            context.drawText(font, Text.literal(questName), textX, textY, difficultyColor, false);
            textY += 10;

            // Description - 1 line only to save space
            if (textY + 8 <= maxY) {
                textY = drawWrappedText(context, font, quest.description(), textX + 5, textY, 140, 1, 0xFF666666);
            }

            // Objective count
            if (textY + 8 <= maxY) {
                String objectives = quest.totalObjectives() + " objective" + (quest.totalObjectives() != 1 ? "s" : "");
                context.drawText(font, Text.literal(objectives), textX + 5, textY, 0xFF888888, false);
                textY += 10;
            }

            textY += 4;
            questsShown++;
        }

        if (questsShown < availableQuests.size() && textY + 8 <= maxY) {
            context.drawText(font, Text.literal("+" + (availableQuests.size() - questsShown) + " more available"),
                    textX, textY, 0xFF888888, false);
        }
    }

    private void renderStatisticsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.drawText(font, Text.literal("Quest Statistics:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.drawText(font, Text.literal("Total Completed: " + data.getTotalCompleted()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Currently Active: " + data.getActiveQuestCount()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Quest Slots: " + data.getActiveQuestCount() + "/" + data.getMaxActiveQuests()),
                textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Book Tier: " + data.getTier().getDisplayName()),
                textX, textY, 0xFF000000, false);
        textY += 25;

        // Calculate success rate
        int total = data.getTotalCompleted() + data.getActiveQuestCount();
        int successRate = total > 0 ? (data.getTotalCompleted() * 100 / total) : 100;
        context.drawText(font, Text.literal("Success Rate: " + successRate + "%"),
                textX, textY, 0xFF00AA00, false);
    }

    private void renderQuestChainsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Quest Chains:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.drawText(font, Text.literal("Quest chains are series of"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.drawText(font, Text.literal("connected quests that tell"), textX, textY, 0xFF666666, false);
        textY += 10;
        context.drawText(font, Text.literal("a story and grant rewards."), textX, textY, 0xFF666666, false);
        textY += 20;

        context.drawText(font, Text.literal("Complete chains to unlock:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.drawText(font, Text.literal("- Higher quest book tiers"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("- Special rewards"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("- New quest lines"), textX, textY, 0xFF000000, false);
    }

    private void renderAchievementsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        ClientQuestData data = ClientQuestData.getInstance();

        context.drawText(font, Text.literal("Achievements:"), textX, textY, 0xFF444444, false);
        textY += 20;

        // Achievement 1: First Steps
        boolean hasCompleted = data.getTotalCompleted() > 0;
        int color = hasCompleted ? 0xFF00AA00 : 0xFF666666;
        String check = hasCompleted ? "✓" : "✗";

        context.drawText(font, Text.literal("First Steps"), textX, textY, color, false);
        textY += 12;
        context.drawText(font, Text.literal(check + " Complete first quest"), textX + 5, textY, color, false);
        textY += 18;

        // Achievement 2: Novice Complete
        boolean hasFive = data.getTotalCompleted() >= 5;
        color = hasFive ? 0xFF00AA00 : 0xFF666666;
        check = hasFive ? "✓" : "✗";

        context.drawText(font, Text.literal("Novice Complete"), textX, textY, color, false);
        textY += 12;
        context.drawText(font, Text.literal(check + " Complete 5 quests"), textX + 5, textY, color, false);
        textY += 18;

        // Achievement 3: Apprentice Complete
        boolean hasTen = data.getTotalCompleted() >= 10;
        color = hasTen ? 0xFF00AA00 : 0xFF666666;
        check = hasTen ? "✓" : "✗";

        context.drawText(font, Text.literal("Apprentice Complete"), textX, textY, color, false);
        textY += 12;
        context.drawText(font, Text.literal(check + " Complete 10 quests"), textX + 5, textY, color, false);
    }

    // Add this helper method at the end of the class, before the closing brace:

    private void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
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
    private List<String> wrapText(TextRenderer font, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() > 0
                    ? currentLine.toString() + " " + word
                    : word;

            if (font.getWidth(testLine) <= maxWidth) {
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
    private int drawWrappedText(DrawContext context, TextRenderer font, String text,
                                int x, int y, int maxWidth, int maxLines, int color) {
        List<String> lines = wrapText(font, text, maxWidth);
        int linesDrawn = 0;

        for (int i = 0; i < lines.size() && linesDrawn < maxLines; i++) {
            String line = lines.get(i);

            // If this is the last line we can draw and there are more lines, add "..."
            if (i == maxLines - 1 && lines.size() > maxLines) {
                // Truncate to fit "..."
                while (font.getWidth(line + "...") > maxWidth && line.length() > 3) {
                    line = line.substring(0, line.length() - 1);
                }
                line = line + "...";
            }

            context.drawText(font, Text.literal(line), x, y, color, false);
            y += 8; // Line height
            linesDrawn++;
        }

        return y;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}