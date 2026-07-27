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

/**
 * Trophy Dealer NPC - Sells boss drops, rare mob drops, and collectibles.
 * High-end merchant for players seeking rare items.
 */
public class LuxuryMerchantNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public LuxuryMerchantNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== DRAGON DROPS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 32),
                Optional.of(new ItemCost(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.DRAGON_HEART),
                1, 50, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 8),
                Optional.of(new ItemCost(Items.NETHERITE_SCRAP, 2)),
                new ItemStack(ModItems.DRAGON_SCALE),
                3, 25, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 6),
                Optional.of(new ItemCost(Items.BONE_BLOCK, 4)),
                new ItemStack(ModItems.DRAGON_BONE),
                4, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 64),
                Optional.of(new ItemCost(ModItems.DRAGON_HEART, 2)),
                new ItemStack(Items.DRAGON_EGG),
                1, 100, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 4)),
                new ItemStack(Items.DRAGON_BREATH, 4),
                2, 30, 0.05F
        ));

        // ===== WITHER & NETHER DROPS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 24),
                Optional.of(new ItemCost(Items.SOUL_SAND, 16)),
                new ItemStack(Items.WITHER_SKELETON_SKULL),
                2, 35, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 48),
                Optional.of(new ItemCost(Items.WITHER_SKELETON_SKULL, 3)),
                new ItemStack(Items.NETHER_STAR),
                1, 60, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                Optional.of(new ItemCost(Items.GOLD_BLOCK, 2)),
                new ItemStack(Items.NETHERITE_INGOT),
                2, 30, 0.05F
        ));

        // ===== OCEAN TREASURES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 20),
                Optional.of(new ItemCost(Items.NAUTILUS_SHELL, 8)),
                new ItemStack(Items.HEART_OF_THE_SEA),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 24),
                Optional.of(new ItemCost(Items.PRISMARINE_SHARD, 16)),
                new ItemStack(Items.TRIDENT),
                1, 45, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.PRISMARINE_CRYSTALS, 8)),
                new ItemStack(Items.CONDUIT),
                1, 30, 0.05F
        ));

        // ===== END TREASURES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 32),
                Optional.of(new ItemCost(Items.PHANTOM_MEMBRANE, 16)),
                new ItemStack(Items.ELYTRA),
                1, 50, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 8),
                Optional.of(new ItemCost(Items.CHORUS_FRUIT, 8)),
                new ItemStack(Items.SHULKER_SHELL, 2),
                3, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.of(new ItemCost(Items.ENDER_PEARL, 4)),
                new ItemStack(Items.ENDER_EYE, 4),
                6, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.END_ROD, 4),
                8, 8, 0.05F
        ));

        // ===== RAID DROPS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                Optional.of(new ItemCost(Items.EMERALD_BLOCK, 4)),
                new ItemStack(Items.TOTEM_OF_UNDYING),
                1, 35, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                Optional.of(new ItemCost(Items.CROSSBOW, 1)),
                new ItemStack(Items.GOAT_HORN),
                2, 20, 0.05F
        ));

        // ===== ENCHANTED ITEMS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 12),
                Optional.of(new ItemCost(Items.GOLDEN_APPLE, 8)),
                new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                Optional.of(new ItemCost(Items.GOLDEN_CARROT, 16)),
                new ItemStack(Items.GOLDEN_APPLE, 4),
                4, 15, 0.05F
        ));

        // ===== MUSIC DISCS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 12),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_CAT),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 12),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_BLOCKS),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_PIGSTEP),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.empty(),
                new ItemStack(Items.MUSIC_DISC_OTHERSIDE),
                1, 20, 0.05F
        ));

        // ===== MOD BOSS TROPHIES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 24),
                Optional.of(new ItemCost(Items.AMETHYST_SHARD, 32)),
                new ItemStack(ModItems.SILMARIL),
                1, 40, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                Optional.of(new ItemCost(Items.NETHERITE_SCRAP, 4)),
                new ItemStack(ModItems.KINGS_SCALE),
                1, 35, 0.05F
        ));

        // ===== DECORATIVE HEADS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.CREEPER_HEAD),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.ZOMBIE_HEAD),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 8),
                Optional.empty(),
                new ItemStack(Items.PIGLIN_HEAD),
                2, 18, 0.05F
        ));

        // ===== SPECIAL ITEMS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 4),
                Optional.of(new ItemCost(Items.ENDER_PEARL, 8)),
                new ItemStack(Items.ENDER_CHEST),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.OBSIDIAN, 8)),
                new ItemStack(Items.CRYING_OBSIDIAN, 8),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.SPONGE, 4),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                Optional.of(new ItemCost(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse("minecraft:copper_block")), 8)),
                new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse("minecraft:lightning_rod")), 4),
                4, 15, 0.05F
        ));

        // ===== BUY RARE ITEMS FROM PLAYERS =====
        // Player sells dragon breath
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DRAGON_BREATH, 4),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 4),
                4, 15, 0.05F
        ));
        // Player sells nether star
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.NETHER_STAR, 1),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 16),
                2, 25, 0.05F
        ));
        // Player sells heart of the sea
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.HEART_OF_THE_SEA, 1),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 8),
                4, 18, 0.05F
        ));
        // Player sells mod dragon drops
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.DRAGON_HEART, 1),
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
        this.offers = new MerchantOffers();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.TROPHY_DEALER);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.TROPHY_DEALER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.TROPHY_DEALER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.TROPHY_DEALER);
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
        return Component.translatable("entity.dagmod.trophy_dealer_npc");
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
        // Trophy Dealer has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, this.getSoundSource(), 0.5F, 1.0F);
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
        return SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
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
