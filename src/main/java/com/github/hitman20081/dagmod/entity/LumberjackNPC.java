package com.github.hitman20081.dagmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.trade.MerchantDialogue;
import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.github.hitman20081.dagmod.trade.RotatingTradeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradedItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class LumberjackNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public LumberjackNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== AXES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.IRON_AXE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                Optional.of(new TradedItem(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_AXE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 14),
                Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_AXE),
                2, 15, 0.05F
        ));
        // Frostbite Axe - Special rare axe
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                Optional.of(new TradedItem(Items.BLUE_ICE, 8)),
                new ItemStack(ModItems.FROSTBITE_AXE),
                1, 20, 0.05F
        ));

        // ===== LOGS - Common =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BIRCH_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.JUNGLE_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.ACACIA_LOG, 16),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_LOG, 16),
                16, 2, 0.05F
        ));

        // ===== LOGS - Special =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.MANGROVE_LOG, 16),
                12, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.CHERRY_LOG, 16),
                12, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.CRIMSON_STEM, 16),
                8, 6, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.WARPED_STEM, 16),
                8, 6, 0.05F
        ));

        // ===== PLANKS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_PLANKS, 32),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_PLANKS, 32),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_PLANKS, 32),
                16, 2, 0.05F
        ));

        // ===== STICKS & BASIC MATERIALS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.STICK, 64),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CHARCOAL, 16),
                16, 2, 0.05F
        ));

        // ===== WOOD PRODUCTS =====
        // Crafting Table
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CRAFTING_TABLE),
                12, 2, 0.05F
        ));
        // Chest
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.CHEST, 2),
                12, 3, 0.05F
        ));
        // Barrel
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BARREL, 2),
                12, 3, 0.05F
        ));
        // Boat
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.OAK_BOAT),
                8, 4, 0.05F
        ));
        // Bed
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.of(new TradedItem(Items.WHITE_WOOL, 3)),
                new ItemStack(Items.WHITE_BED),
                6, 5, 0.05F
        ));
        // Bookshelf
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.of(new TradedItem(Items.BOOK, 3)),
                new ItemStack(Items.BOOKSHELF),
                8, 6, 0.05F
        ));
        // Ladder
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LADDER, 16),
                16, 2, 0.05F
        ));
        // Fence
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_FENCE, 8),
                12, 3, 0.05F
        ));
        // Door
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_DOOR, 2),
                12, 3, 0.05F
        ));
        // Trapdoor
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_TRAPDOOR, 4),
                12, 3, 0.05F
        ));

        // ===== SAPLINGS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.OAK_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.SPRUCE_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BIRCH_SAPLING, 4),
                12, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.DARK_OAK_SAPLING, 4),
                10, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.CHERRY_SAPLING, 2),
                10, 4, 0.05F
        ));

        // ===== SPECIAL - Nature Shield =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.OAK_LOG, 32)),
                new ItemStack(ModItems.NATURE_SHIELD),
                2, 15, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells logs
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.OAK_LOG, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.SPRUCE_LOG, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells sticks
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.STICK, 64),
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
        this.offers = new TradeOfferList();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.LUMBERJACK);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.LUMBERJACK, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
    }

    @Override
    public boolean canInteract(PlayerEntity player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getEntityWorld().isClient()) {
            if (this.isAlive() && this.canInteract(player) && !this.hasCustomer() && !player.isSneaking()) {
                // Rebuild offers to include current rotating trades
                rebuildOffers();

                this.setCustomer(player);

                // Send merchant dialogue
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.LUMBERJACK);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.LUMBERJACK);
                }

                this.openOfferScreen(player, this.getDisplayName(), 1);
                return ActionResult.CONSUME;
            } else if (this.hasCustomer() && this.getCustomer() == player) {
                return ActionResult.PASS;
            }
        }
        return ActionResult.SUCCESS;
    }

    public void openOfferScreen(PlayerEntity player, Text name, int level) {
        OptionalInt optionalSyncId = player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, playerEntity) -> new MerchantScreenHandler(syncId, inventory, this),
                this.getDisplayName()));

        if (optionalSyncId.isPresent() && player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            int syncId = optionalSyncId.getAsInt();
            serverPlayer.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket(
                    syncId,
                    this.getOffers(),
                    level,
                    this.getExperience(),
                    this.isLeveledMerchant(),
                    this.canRefreshTrades()
            ));
        }
    }

    public boolean canRefreshTrades() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("entity.dagmod.lumberjack_npc");
    }

    public boolean hasCustomer() {
        return this.customer != null;
    }

    @Override
    public boolean isClient() {
        return this.getEntityWorld().isClient();
    }

    @Override
    public PlayerEntity getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(PlayerEntity player) {
        this.customer = player;
    }

    @Override
    public TradeOfferList getOffers() {
        return this.offers;
    }

    @Override
    public void setOffersFromServer(TradeOfferList offers) {
        // Lumberjack has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_WOOD_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onSellingItem(ItemStack stack) {
        // Not implemented
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    public void setExperienceFromServer(int experience) {
        // Not implemented
    }

    @Override
    public boolean isLeveledMerchant() {
        return false;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    public void pushAwayFrom(net.minecraft.entity.Entity entity) {
        // Don't get pushed by other entities
    }
}
