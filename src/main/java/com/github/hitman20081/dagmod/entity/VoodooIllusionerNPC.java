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

public class VoodooIllusionerNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public VoodooIllusionerNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== DARK INGREDIENTS =====
        // Ender Pearls
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.ENDER_PEARL, 2),
                8, 6, 0.05F
        ));
        // Blaze Rods
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.BLAZE_ROD, 2),
                10, 5, 0.05F
        ));
        // Ghast Tears
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.empty(),
                new ItemStack(Items.GHAST_TEAR, 1),
                6, 10, 0.05F
        ));
        // Echo Shards
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.ECHO_SHARD, 2),
                4, 12, 0.05F
        ));
        // Phantom Membrane
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.PHANTOM_MEMBRANE, 2),
                8, 6, 0.05F
        ));
        // Wither Rose
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.WITHER_ROSE, 1),
                4, 12, 0.05F
        ));
        // Fermented Spider Eye
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.of(new TradedItem(Items.SPIDER_EYE, 1)),
                new ItemStack(Items.FERMENTED_SPIDER_EYE, 2),
                10, 4, 0.05F
        ));
        // Skulls
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                Optional.empty(),
                new ItemStack(Items.SKELETON_SKULL, 1),
                4, 15, 0.05F
        ));

        // ===== POTIONS & BREWING =====
        // Brewing Stand
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.BLAZE_ROD, 1)),
                new ItemStack(Items.BREWING_STAND),
                4, 8, 0.05F
        ));
        // Glass Bottles
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.GLASS_BOTTLE, 8),
                16, 2, 0.05F
        ));
        // Nether Wart
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.NETHER_WART, 4),
                12, 4, 0.05F
        ));

        // ===== REBIRTH POTIONS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.GHAST_TEAR, 2)),
                new ItemStack(ModItems.POTION_OF_RACIAL_REBIRTH),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.POTION_OF_CLASS_REBIRTH),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.POTION_OF_TOTAL_REBIRTH),
                1, 30, 0.05F
        ));

        // ===== RESET CRYSTALS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                Optional.of(new TradedItem(Items.AMETHYST_SHARD, 16)),
                new ItemStack(ModItems.RACE_RESET_CRYSTAL),
                2, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.CLASS_RESET_CRYSTAL),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(ModItems.DRAGON_HEART, 1)),
                new ItemStack(ModItems.CHARACTER_RESET_CRYSTAL),
                1, 30, 0.05F
        ));

        // ===== ROGUE ITEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.VOID_BLADE),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.PHANTOM_MEMBRANE, 4)),
                new ItemStack(ModItems.VANISH_CLOAK),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.SPIDER_EYE, 8)),
                new ItemStack(ModItems.POISON_VIAL),
                3, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.of(new TradedItem(Items.WITHER_ROSE, 1)),
                new ItemStack(ModItems.ASSASSINS_MARK),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 18),
                Optional.of(new TradedItem(Items.BONE, 8)),
                new ItemStack(ModItems.ROGUE_ABILITY_TOME),
                2, 15, 0.05F
        ));

        // ===== SHADOW WEAPONS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 28),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOWFANG_DAGGER),
                1, 18, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 36),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                new ItemStack(ModItems.SHADOWFANG_SWORD),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOW_SHIELD),
                1, 18, 0.05F
        ));

        // ===== MYSTICAL CONSUMABLES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.FERMENTED_SPIDER_EYE, 2)),
                new ItemStack(ModItems.VAMPIRE_DUST),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.PHANTOM_MEMBRANE, 2)),
                new ItemStack(ModItems.PHANTOM_DUST),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 2)),
                new ItemStack(ModItems.SHADOW_BLEND),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.GHAST_TEAR, 1)),
                new ItemStack(ModItems.LAST_STAND_POWDER),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 14),
                Optional.of(new TradedItem(Items.CLOCK, 1)),
                new ItemStack(ModItems.TIME_DISTORTION),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                Optional.of(new TradedItem(Items.PHANTOM_MEMBRANE, 2)),
                new ItemStack(ModItems.PERFECT_DODGE),
                2, 15, 0.05F
        ));

        // ===== ECHO DUST & POWDERS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 1)),
                new ItemStack(ModItems.ECHO_DUST, 4),
                8, 8, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells bones
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.BONE, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells spider eyes
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.SPIDER_EYE, 16),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                12, 3, 0.05F
        ));
        // Player sells rotten flesh
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.ROTTEN_FLESH, 32),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells phantom membrane
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.PHANTOM_MEMBRANE, 4),
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.VOODOO_ILLUSIONER);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.VOODOO_ILLUSIONER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.VOODOO_ILLUSIONER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.VOODOO_ILLUSIONER);
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
        return Text.translatable("entity.dagmod.voodoo_illusioner_npc");
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
        // Voodoo Illusioner has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, this.getSoundCategory(), 1.0F, 1.0F);
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
        return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
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
