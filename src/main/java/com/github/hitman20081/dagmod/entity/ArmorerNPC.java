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
import java.util.OptionalInt;

public class ArmorerNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public ArmorerNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== STATIC TRADES (Always Available) =====

        // ===== VANILLA IRON ARMOR =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_HELMET),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_CHESTPLATE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_LEGGINGS),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 5),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_BOOTS),
                8, 5, 0.05F
        ));

        // ===== VANILLA CHAINMAIL ARMOR =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.of(new TradedItem(Items.IRON_NUGGET, 8)),
                new ItemStack(Items.CHAINMAIL_HELMET),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 7),
                java.util.Optional.of(new TradedItem(Items.IRON_NUGGET, 12)),
                new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                java.util.Optional.of(new TradedItem(Items.IRON_NUGGET, 10)),
                new ItemStack(Items.CHAINMAIL_LEGGINGS),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.of(new TradedItem(Items.IRON_NUGGET, 6)),
                new ItemStack(Items.CHAINMAIL_BOOTS),
                6, 8, 0.05F
        ));

        // ===== VANILLA DIAMOND ARMOR =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                java.util.Optional.of(new TradedItem(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_HELMET),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                java.util.Optional.of(new TradedItem(Items.DIAMOND, 4)),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                java.util.Optional.of(new TradedItem(Items.DIAMOND, 3)),
                new ItemStack(Items.DIAMOND_LEGGINGS),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                java.util.Optional.of(new TradedItem(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_BOOTS),
                3, 15, 0.05F
        ));

        // ===== MYTHRIL ARMOR =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_HELMET),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 7)),
                new ItemStack(ModItems.MYTHRIL_CHESTPLATE),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 28),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 6)),
                new ItemStack(ModItems.MYTHRIL_LEGGINGS),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                java.util.Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_BOOTS),
                2, 12, 0.05F
        ));

        // ===== SHIELDS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.SHIELD),
                10, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                java.util.Optional.of(new TradedItem(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.CRYSTAL_SHIELD),
                3, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                java.util.Optional.of(new TradedItem(Items.OAK_LOG, 24)),
                new ItemStack(ModItems.NATURE_SHIELD),
                3, 10, 0.05F
        ));

        // ===== CRAFTING MATERIALS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                java.util.Optional.of(new TradedItem(Items.IRON_INGOT, 2)),
                new ItemStack(ModItems.MYTHRIL_INGOT),
                12, 10, 0.1F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_INGOT, 8),
                16, 5, 0.05F
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.ARMORER);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.ARMORER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.ARMORER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.ARMORER);
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
        return Text.translatable("entity.dagmod.armorer_npc");
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
        // Armorer has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_VILLAGER_YES, this.getSoundCategory(), 1.0F, 1.0F);
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
        return SoundEvents.ENTITY_VILLAGER_YES;
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
