package com.github.hitman20081.dagmod.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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

public class EnchantsmithNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;
    private final TradeOfferList staticOffers;

    public EnchantsmithNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.staticOffers = new TradeOfferList();
        this.offers = new TradeOfferList();
        initializeTrades();
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.ENCHANTSMITH);
            List<TradeOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.ENCHANTSMITH, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
    }

    private void initializeTrades() {
        World world = this.getEntityWorld();
        if (world == null) return;

        // ===== ENCHANTING SUPPLIES =====
        // Lapis Lazuli
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LAPIS_LAZULI, 4),
                16, 2, 0.05F
        ));
        // Experience Bottles
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.EXPERIENCE_BOTTLE, 1),
                12, 5, 0.05F
        ));
        // Bookshelf
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.BOOK, 3)),
                new ItemStack(Items.BOOKSHELF),
                8, 5, 0.05F
        ));

        // ===== ENCHANTED BOOKS - Basic =====
        addEnchantedBookTrade(Enchantments.PROTECTION, 1, 8, 6, 8);
        addEnchantedBookTrade(Enchantments.SHARPNESS, 1, 8, 6, 8);
        addEnchantedBookTrade(Enchantments.EFFICIENCY, 1, 8, 6, 8);
        addEnchantedBookTrade(Enchantments.UNBREAKING, 1, 10, 5, 10);
        addEnchantedBookTrade(Enchantments.FEATHER_FALLING, 1, 6, 6, 6);
        addEnchantedBookTrade(Enchantments.FIRE_ASPECT, 1, 12, 4, 12);
        addEnchantedBookTrade(Enchantments.LOOTING, 1, 14, 3, 15);
        addEnchantedBookTrade(Enchantments.FORTUNE, 1, 14, 3, 15);
        addEnchantedBookTrade(Enchantments.SILK_TOUCH, 1, 20, 2, 20);

        // ===== ENCHANTED BOOKS - Advanced =====
        addEnchantedBookTrade(Enchantments.PROTECTION, 3, 20, 3, 15);
        addEnchantedBookTrade(Enchantments.SHARPNESS, 3, 20, 3, 15);
        addEnchantedBookTrade(Enchantments.EFFICIENCY, 3, 18, 3, 12);
        addEnchantedBookTrade(Enchantments.UNBREAKING, 3, 24, 2, 18);
        addEnchantedBookTrade(Enchantments.LOOTING, 3, 32, 1, 25);
        addEnchantedBookTrade(Enchantments.FORTUNE, 3, 32, 1, 25);
        addEnchantedBookTrade(Enchantments.MENDING, 1, 36, 1, 30);

        // ===== SPELL SCROLLS - Basic =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.PAPER, 4)),
                new ItemStack(ModItems.HEAL_SCROLL),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                Optional.of(new TradedItem(Items.BLAZE_POWDER, 2)),
                new ItemStack(ModItems.FIREBALL_SCROLL),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                Optional.of(new TradedItem(Items.GOLDEN_APPLE, 1)),
                new ItemStack(ModItems.ABSORPTION_SCROLL),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.ENDER_PEARL, 1)),
                new ItemStack(ModItems.TELEPORT_SCROLL),
                8, 6, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                Optional.of(new TradedItem(Items.LAPIS_LAZULI, 8)),
                new ItemStack(ModItems.MANA_SHIELD_SCROLL),
                6, 10, 0.05F
        ));

        // ===== SPELL SCROLLS - Advanced =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.LIGHTNING_ROD, 1)),
                new ItemStack(ModItems.LIGHTNING_SCROLL),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 14),
                Optional.of(new TradedItem(Items.BLUE_ICE, 4)),
                new ItemStack(ModItems.FROST_NOVA_SCROLL),
                3, 15, 0.05F
        ));

        // ===== WANDS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 16),
                Optional.of(new TradedItem(Items.STICK, 1)),
                new ItemStack(ModItems.APPRENTICE_WAND),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 32),
                Optional.of(new TradedItem(Items.BLAZE_ROD, 2)),
                new ItemStack(ModItems.ADEPT_WAND),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 16),
                Optional.of(new TradedItem(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.MASTER_WAND),
                1, 30, 0.05F
        ));

        // ===== MAGE ABILITY ITEMS =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 24),
                Optional.of(new TradedItem(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.ARCANE_ORB),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 28),
                Optional.of(new TradedItem(Items.CLOCK, 1)),
                new ItemStack(ModItems.TEMPORAL_CRYSTAL),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                Optional.of(new TradedItem(Items.LAPIS_LAZULI, 16)),
                new ItemStack(ModItems.MANA_CATALYST),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.DIAMOND, 8),
                Optional.of(new TradedItem(Items.TOTEM_OF_UNDYING, 1)),
                new ItemStack(ModItems.BARRIER_CHARM),
                1, 25, 0.05F
        ));

        // ===== CONSUMABLES =====
        this.staticOffers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                Optional.of(new TradedItem(Items.AMETHYST_SHARD, 4)),
                new ItemStack(ModItems.MANA_CRYSTAL),
                8, 8, 0.05F
        ));
    }

    private void addEnchantedBookTrade(RegistryKey<Enchantment> enchantmentKey, int level, int emeraldCost, int maxUses, int xp) {
        World world = this.getEntityWorld();
        if (world == null) return;

        world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).ifPresent(registry -> {
            Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntry = registry.getOptional(enchantmentKey);
            enchantmentEntry.ifPresent(entry -> {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
                        ItemEnchantmentsComponent.DEFAULT);
                builder.add(entry, level);
                book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

                this.staticOffers.add(new TradeOffer(
                        new TradedItem(Items.EMERALD, emeraldCost),
                        Optional.of(new TradedItem(Items.BOOK, 1)),
                        book,
                        maxUses, xp, 0.05F
                ));
            });
        });
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.ENCHANTSMITH);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.ENCHANTSMITH);
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
        return Text.translatable("entity.dagmod.enchantsmith_npc");
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
        // Enchantsmith has static offers
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, this.getSoundCategory(), 1.0F, 1.0F);
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
        return SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE;
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
