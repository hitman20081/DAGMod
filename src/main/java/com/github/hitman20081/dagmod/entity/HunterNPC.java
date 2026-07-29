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

/**
 * Hunter/Ranger NPC - Sells bows, arrows, leather goods, and mob drops.
 */
public class HunterNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public HunterNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== RANGED WEAPONS =====
        // Bow
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.BOW),
                8, 5, 0.05F
        ));
        // Crossbow
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.TRIPWIRE_HOOK, 1)),
                new ItemStack(Items.CROSSBOW),
                6, 8, 0.05F
        ));

        // ===== ARROWS =====
        // Regular Arrows
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.ARROW, 16),
                16, 2, 0.05F
        ));
        // Spectral Arrows
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.of(new ItemCost(Items.GLOWSTONE_DUST, 4)),
                new ItemStack(Items.SPECTRAL_ARROW, 8),
                12, 5, 0.05F
        ));
        // Tipped Arrows - Poison
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.of(new ItemCost(Items.ARROW, 8)),
                new ItemStack(Items.TIPPED_ARROW, 8),
                8, 8, 0.05F
        ));

        // ===== LEATHER & MATERIALS =====
        // Leather
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEATHER, 4),
                12, 4, 0.05F
        ));
        // Rabbit Hide
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.RABBIT_HIDE, 4),
                12, 3, 0.05F
        ));
        // String
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.STRING, 8),
                16, 2, 0.05F
        ));
        // Feathers
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.FEATHER, 16),
                16, 2, 0.05F
        ));
        // Flint
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.FLINT, 16),
                16, 2, 0.05F
        ));

        // ===== LEATHER ARMOR =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.LEATHER_HELMET),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 5),
                Optional.empty(),
                new ItemStack(Items.LEATHER_CHESTPLATE),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.LEATHER_LEGGINGS),
                8, 4, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEATHER_BOOTS),
                8, 4, 0.05F
        ));

        // ===== MOB DROPS =====
        // Bones
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.BONE, 8),
                16, 2, 0.05F
        ));
        // Gunpowder
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.GUNPOWDER, 4),
                12, 4, 0.05F
        ));
        // Slimeball
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.SLIME_BALL, 4),
                10, 5, 0.05F
        ));
        // Honey Bottle
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.of(new ItemCost(Items.GLASS_BOTTLE, 1)),
                new ItemStack(Items.HONEY_BOTTLE),
                8, 5, 0.05F
        ));
        // Ink Sac
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.INK_SAC, 4),
                12, 3, 0.05F
        ));
        // Glow Ink Sac
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.GLOW_INK_SAC, 2),
                8, 6, 0.05F
        ));

        // ===== ANIMAL TAMING & EQUIPMENT =====
        // Saddle
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.empty(),
                new ItemStack(Items.SADDLE),
                4, 10, 0.05F
        ));
        // Lead
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 2),
                Optional.empty(),
                new ItemStack(Items.LEAD, 2),
                8, 5, 0.05F
        ));
        // Name Tag
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.NAME_TAG),
                4, 12, 0.05F
        ));
        // Horse Armor
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.empty(),
                new ItemStack(Items.IRON_HORSE_ARMOR),
                4, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.DIAMOND, 2)),
                new ItemStack(Items.DIAMOND_HORSE_ARMOR),
                2, 18, 0.05F
        ));

        // ===== TRACKING & NAVIGATION =====
        // Compass
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 4),
                Optional.empty(),
                new ItemStack(Items.COMPASS),
                6, 6, 0.05F
        ));
        // Recovery Compass
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 8)),
                new ItemStack(Items.RECOVERY_COMPASS),
                2, 20, 0.05F
        ));
        // Spyglass
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.AMETHYST_SHARD, 2)),
                new ItemStack(Items.SPYGLASS),
                4, 10, 0.05F
        ));

        // ===== COOKED MEAT (Hunter's Provisions) =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_BEEF, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_PORKCHOP, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_MUTTON, 6),
                12, 3, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.COOKED_RABBIT, 4),
                12, 3, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells leather
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.LEATHER, 8),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells raw meat
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.BEEF, 12),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells feathers
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.FEATHER, 24),
                Optional.empty(),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
        ));
        // Player sells rabbit hide
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.RABBIT_HIDE, 8),
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
        this.offers = new MerchantOffers();

        // Add all static offers
        this.offers.addAll(this.staticOffers);

        // Add rotating trades from the registry
        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.HUNTER);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.HUNTER, rotationIndex);
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.HUNTER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.HUNTER);
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
        return Component.translatable("entity.dagmod.hunter_npc");
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
        // Hunter has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ARROW_HIT, this.getSoundSource(), 1.0F, 1.0F);
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
        return SoundEvents.ARROW_HIT;
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
