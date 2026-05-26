package com.github.hitman20081.dagmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.trade.MerchantDialogue;
import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.github.hitman20081.dagmod.trade.RotatingTradeRegistry;
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

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.world.item.trading.ItemCost;

public class LumberjackNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public LumberjackNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== AXES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.IRON_AXE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                Optional.of(new ItemCost(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_AXE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 14),
                Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_AXE),
                2, 15, 0.05F
        ));
        // Frostbite Axe - Special rare axe
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                Optional.of(new ItemCost(Items.BLUE_ICE, 8)),
                new ItemStack(ModItems.FROSTBITE_AXE),
                1, 20, 0.05F
        ));

        // ===== LOGS - Common =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BIRCH_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.JUNGLE_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.ACACIA_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_LOG, 16),
                16, 2, 0.05F
        ));

        // ===== LOGS - Special =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.MANGROVE_LOG, 16),
                12, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.CHERRY_LOG, 16),
                12, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.CRIMSON_STEM, 16),
                8, 6, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.WARPED_STEM, 16),
                8, 6, 0.05F
        ));

        // ===== PLANKS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_PLANKS, 32),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_PLANKS, 32),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_PLANKS, 32),
                16, 2, 0.05F
        ));

        // ===== STICKS & BASIC MATERIALS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.STICK, 64),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CHARCOAL, 16),
                16, 2, 0.05F
        ));

        // ===== WOOD PRODUCTS =====
        // Crafting Table
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CRAFTING_TABLE),
                12, 2, 0.05F
        ));
        // Chest
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CHEST, 2),
                12, 3, 0.05F
        ));
        // Barrel
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BARREL, 2),
                12, 3, 0.05F
        ));
        // Boat
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.OAK_BOAT),
                8, 4, 0.05F
        ));
        // Bed
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.of(new ItemCost(Items.WHITE_WOOL, 3)),
                new ItemStack(Items.WHITE_BED),
                6, 5, 0.05F
        ));
        // Bookshelf
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.of(new ItemCost(Items.BOOK, 3)),
                new ItemStack(Items.BOOKSHELF),
                8, 6, 0.05F
        ));
        // Ladder
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LADDER, 16),
                16, 2, 0.05F
        ));
        // Fence
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_FENCE, 8),
                12, 3, 0.05F
        ));
        // Door
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_DOOR, 2),
                12, 3, 0.05F
        ));
        // Trapdoor
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_TRAPDOOR, 4),
                12, 3, 0.05F
        ));

        // ===== SAPLINGS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BIRCH_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_SAPLING, 4),
                10, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.CHERRY_SAPLING, 2),
                10, 4, 0.05F
        ));

        // ===== SPECIAL - Nature Shield =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.OAK_LOG, 32)),
                new ItemStack(ModItems.NATURE_SHIELD),
                2, 15, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells logs
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.OAK_LOG, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.SPRUCE_LOG, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells sticks
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.STICK, 64),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));

        // Initialize offers with static trades
        rebuildOffers();
    }

    /**
     * Rebuilds the offer list with static trades + current rotating trades.
     */
    private void rebuildOffers() {
        this.offers = new MerchantOffers();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.LUMBERJACK);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.LUMBERJACK, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
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
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            if (this.isAlive() && this.canInteract(player) && !this.hasCustomer() && !player.isShiftKeyDown()) {
                // Rebuild offers to include current rotating trades
                rebuildOffers();

                this.setTradingPlayer(player);

                // Send merchant dialogue
                if (player instanceof ServerPlayer serverPlayer) {
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.LUMBERJACK);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.LUMBERJACK);
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

        if (optionalSyncId.isPresent() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
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
        return Component.translatable("entity.dagmod.lumberjack_npc");
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
        // Lumberjack has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.WOOD_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // Not implemented
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int experience) {
        // Not implemented
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.WOOD_BREAK;
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
        // Don't get pushed by other entities
    }
}
