package com.github.hitman20081.dagmod.entity;

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
import com.github.hitman20081.dagmod.item.ModItems;
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

public class BakerNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;

    public BakerNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.offers = new MerchantOffers();

        // ===== BAKED GOODS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.BREAD, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKIE, 16),
                12, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 5),
                java.util.Optional.empty(),
                new ItemStack(Items.PUMPKIN_PIE, 4),
                10, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.CAKE),
                6, 10, 0.05F
        ));

        // ===== PREMIUM FOODS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 12),
                java.util.Optional.empty(),
                new ItemStack(Items.GOLDEN_APPLE, 2),
                4, 15, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 6),
                java.util.Optional.empty(),
                new ItemStack(Items.GOLDEN_CARROT, 8),
                8, 8, 0.05F
        ));

        // ===== COOKED MEATS =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKED_BEEF, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKED_PORKCHOP, 8),
                16, 5, 0.05F
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
                    serverPlayer.sendSystemMessage(
                            Component.literal("<Baker> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                    .append(Component.literal("Fresh from the oven! Take a look at what I've got today.").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
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
        return Component.translatable("entity.dagmod.baker_npc");
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
        // Baker has static offers
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
