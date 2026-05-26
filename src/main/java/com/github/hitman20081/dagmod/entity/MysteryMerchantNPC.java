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
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.SimpleMenuProvider;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.world.item.trading.ItemCost;

public class MysteryMerchantNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public MysteryMerchantNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== MID-TIER WEAPONS =====
        // Mythril Sword - Entry level rare weapon
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                java.util.Optional.empty(),
                new ItemStack(ModItems.MYTHRIL_SWORD),
                3, 5, 0.05F
        ));
        // Gilded Rapier - Fast attack speed
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                java.util.Optional.of(new ItemCost(Items.GOLD_INGOT, 8)),
                new ItemStack(ModItems.GILDED_RAPIER),
                2, 8, 0.05F
        ));
        // Crystal Katana - Balanced rare weapon
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                java.util.Optional.of(new ItemCost(Items.AMETHYST_SHARD, 16)),
                new ItemStack(ModItems.CRYSTAL_KATANA),
                2, 10, 0.05F
        ));

        // ===== HIGH-TIER WEAPONS =====
        // Dragonscale Sword
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 48),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 2)),
                new ItemStack(ModItems.DRAGONSCALE_SWORD),
                1, 15, 0.05F
        ));
        // Inferno Sword
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 48),
                java.util.Optional.of(new ItemCost(Items.BLAZE_ROD, 8)),
                new ItemStack(ModItems.INFERNO_SWORD),
                1, 15, 0.05F
        ));
        // Shadowfang Dagger - Rogue favorite
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 40),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOWFANG_DAGGER),
                1, 12, 0.05F
        ));
        // Shadowfang Sword
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 52),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 8)),
                new ItemStack(ModItems.SHADOWFANG_SWORD),
                1, 15, 0.05F
        ));
        // Bloodthirster Blade - Lifesteal weapon
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                java.util.Optional.of(new ItemCost(Items.GHAST_TEAR, 4)),
                new ItemStack(ModItems.BLOODTHIRSTER_BLADE),
                1, 15, 0.05F
        ));
        // Frostbite Axe
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 56),
                java.util.Optional.of(new ItemCost(Items.BLUE_ICE, 16)),
                new ItemStack(ModItems.FROSTBITE_AXE),
                1, 15, 0.05F
        ));
        // Poison Fang Spear
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 44),
                java.util.Optional.of(new ItemCost(Items.SPIDER_EYE, 8)),
                new ItemStack(ModItems.POISON_FANG_SPEAR),
                1, 12, 0.05F
        ));

        // ===== EPIC WEAPONS =====
        // Ethereal Blade
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 24),
                java.util.Optional.of(new ItemCost(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.ETHEREAL_BLADE),
                1, 20, 0.05F
        ));
        // Thunder Pike
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 20),
                java.util.Optional.of(new ItemCost(Items.TRIDENT, 1)),
                new ItemStack(ModItems.THUNDER_PIKE),
                1, 20, 0.05F
        ));
        // Crystalhammer
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 28),
                java.util.Optional.of(new ItemCost(ModItems.SILMARIL, 1)),
                new ItemStack(ModItems.CRYSTALHAMMER),
                1, 25, 0.05F
        ));

        // ===== SHIELDS =====
        // Crystal Shield - Entry level
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                java.util.Optional.of(new ItemCost(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.CRYSTAL_SHIELD),
                2, 8, 0.05F
        ));
        // Nature Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                java.util.Optional.of(new ItemCost(Items.OAK_LOG, 32)),
                new ItemStack(ModItems.NATURE_SHIELD),
                2, 8, 0.05F
        ));
        // Frost Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 36),
                java.util.Optional.of(new ItemCost(Items.BLUE_ICE, 8)),
                new ItemStack(ModItems.FROST_SHIELD),
                1, 12, 0.05F
        ));
        // Inferno Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 40),
                java.util.Optional.of(new ItemCost(Items.BLAZE_ROD, 4)),
                new ItemStack(ModItems.INFERNO_SHIELD),
                1, 12, 0.05F
        ));
        // Shadow Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 40),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOW_SHIELD),
                1, 12, 0.05F
        ));
        // Solar Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 44),
                java.util.Optional.of(new ItemCost(Items.SUNFLOWER, 16)),
                new ItemStack(ModItems.SOLAR_SHIELD),
                1, 15, 0.05F
        ));
        // Dragonbone Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_BONE, 4)),
                new ItemStack(ModItems.DRAGONBONE_SHIELD),
                1, 20, 0.05F
        ));
        // Stormguard Shield
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 20),
                java.util.Optional.of(new ItemCost(Items.HEART_OF_THE_SEA, 1)),
                new ItemStack(ModItems.STORMGUARD_SHIELD),
                1, 20, 0.05F
        ));
        // Celestial Shield - Top tier
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 32),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_HEART, 1)),
                new ItemStack(ModItems.CELESTIAL_SHIELD),
                1, 25, 0.05F
        ));

        // ===== MYTHRIL ARMOR SET =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 5)),
                new ItemStack(ModItems.MYTHRIL_HELMET),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 40),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 8)),
                new ItemStack(ModItems.MYTHRIL_CHESTPLATE),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 36),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 7)),
                new ItemStack(ModItems.MYTHRIL_LEGGINGS),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_BOOTS),
                2, 10, 0.05F
        ));

        // ===== DRAGONSCALE ARMOR SET =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 8),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 5)),
                new ItemStack(ModItems.DRAGONSCALE_HELMET),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 12),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 8)),
                new ItemStack(ModItems.DRAGONSCALE_CHESTPLATE),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 10),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 7)),
                new ItemStack(ModItems.DRAGONSCALE_LEGGINGS),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 6),
                java.util.Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 4)),
                new ItemStack(ModItems.DRAGONSCALE_BOOTS),
                1, 15, 0.05F
        ));

        // ===== SHADOW ARMOR SET =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOW_HELMET),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 48),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOW_CHESTPLATE),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 44),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 5)),
                new ItemStack(ModItems.SHADOW_LEGGINGS),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 28),
                java.util.Optional.of(new ItemCost(Items.ECHO_SHARD, 3)),
                new ItemStack(ModItems.SHADOW_BOOTS),
                1, 12, 0.05F
        ));

        // ===== SPECIAL MATERIALS =====
        // Dragon Scale
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 4),
                java.util.Optional.of(new ItemCost(Items.NETHERITE_SCRAP, 1)),
                new ItemStack(ModItems.DRAGON_SCALE),
                5, 20, 0.1F
        ));
        // Mythril Ingot
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                java.util.Optional.of(new ItemCost(Items.IRON_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_INGOT),
                8, 15, 0.1F
        ));
        // Silmaril
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                java.util.Optional.of(new ItemCost(Items.AMETHYST_SHARD, 32)),
                new ItemStack(ModItems.SILMARIL),
                1, 25, 0.05F
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.MYSTERY_MERCHANT);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.MYSTERY_MERCHANT, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
    }
    public boolean canInteract(Player player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0; // Example condition, can be adjusted
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.MYSTERY_MERCHANT);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.MYSTERY_MERCHANT);
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
        return Component.translatable("entity.dagmod.mystery_merchant_npc");
    }

    // Custom helper method, not part of Merchant interface
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
        // Mystery Merchant has static offers, ignore server updates
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses(); // Decrease uses
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0F, 1.0F); // Corrected playSound
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // Not implemented for Mystery Merchant
    }

    @Override
    public int getVillagerXp() {
        return 0; // Mystery Merchant does not gain experience
    }

    @Override
    public void overrideXp(int experience) {
        // Mystery Merchant does not gain experience
    }

    public boolean isLeveledUp() {
        return false; // Mystery Merchant does not level up
    }

    @Override
    public boolean showProgressBar() {
        return false; // Mystery Merchant is not a leveled merchant
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    public void playWorkSound() {
        // Mystery Merchant does not have a work sound
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
