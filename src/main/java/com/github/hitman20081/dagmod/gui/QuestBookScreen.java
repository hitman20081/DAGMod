package com.github.hitman20081.dagmod.gui;

import com.github.hitman20081.dagmod.quest.QuestData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.font.TextRenderer;

public class QuestBookScreen extends Screen {
    private final QuestData.QuestBookTier tier;
    private int currentPage = 0;
    private static final int TOTAL_PAGES = 6; // Overview, Active Quests, Available Quests, Statistics

    // Page names for reference
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

        // Navigation buttons
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
        // Book appearance
        int bookX = (this.width - 192) / 2;
        int bookY = (this.height - 192) / 2;

        // Book background
        context.fill(bookX, bookY, bookX + 192, bookY + 192, 0xFFF5DEB3);
        context.drawBorder(bookX, bookY, 192, 192, 0xFF8B4513);

        // Inner page area
        context.fill(bookX + 10, bookY + 10, bookX + 182, bookY + 182, 0xFFFFFFF8);

        var font = this.client.textRenderer;
        int textX = bookX + 20;
        int textY = bookY + 20;

        // Page title and number
        String pageTitle = PAGE_TITLES[currentPage];
        context.drawText(font, Text.literal(pageTitle), textX, textY, 0xFF000000, false);
        context.drawText(font, Text.literal("Page " + (currentPage + 1) + "/" + TOTAL_PAGES),
                bookX + 120, textY, 0xFF666666, false);
        textY += 25;

        if (currentPage >= TOTAL_PAGES) {
            currentPage = TOTAL_PAGES - 1;
        }

