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

/**
 * Hunter/Ranger NPC - Sells bows, arrows, leather goods, and mob drops.
 */
public class HunterNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public HunterNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();

        // ===== RANGED WEAPONS =====
        // Bow
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.BOW),
                8, 5, 0.05F
        ));
        // Crossbow
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.TRIPWIRE_HOOK, 1)),
                new ItemStack(Items.CROSSBOW),
                6, 8, 0.05F
        ));

        // ===== ARROWS =====
        // Regular Arrows
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.ARROW, 16),
                16, 2, 0.05F
        ));
        // Spectral Arrows
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.of(new TradedItem(Items.GLOWSTONE_DUST, 4)),
                new ItemStack(Items.SPECTRAL_ARROW, 8),
                12, 5, 0.05F
        ));
        // Tipped Arrows - Poison
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.of(new TradedItem(Items.ARROW, 8)),
                new ItemStack(Items.TIPPED_ARROW, 8),
                8, 8, 0.05F
        ));

        // ===== LEATHER & MATERIALS =====
        // Leather
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEATHER, 4),
                12, 4, 0.05F
        ));
        // Rabbit Hide
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RABBIT_HIDE, 4),
                12, 3, 0.05F
        ));
        // String
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.STRING, 8),
                16, 2, 0.05F
        ));
        // Feathers
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.FEATHER, 16),
                16, 2, 0.05F
        ));
        // Flint
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.FLINT, 16),
                16, 2, 0.05F
        ));

        // ===== LEATHER ARMOR =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.LEATHER_HELMET),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 5),
                Optional.empty(),
                new ItemStack(Items.LEATHER_CHESTPLATE),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.LEATHER_LEGGINGS),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEATHER_BOOTS),
                8, 4, 0.05F
        ));

        // ===== MOB DROPS =====
        // Bones
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BONE, 8),
                16, 2, 0.05F
        ));
        // Gunpowder
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.GUNPOWDER, 4),
                12, 4, 0.05F
        ));
        // Slimeball
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.SLIME_BALL, 4),
                10, 5, 0.05F
        ));
        // Honey Bottle
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.of(new TradedItem(Items.GLASS_BOTTLE, 1)),
                new ItemStack(Items.HONEY_BOTTLE),
                8, 5, 0.05F
        ));
        // Ink Sac
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.INK_SAC, 4),
                12, 3, 0.05F
        ));
        // Glow Ink Sac
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.GLOW_INK_SAC, 2),
                8, 6, 0.05F
        ));

        // ===== ANIMAL TAMING & EQUIPMENT =====
        // Saddle
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.empty(),
                new ItemStack(Items.SADDLE),
                4, 10, 0.05F
        ));
        // Lead
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEAD, 2),
                8, 5, 0.05F
        ));
        // Name Tag
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.NAME_TAG),
                4, 12, 0.05F
        ));
        // Horse Armor
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.IRON_HORSE_ARMOR),
                4, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_HORSE_ARMOR),
                2, 18, 0.05F
        ));

        // ===== TRACKING & NAVIGATION =====
        // Compass
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.COMPASS),
                6, 6, 0.05F
        ));
        // Recovery Compass
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.ECHO_SHARD, 8)),
                new ItemStack(Items.RECOVERY_COMPASS),
                2, 20, 0.05F
        ));
        // Spyglass
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.AMETHYST_SHARD, 2)),
                new ItemStack(Items.SPYGLASS),
                4, 10, 0.05F
        ));

        // ===== COOKED MEAT (Hunter's Provisions) =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_BEEF, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_PORKCHOP, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_MUTTON, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_RABBIT, 4),
                12, 3, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells leather
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.LEATHER, 8),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells raw meat
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.BEEF, 12),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells feathers
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.FEATHER, 24),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells rabbit hide
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.RABBIT_HIDE, 8),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                12, 3, 0.05F
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.HUNTER);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.HUNTER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.HUNTER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.HUNTER);
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
        return Text.translatable("entity.dagmod.hunter_npc");
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
        // Hunter has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_ARROW_HIT, this.getSoundCategory(), 1.0F, 1.0F);
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
        return SoundEvents.ENTITY_ARROW_HIT;
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
