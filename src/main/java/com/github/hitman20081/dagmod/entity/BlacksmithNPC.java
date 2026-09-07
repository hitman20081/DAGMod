package com.github.hitman20081.dagmod.entity;

import com.github.hitman20081.dagmod.item.ModItems;
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

public class BlacksmithNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;

    public BlacksmithNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.offers = new MerchantOffers();

        // ===== BUY VANILLA RAW ORES (Player sells ores → gets emeralds) =====
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.RAW_IRON, 8),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.RAW_GOLD, 6),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 3),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.RAW_COPPER, 16),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 1),
                24, 3, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.COAL, 16),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 1),
                24, 3, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.REDSTONE, 16),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.LAPIS_LAZULI, 12),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_SILVER, 1),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 4),
                16, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.QUARTZ, 12),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                24, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(Items.AMETHYST_SHARD, 8),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                24, 5, 0.05F
        ));

        // ===== BUY MOD RAW ORES =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_MYTHRIL, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 6),
                12, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_RUBY, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 3),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_SAPPHIRE, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 3),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_CITRINE, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                16, 6, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_TANZANITE, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 4),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_TOPAZ, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
                16, 6, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_ZIRCON, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 3),
                16, 8, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_PINK_GARNET, 4),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 3),
                16, 8, 0.05F
        ));

        // ===== REPAIR SERVICES (Sell repair materials & anvils) =====
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.ANVIL),
                8, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.IRON_INGOT, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                java.util.Optional.empty(),
                new ItemStack(ModItems.COIN_SILVER, 2),
                12, 10, 0.05F
        ));
        this.offers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 6),
                java.util.Optional.of(new ItemCost(Items.IRON_INGOT, 2)),
                new ItemStack(ModItems.MYTHRIL_INGOT),
                12, 10, 0.1F
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
                            Component.literal("<Blacksmith> ").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.GOLD))
                                    .append(Component.literal("Bring me your ores and I'll give you a fair price. Need repairs? I've got materials.").withStyle(s -> s.withColor(net.minecraft.ChatFormatting.YELLOW))));
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
        return Component.translatable("entity.dagmod.blacksmith_npc");
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
                SoundEvents.ANVIL_USE, this.getSoundSource(), 1.0F, 1.0F);
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
        return SoundEvents.ANVIL_USE;
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
