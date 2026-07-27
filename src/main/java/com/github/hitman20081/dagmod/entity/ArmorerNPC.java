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
import java.util.OptionalInt;
import net.minecraft.world.item.trading.ItemCost;

public class ArmorerNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public ArmorerNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== STATIC TRADES (Always Available) =====

        // ===== VANILLA IRON ARMOR =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_HELMET),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_CHESTPLATE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_LEGGINGS),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 5),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_BOOTS),
                8, 5, 0.05F
        ));

        // ===== VANILLA CHAINMAIL ARMOR =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.of(new ItemCost(Items.IRON_NUGGET, 8)),
                new ItemStack(Items.CHAINMAIL_HELMET),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 7),
                java.util.Optional.of(new ItemCost(Items.IRON_NUGGET, 12)),
                new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                java.util.Optional.of(new ItemCost(Items.IRON_NUGGET, 10)),
                new ItemStack(Items.CHAINMAIL_LEGGINGS),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.of(new ItemCost(Items.IRON_NUGGET, 6)),
                new ItemStack(Items.CHAINMAIL_BOOTS),
                6, 8, 0.05F
        ));

        // ===== VANILLA DIAMOND ARMOR =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                java.util.Optional.of(new ItemCost(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_HELMET),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                java.util.Optional.of(new ItemCost(Items.DIAMOND, 4)),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                java.util.Optional.of(new ItemCost(Items.DIAMOND, 3)),
                new ItemStack(Items.DIAMOND_LEGGINGS),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 12),
                java.util.Optional.of(new ItemCost(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_BOOTS),
                3, 15, 0.05F
        ));

        // ===== MYTHRIL ARMOR =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_HELMET),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 7)),
                new ItemStack(ModItems.MYTHRIL_CHESTPLATE),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 28),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 6)),
                new ItemStack(ModItems.MYTHRIL_LEGGINGS),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                java.util.Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_BOOTS),
                2, 12, 0.05F
        ));

        // ===== SHIELDS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.SHIELD),
                10, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                java.util.Optional.of(new ItemCost(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.CRYSTAL_SHIELD),
                3, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                java.util.Optional.of(new ItemCost(Items.OAK_LOG, 24)),
                new ItemStack(ModItems.NATURE_SHIELD),
                3, 10, 0.05F
        ));

        // ===== CRAFTING MATERIALS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                java.util.Optional.of(new ItemCost(Items.IRON_INGOT, 2)),
                new ItemStack(ModItems.MYTHRIL_INGOT),
                12, 10, 0.1F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
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
        this.offers = new MerchantOffers();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.ARMORER);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.ARMORER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.ARMORER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.ARMORER);
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
        return Component.translatable("entity.dagmod.armorer_npc");
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
        // Armorer has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0F, 1.0F);
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
        // Don't get pushed by other entities
    }
}