        // Render content based on current page
        switch (currentPage) {
            case 0 -> renderOverviewPage(context, font, textX, textY);
            case 1 -> renderActiveQuestsPage(context, font, textX, textY);
            case 2 -> renderAvailableQuestsPage(context, font, textX, textY);
            case 3 -> renderStatisticsPage(context, font, textX, textY);
            case 4 -> renderQuestChainsPage(context, font, textX, textY);      // NEW
            case 5 -> renderAchievementsPage(context, font, textX, textY);    // NEW
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderOverviewPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Quest Book Information:"), textX, textY, 0xFF444444, false);
        textY += 15;

        context.drawText(font, Text.literal("Tier: " + tier.getDisplayName()), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Max Active Quests: " + tier.getMaxActiveQuests()), textX, textY, 0xFF000000, false);
        textY += 20;

        context.drawText(font, Text.literal("Available Difficulties:"), textX, textY, 0xFF444444, false);
        textY += 15;

        for (var difficulty : tier.getAllowedDifficulties()) {
            context.drawText(font, Text.literal("- " + difficulty.getDisplayName()), textX, textY, 0xFF000000, false);
            textY += 12;
        }

        textY += 15;
        context.drawText(font, Text.literal("Navigation:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.drawText(font, Text.literal("Use Previous/Next buttons"), textX, textY, 0xFF666666, false);
        context.drawText(font, Text.literal("to browse quest pages"), textX, textY + 10, 0xFF666666, false);
    }

    private void renderActiveQuestsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Currently Active Quests:"), textX, textY, 0xFF444444, false);
        textY += 20;

        // Placeholder for actual quest data
        context.drawText(font, Text.literal("No active quests to display"), textX, textY, 0xFF666666, false);
        textY += 15;
        context.drawText(font, Text.literal("(Quest data needs server sync)"), textX, textY, 0xFF888888, false);
        textY += 25;

        context.drawText(font, Text.literal("How to get quests:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.drawText(font, Text.literal("1. Find a Quest Block"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("2. Right-click to browse"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("3. Accept quests"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("4. Complete objectives"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("5. Return to turn in"), textX, textY, 0xFF000000, false);
    }

    private void renderAvailableQuestsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Available for Your Tier:"), textX, textY, 0xFF444444, false);
        textY += 20;

        // Placeholder quest examples
        context.drawText(font, Text.literal("Example Quests:"), textX, textY, 0xFF444444, false);
        textY += 15;

        context.drawText(font, Text.literal("- Gather Wood (Novice)"), textX, textY, 0xFF00AA00, false);
        textY += 12;
        context.drawText(font, Text.literal("- Mine Stone (Novice)"), textX, textY, 0xFF00AA00, false);
        textY += 12;
        context.drawText(font, Text.literal("- Village Builder (Apprentice)"), textX, textY, 0xFF0066FF, false);
        textY += 12;
        context.drawText(font, Text.literal("- Monster Hunter (Apprentice)"), textX, textY, 0xFF0066FF, false);
        textY += 20;

        context.drawText(font, Text.literal("Visit Quest Blocks to see"), textX, textY, 0xFF666666, false);
        context.drawText(font, Text.literal("actual available quests"), textX, textY + 10, 0xFF666666, false);
    }

    private void renderStatisticsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Quest Statistics:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.drawText(font, Text.literal("Total Completed: 0"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Currently Active: 0"), textX, textY, 0xFF000000, false);
        textY += 12;
        context.drawText(font, Text.literal("Success Rate: 100%"), textX, textY, 0xFF00AA00, false);
        textY += 25;

        context.drawText(font, Text.literal("Achievements:"), textX, textY, 0xFF444444, false);
        textY += 15;
        context.drawText(font, Text.literal("- First Quest (Locked)"), textX, textY, 0xFF666666, false);
        textY += 12;
        context.drawText(font, Text.literal("- Quest Master (Locked)"), textX, textY, 0xFF666666, false);
        textY += 12;
        context.drawText(font, Text.literal("- Completionist (Locked)"), textX, textY, 0xFF666666, false);
        textY += 25;

        context.drawText(font, Text.literal("(Statistics will update when"), textX, textY, 0xFF888888, false);
        context.drawText(font, Text.literal("quest system is fully connected)"), textX, textY + 10, 0xFF888888, false);
    }

    private void renderQuestChainsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Quest Chains:"), textX, textY, 0xFF444444, false);
        textY += 20;

        // This would require client-server sync to show real data
        // For now, show placeholder chain information
        context.drawText(font, Text.literal("The Adventurer's Path"), textX, textY, 0xFF00AA00, false);
        textY += 12;
        context.drawText(font, Text.literal("Progress: [▓▓▓░░] 3/5 quests"), textX, textY, 0xFF666666, false);
        textY += 20;

        context.drawText(font, Text.literal("Village Development"), textX, textY, 0xFF666666, false);
        textY += 12;
        context.drawText(font, Text.literal("Progress: [░░░░░] Locked"), textX, textY, 0xFF666666, false);
        textY += 20;

        context.drawText(font, Text.literal("Complete chains to unlock"), textX, textY, 0xFF888888, false);
        context.drawText(font, Text.literal("higher quest book tiers!"), textX, textY + 10, 0xFF888888, false);
    }

    private void renderAchievementsPage(DrawContext context, TextRenderer font, int textX, int textY) {
        context.drawText(font, Text.literal("Achievements:"), textX, textY, 0xFF444444, false);
        textY += 20;

        context.drawText(font, Text.literal("First Steps"), textX, textY, 0xFF00AA00, false);
        context.drawText(font, Text.literal(" ✓ Complete your first quest"), textX + 80, textY, 0xFF00AA00, false);
        textY += 15;

        context.drawText(font, Text.literal("Chain Master"), textX, textY, 0xFF666666, false);
        context.drawText(font, Text.literal(" ✗ Complete a quest chain"), textX + 80, textY, 0xFF666666, false);
        textY += 15;

        context.drawText(font, Text.literal("Master Adventurer"), textX, textY, 0xFF666666, false);
        context.drawText(font, Text.literal(" ✗ Reach Master tier"), textX + 80, textY, 0xFF666666, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}