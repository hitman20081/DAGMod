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

public class MinerNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public MinerNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== PICKAXES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.IRON_PICKAXE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 12),
                Optional.of(new ItemCost(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_PICKAXE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_PICKAXE),
                2, 15, 0.05F
        ));

        // ===== MINING SUPPLIES =====
        // Torches
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.TORCH, 16),
                16, 2, 0.05F
        ));
        // Ladders
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LADDER, 8),
                16, 2, 0.05F
        ));
        // Rails
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.RAIL, 16),
                12, 4, 0.05F
        ));
        // Powered Rails
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.of(new ItemCost(Items.GOLD_INGOT, 1)),
                new ItemStack(Items.POWERED_RAIL, 4),
                8, 8, 0.05F
        ));
        // Minecart
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.MINECART),
                6, 6, 0.05F
        ));
        // TNT
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.of(new ItemCost(Items.GUNPOWDER, 2)),
                new ItemStack(Items.TNT, 2),
                8, 8, 0.05F
        ));

        // ===== COMMON ORES & MATERIALS =====
        // Coal
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COAL, 16),
                16, 2, 0.05F
        ));
        // Iron Ingots
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.IRON_INGOT, 4),
                12, 4, 0.05F
        ));
        // Copper Ingots
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COPPER_INGOT, 8),
                12, 3, 0.05F
        ));
        // Gold Ingots
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.GOLD_INGOT, 3),
                8, 6, 0.05F
        ));
        // Redstone
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.REDSTONE, 8),
                12, 4, 0.05F
        ));
        // Lapis Lazuli
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LAPIS_LAZULI, 6),
                12, 4, 0.05F
        ));
        // Diamond
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 1),
                4, 12, 0.05F
        ));

        // ===== RAW ORES (For smelting) =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RAW_IRON, 4),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.RAW_GOLD, 3),
                10, 5, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RAW_COPPER, 8),
                12, 3, 0.05F
        ));

        // ===== MOD MATERIALS =====
        // Raw Mythril
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.IRON_INGOT, 2)),
                new ItemStack(ModItems.RAW_MYTHRIL, 2),
                6, 10, 0.05F
        ));
        // Mythril Ingot
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.of(new ItemCost(Items.IRON_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_INGOT, 1),
                8, 12, 0.05F
        ));

        // ===== RAW GEMS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_RUBY, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_SAPPHIRE, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(ModItems.RAW_CITRINE, 2),
                6, 6, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 5),
                Optional.empty(),
                new ItemStack(ModItems.RAW_TANZANITE, 2),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(ModItems.RAW_TOPAZ, 2),
                6, 6, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_ZIRCON, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_PINK_GARNET, 2),
                6, 8, 0.05F
        ));

        // ===== SPECIAL - Buy ores from players =====
        // Player sells coal
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.COAL, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells iron
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.IRON_INGOT, 8),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                12, 3, 0.05F
        ));
        // Player sells raw gems
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.RAW_RUBY, 4),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 2),
                8, 5, 0.05F
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.MINER);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.MINER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.MINER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.MINER);
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
        return Component.translatable("entity.dagmod.miner_npc");
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
        // Miner has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ANVIL_USE, this.getSoundSource(), 0.5F, 1.0F);
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
        // Don't get pushed by other entities
    }
}
