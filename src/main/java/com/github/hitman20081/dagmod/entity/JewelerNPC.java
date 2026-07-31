package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.progression.ProgressionManager;
import com.github.hitman20081.dagmod.quest.QuestManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.SimpleMenuProvider;

import java.util.OptionalInt;
import net.minecraft.world.item.trading.ItemCost;

public class JewelerNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;

    public JewelerNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.offers = new MerchantOffers();

        // ===== BUY PROCESSED GEMS (Player sells gems → gets emeralds) =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_RUBY, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 5),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_SAPPHIRE, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 5),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_CITRINE, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 3),
                16, 6, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_TANZANITE, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 6),
                16, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_TOPAZ, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 3),
                16, 6, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_ZIRCON, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 4),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.GEM_CUT_PINK_GARNET, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 4),
                16, 8, 0.05F
        ));

        // ===== BUY VANILLA GEMS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.AMETHYST_SHARD, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 2),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.LAPIS_LAZULI, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.EMERALD, 2),
                24, 5, 0.05F
        ));

        // ===== SELL GEM CRAFTING SUPPLIES =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                java.util.Optional.empty(),
                new ItemStack(ModItems.GEM_CUTTER_TOOL),
                6, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.CITRINE_POWDER, 4),
                12, 5, 0.05F
        ));

        // ===== SELL PREMIUM GEMS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                java.util.Optional.of(new ItemCost(Items.DIAMOND, 4)),
                new ItemStack(ModItems.SILMARIL),
                1, 30, 0.05F
        ));
    }
    public boolean canInteract(Player player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            if (this.isAlive() && this.canInteract(player) && !this.hasCustomer() && !player.isShiftKeyDown()) {
                this.setTradingPlayer(player);

                if (player instanceof ServerPlayer serverPlayer) {
                    if (!PlayerDataManager.hasStartedGemChain(serverPlayer)) {
                        var progressionData = ProgressionManager.getPlayerData(serverPlayer);
                        int playerLevel = progressionData != null ? progressionData.getCurrentLevel() : 0;
                        if (playerLevel < 5) {
                            serverPlayer.sendSystemMessage(
                                    Component.literal("<Jeweler> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                            .append(Component.literal("You're still green, traveler. Explore a bit more and come back when you've got some experience under your belt. I'll have work for you then.").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
                        } else {
                        PlayerDataManager.markGemChainStarted(serverPlayer);
                        QuestManager.getInstance().startQuest(serverPlayer, "gem_rough_trade");
                        serverPlayer.sendSystemMessage(
                                Component.literal("<Jeweler> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                        .append(Component.literal("Ah, a seasoned adventurer! Before we get to trading, let me teach you the craft. Raw gems are worthless on their own — bring me five Raw Citrine and I'll give you the tools to turn them into something worth selling.").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
                        }
                    } else {
                        serverPlayer.sendSystemMessage(
                                Component.literal("<Jeweler> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                        .append(Component.literal("Gems, jewels, and fine craftsmanship. Care to browse my collection?").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
                    }
                }

                this.openOfferScreen(player, this.getDisplayName(), 1);
                return InteractionResult.CONSUME;
            } else if (this.hasCustomer() && this.getTradingPlayer() == player) {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void openOfferScreen(Player player, Component name, int level) {
        OptionalInt optionalSyncId = player.openMenu(new SimpleMenuProvider(
                (syncId, inventory, playerEntity) -> new MerchantMenu(syncId, inventory, this),
                this.getDisplayName()));

        if (optionalSyncId.isPresent() && player instanceof ServerPlayer serverPlayer) {
            int syncId = optionalSyncId.getAsInt();
            serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket(
                    syncId,
                    this.getOffers(),
                    level,
                    this.getVillagerXp(),
                    this.showProgressBar(),
                    this.canRefreshTrades()
            ));
        }
    }

    public boolean canRefreshTrades() {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.dagmod.jeweler_npc");
    }

    public boolean hasCustomer() {
        return this.customer != null;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    @Override
    public Player getTradingPlayer() {
        return this.customer;
    }

    @Override
    public void setTradingPlayer(Player player) {
        this.customer = player;
    }

    @Override
    public MerchantOffers getOffers() {
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int experience) {
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    public void pushAwayFrom(net.minecraft.world.entity.Entity entity) {
    }
}
