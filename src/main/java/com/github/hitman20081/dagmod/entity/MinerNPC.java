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

public class MinerNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public MinerNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== PICKAXES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.IRON_PICKAXE),
                8, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                Optional.of(new TradedItem(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_PICKAXE),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(ModItems.MYTHRIL_INGOT, 3)),
                new ItemStack(ModItems.MYTHRIL_PICKAXE),
                2, 15, 0.05F
        ));

        // ===== MINING SUPPLIES =====
        // Torches
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.TORCH, 16),
                16, 2, 0.05F
        ));
        // Ladders
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LADDER, 8),
                16, 2, 0.05F
        ));
        // Rails
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.RAIL, 16),
                12, 4, 0.05F
        ));
        // Powered Rails
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.of(new TradedItem(Items.GOLD_INGOT, 1)),
                new ItemStack(Items.POWERED_RAIL, 4),
                8, 8, 0.05F
        ));
        // Minecart
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.MINECART),
                6, 6, 0.05F
        ));
        // TNT
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.of(new TradedItem(Items.GUNPOWDER, 2)),
                new ItemStack(Items.TNT, 2),
                8, 8, 0.05F
        ));

        // ===== COMMON ORES & MATERIALS =====
        // Coal
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COAL, 16),
                16, 2, 0.05F
        ));
        // Iron Ingots
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.IRON_INGOT, 4),
                12, 4, 0.05F
        ));
        // Copper Ingots
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COPPER_INGOT, 8),
                12, 3, 0.05F
        ));
        // Gold Ingots
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.GOLD_INGOT, 3),
                8, 6, 0.05F
        ));
        // Redstone
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.REDSTONE, 8),
                12, 4, 0.05F
        ));
        // Lapis Lazuli
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LAPIS_LAZULI, 6),
                12, 4, 0.05F
        ));
        // Diamond
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.DIAMOND, 1),
                4, 12, 0.05F
        ));

        // ===== RAW ORES (For smelting) =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RAW_IRON, 4),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.RAW_GOLD, 3),
                10, 5, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RAW_COPPER, 8),
                12, 3, 0.05F
        ));

        // ===== MOD MATERIALS =====
        // Raw Mythril
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.IRON_INGOT, 2)),
                new ItemStack(ModItems.RAW_MYTHRIL, 2),
                6, 10, 0.05F
        ));
        // Mythril Ingot
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.IRON_INGOT, 4)),
                new ItemStack(ModItems.MYTHRIL_INGOT, 1),
                8, 12, 0.05F
        ));

        // ===== RAW GEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_RUBY, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_SAPPHIRE, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(ModItems.RAW_CITRINE, 2),
                6, 6, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 5),
                Optional.empty(),
                new ItemStack(ModItems.RAW_TANZANITE, 2),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(ModItems.RAW_TOPAZ, 2),
                6, 6, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_ZIRCON, 2),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(ModItems.RAW_PINK_GARNET, 2),
                6, 8, 0.05F
        ));

        // ===== PROCESSED GEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(ModItems.RAW_RUBY, 2)),
                new ItemStack(ModItems.RUBY, 1),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(ModItems.RAW_SAPPHIRE, 2)),
                new ItemStack(ModItems.SAPPHIRE, 1),
                4, 12, 0.05F
        ));

        // ===== GEM CUTTER TOOL =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                Optional.of(new TradedItem(Items.IRON_INGOT, 4)),
                new ItemStack(ModItems.GEM_CUTTER_TOOL),
                2, 15, 0.05F
        ));

        // ===== SPECIAL - Buy ores from players =====
        // Player sells coal
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.COAL, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells iron
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.IRON_INGOT, 8),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                12, 3, 0.05F
        ));
        // Player sells raw gems
        this.staticOffers.add(new TradeOffer(
                new TradedItem(ModItems.RAW_RUBY, 4),
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
        this.offers = new TradeOfferList();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.MINER);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.MINER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.MINER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.MINER);
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
        return Text.translatable("entity.dagmod.miner_npc");
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
        // Miner has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_ANVIL_USE, this.getSoundCategory(), 0.5F, 1.0F);
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
        return SoundEvents.BLOCK_ANVIL_USE;
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
