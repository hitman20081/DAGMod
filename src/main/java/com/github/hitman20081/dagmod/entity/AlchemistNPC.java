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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.ItemCost;
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

public class AlchemistNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;

    public AlchemistNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.offers = new MerchantOffers();

        // ===== BREWING EQUIPMENT =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.BREWING_STAND),
                6, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.CAULDRON),
                8, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                java.util.Optional.empty(),
                new ItemStack(Items.GLASS_BOTTLE, 6),
                24, 3, 0.05F
        ));

        // ===== ESSENTIAL BREWING INGREDIENTS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.BLAZE_ROD, 2),
                12, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.BLAZE_POWDER, 4),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.NETHER_WART, 4),
                16, 5, 0.05F
        ));

        // ===== POTION MODIFIERS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.REDSTONE, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.GLOWSTONE_DUST, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.GUNPOWDER, 4),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.DRAGON_BREATH, 2),
                6, 10, 0.05F
        ));

        // ===== POTION EFFECT INGREDIENTS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.SUGAR, 8),
                16, 3, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.GLISTERING_MELON_SLICE, 4),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.SPIDER_EYE, 4),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.FERMENTED_SPIDER_EYE, 2),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.MAGMA_CREAM, 4),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.GHAST_TEAR, 2),
                8, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.RABBIT_FOOT, 2),
                10, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.PHANTOM_MEMBRANE, 4),
                10, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.GOLDEN_CARROT, 4),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.PUFFERFISH, 2),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.TURTLE_SCUTE, 2),
                8, 8, 0.05F
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
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide()) {
            if (this.isAlive() && this.canInteract(player) && !this.hasCustomer() && !player.isShiftKeyDown()) {
                this.setTradingPlayer(player);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(
                            Component.literal("<Alchemist> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                    .append(Component.literal("Potions, reagents, and the secrets of transmutation. What do you need?").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
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
        return Component.translatable("entity.dagmod.alchemist_npc");
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
                SoundEvents.BREWING_STAND_BREW, this.getSoundSource(), 1.0F, 1.0F);
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
        return SoundEvents.BREWING_STAND_BREW;
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
