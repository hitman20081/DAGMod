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

/**
 * Trophy Dealer NPC - Sells boss drops, rare mob drops, and collectibles.
 * High-end merchant for players seeking rare items.
 */
public class LuxuryMerchantNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public LuxuryMerchantNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== DRAGON DROPS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 32),
                Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.DRAGON_HEART),
                1, 50, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.of(new TradedItem(Items.NETHERITE_SCRAP, 2)),
                new ItemStack(ModItems.DRAGON_SCALE),
                3, 25, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 6),
                Optional.of(new TradedItem(Items.BONE_BLOCK, 4)),
                new ItemStack(ModItems.DRAGON_BONE),
                4, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 64),
                Optional.of(new TradedItem(ModItems.DRAGON_HEART, 2)),
                new ItemStack(Items.DRAGON_EGG),
                1, 100, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 4)),
                new ItemStack(Items.DRAGON_BREATH, 4),
                2, 30, 0.05F
        ));

        // ===== WITHER & NETHER DROPS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 24),
                Optional.of(new TradedItem(Items.SOUL_SAND, 16)),
                new ItemStack(Items.WITHER_SKELETON_SKULL),
                2, 35, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 48),
                Optional.of(new TradedItem(Items.WITHER_SKELETON_SKULL, 3)),
                new ItemStack(Items.NETHER_STAR),
                1, 60, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(Items.GOLD_BLOCK, 2)),
                new ItemStack(Items.NETHERITE_INGOT),
                2, 30, 0.05F
        ));

        // ===== OCEAN TREASURES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 20),
                Optional.of(new TradedItem(Items.NAUTILUS_SHELL, 8)),
                new ItemStack(Items.HEART_OF_THE_SEA),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 24),
                Optional.of(new TradedItem(Items.PRISMARINE_SHARD, 16)),
                new ItemStack(Items.TRIDENT),
                1, 45, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.PRISMARINE_CRYSTALS, 8)),
                new ItemStack(Items.CONDUIT),
                1, 30, 0.05F
        ));

        // ===== END TREASURES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 32),
                Optional.of(new TradedItem(Items.PHANTOM_MEMBRANE, 16)),
                new ItemStack(Items.ELYTRA),
                1, 50, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.of(new TradedItem(Items.CHORUS_FRUIT, 8)),
                new ItemStack(Items.SHULKER_SHELL, 2),
                3, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.ENDER_PEARL, 4)),
                new ItemStack(Items.ENDER_EYE, 4),
                6, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.END_ROD, 4),
                8, 8, 0.05F
        ));

        // ===== RAID DROPS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(Items.EMERALD_BLOCK, 4)),
                new ItemStack(Items.TOTEM_OF_UNDYING),
                1, 35, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.CROSSBOW, 1)),
                new ItemStack(Items.GOAT_HORN),
                2, 20, 0.05F
        ));

        // ===== ENCHANTED ITEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 12),
                Optional.of(new TradedItem(Items.GOLDEN_APPLE, 8)),
                new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                Optional.of(new TradedItem(Items.GOLDEN_CARROT, 16)),
                new ItemStack(Items.GOLDEN_APPLE, 4),
                4, 15, 0.05F
        ));

        // ===== MUSIC DISCS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_CAT),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_BLOCKS),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_PIGSTEP),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_OTHERSIDE),
                1, 20, 0.05F
        ));

        // ===== MOD BOSS TROPHIES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 24),
                Optional.of(new TradedItem(Items.AMETHYST_SHARD, 32)),
                new ItemStack(ModItems.SILMARIL),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(Items.NETHERITE_SCRAP, 4)),
                new ItemStack(ModItems.KINGS_SCALE),
                1, 35, 0.05F
        ));

        // ===== DECORATIVE HEADS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.CREEPER_HEAD),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.ZOMBIE_HEAD),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.empty(),
                new ItemStack(Items.PIGLIN_HEAD),
                2, 18, 0.05F
        ));

        // ===== SPECIAL ITEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 4),
                Optional.of(new TradedItem(Items.ENDER_PEARL, 8)),
                new ItemStack(Items.ENDER_CHEST),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.OBSIDIAN, 8)),
                new ItemStack(Items.CRYING_OBSIDIAN, 8),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.SPONGE, 4),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.COPPER_BLOCK, 8)),
                new ItemStack(Items.LIGHTNING_ROD, 4),
                4, 15, 0.05F
        ));

        // ===== BUY RARE ITEMS FROM PLAYERS =====
        // Player sells dragon breath
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DRAGON_BREATH, 4),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 4),
                4, 15, 0.05F
        ));
        // Player sells nether star
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.NETHER_STAR, 1),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 16),
                2, 25, 0.05F
        ));
        // Player sells heart of the sea
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.HEART_OF_THE_SEA, 1),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 8),
                4, 18, 0.05F
        ));
        // Player sells mod dragon drops
        this.staticOffers.add(new TradeOffer(
                new TradedItem(ModItems.DRAGON_HEART, 1),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 24),
                2, 30, 0.05F
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.TROPHY_DEALER);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.TROPHY_DEALER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.TROPHY_DEALER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.TROPHY_DEALER);
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
        return Text.translatable("entity.dagmod.trophy_dealer_npc");
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
        // Trophy Dealer has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, this.getSoundCategory(), 0.5F, 1.0F);
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
        return SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
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
