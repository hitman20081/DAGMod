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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

import java.util.List;
import java.util.OptionalInt;

public class MysteryMerchantNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public MysteryMerchantNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== MID-TIER WEAPONS =====
        // Mythril Sword - Entry level rare weapon
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                java.util.Optional.empty(),
                new ItemStack(ModItems.MYTHRIL_SWORD),
                3, 5, 0.05F
        ));
        // Gilded Rapier - Fast attack speed
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                java.util.Optional.of(new TradedItem(Items.GOLD_INGOT, 8)),
                new ItemStack(ModItems.GILDED_RAPIER),
                2, 8, 0.05F
        ));
        // Crystal Katana - Balanced rare weapon
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                java.util.Optional.of(new TradedItem(Items.AMETHYST_SHARD, 16)),
                new ItemStack(ModItems.CRYSTAL_KATANA),
                2, 10, 0.05F
        ));

        // ===== HIGH-TIER WEAPONS =====
        // Dragonscale Sword
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 48),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 2)),
                new ItemStack(ModItems.DRAGONSCALE_SWORD),
                1, 15, 0.05F
        ));
        // Inferno Sword
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 48),
                java.util.Optional.of(new TradedItem(Items.BLAZE_ROD, 8)),
                new ItemStack(ModItems.INFERNO_SWORD),
                1, 15, 0.05F
        ));
        // Shadowfang Dagger - Rogue favorite
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 40),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOWFANG_DAGGER),
                1, 12, 0.05F
        ));
        // Shadowfang Sword
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 52),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                new ItemStack(ModItems.SHADOWFANG_SWORD),
                1, 15, 0.05F
        ));
        // Bloodthirster Blade - Lifesteal weapon
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                java.util.Optional.of(new TradedItem(Items.GHAST_TEAR, 4)),
                new ItemStack(ModItems.BLOODTHIRSTER_BLADE),
                1, 15, 0.05F
        ));
        // Frostbite Axe
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 56),
                java.util.Optional.of(new TradedItem(Items.BLUE_ICE, 16)),
                new ItemStack(ModItems.FROSTBITE_AXE),
                1, 15, 0.05F
        ));
        // Poison Fang Spear
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 44),
                java.util.Optional.of(new TradedItem(Items.SPIDER_EYE, 8)),
                new ItemStack(ModItems.POISON_FANG_SPEAR),
                1, 12, 0.05F
        ));

        // ===== EPIC WEAPONS =====
        // Ethereal Blade
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 24),
                java.util.Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.ETHEREAL_BLADE),
                1, 20, 0.05F
        ));
        // Thunder Pike
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 20),
                java.util.Optional.of(new TradedItem(Items.TRIDENT, 1)),
                new ItemStack(ModItems.THUNDER_PIKE),
                1, 20, 0.05F
        ));
        // Crystalhammer
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 28),
                java.util.Optional.of(new TradedItem(ModItems.SILMARIL, 1)),
                new ItemStack(ModItems.CRYSTALHAMMER),
                1, 25, 0.05F
        ));

        // ===== SHIELDS =====
        // Crystal Shield - Entry level
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                java.util.Optional.of(new TradedItem(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.CRYSTAL_SHIELD),
                2, 8, 0.05F
        ));
        // Nature Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                java.util.Optional.of(new TradedItem(Items.OAK_LOG, 32)),
                new ItemStack(ModItems.NATURE_SHIELD),
                2, 8, 0.05F
        ));
        // Frost Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 36),
                java.util.Optional.of(new TradedItem(Items.BLUE_ICE, 8)),
                new ItemStack(ModItems.FROST_SHIELD),
                1, 12, 0.05F
        ));
        // Inferno Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 40),
                java.util.Optional.of(new TradedItem(Items.BLAZE_ROD, 4)),
                new ItemStack(ModItems.INFERNO_SHIELD),
                1, 12, 0.05F
        ));
        // Shadow Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 40),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOW_SHIELD),
                1, 12, 0.05F
        ));
        // Solar Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 44),
                java.util.Optional.of(new TradedItem(Items.SUNFLOWER, 16)),
                new ItemStack(ModItems.SOLAR_SHIELD),
                1, 15, 0.05F
        ));
        // Dragonbone Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_BONE, 4)),
                new ItemStack(ModItems.DRAGONBONE_SHIELD),
                1, 20, 0.05F
        ));
        // Stormguard Shield
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 20),
                java.util.Optional.of(new TradedItem(Items.HEART_OF_THE_SEA, 1)),
                new ItemStack(ModItems.STORMGUARD_SHIELD),
                1, 20, 0.05F
        ));
        // Celestial Shield - Top tier
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 32),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_HEART, 1)),
                new ItemStack(ModItems.CELESTIAL_SHIELD),
                1, 25, 0.05F
        ));

        // ===== MYTHRIL ARMOR SET =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 5)),
                new ItemStack(ModItems.MYTHRIL_HELMET),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 40),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 8)),
                new ItemStack(ModItems.MYTHRIL_CHESTPLATE),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 36),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 7)),
                new ItemStack(ModItems.MYTHRIL_LEGGINGS),
                2, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_BOOTS),
                2, 10, 0.05F
        ));

        // ===== DRAGONSCALE ARMOR SET =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 5)),
                new ItemStack(ModItems.DRAGONSCALE_HELMET),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 12),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 8)),
                new ItemStack(ModItems.DRAGONSCALE_CHESTPLATE),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 10),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 7)),
                new ItemStack(ModItems.DRAGONSCALE_LEGGINGS),
                1, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 6),
                java.util.Optional.of(new TradedItem(ModItems.DRAGON_SCALE, 4)),
                new ItemStack(ModItems.DRAGONSCALE_BOOTS),
                1, 15, 0.05F
        ));

        // ===== SHADOW ARMOR SET =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.SHADOW_HELMET),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 48),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOW_CHESTPLATE),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 44),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 5)),
                new ItemStack(ModItems.SHADOW_LEGGINGS),
                1, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 28),
                java.util.Optional.of(new TradedItem(Items.ECHO_SHARD, 3)),
                new ItemStack(ModItems.SHADOW_BOOTS),
                1, 12, 0.05F
        ));

        // ===== SPECIAL MATERIALS =====
        // Dragon Scale
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 4),
                java.util.Optional.of(new TradedItem(Items.NETHERITE_SCRAP, 1)),
                new ItemStack(ModItems.DRAGON_SCALE),
                5, 20, 0.1F
        ));
        // Mythril Ingot
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                java.util.Optional.of(new TradedItem(Items.IRON_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_INGOT),
                8, 15, 0.1F
        ));
        // Silmaril
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                java.util.Optional.of(new TradedItem(Items.AMETHYST_SHARD, 32)),
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
        this.offers = new TradeOfferList();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.MYSTERY_MERCHANT);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.MYSTERY_MERCHANT, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
    }

    @Override
    public boolean canInteract(PlayerEntity player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0; // Example condition, can be adjusted
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.MYSTERY_MERCHANT);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.MYSTERY_MERCHANT);
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
        return Text.translatable("entity.dagmod.mystery_merchant_npc");
    }

    // Custom helper method, not part of Merchant interface
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
        // Mystery Merchant has static offers, ignore server updates
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use(); // Decrease uses
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_VILLAGER_YES, this.getSoundCategory(), 1.0F, 1.0F); // Corrected playSound
    }

    @Override
    public void onSellingItem(ItemStack stack) {
        // Not implemented for Mystery Merchant
    }

    @Override
    public int getExperience() {
        return 0; // Mystery Merchant does not gain experience
    }

    @Override
    public void setExperienceFromServer(int experience) {
        // Mystery Merchant does not gain experience
    }

    public boolean isLeveledUp() {
        return false; // Mystery Merchant does not level up
    }

    @Override
    public boolean isLeveledMerchant() {
        return false; // Mystery Merchant is not a leveled merchant
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    public void playWorkSound() {
        // Mystery Merchant does not have a work sound
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
