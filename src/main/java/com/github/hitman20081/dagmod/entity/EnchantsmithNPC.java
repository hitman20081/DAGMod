package com.github.hitman20081.dagmod.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
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

public class EnchantsmithNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public EnchantsmithNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();
        initializeTrades();
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.ENCHANTSMITH);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.ENCHANTSMITH, rotationIndex);
            this.offers.addAll(rotatingTrades);
        }
    }

    private void initializeTrades() {
        Level world = this.level();
        if (world == null) return;

        // ===== ENCHANTING SUPPLIES =====
        // Lapis Lazuli
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                Optional.empty(),
                new ItemStack(Items.LAPIS_LAZULI, 4),
                16, 2, 0.05F
        ));
        // Experience Bottles
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 3),
                Optional.empty(),
                new ItemStack(Items.EXPERIENCE_BOTTLE, 1),
                12, 5, 0.05F
        ));
        // Bookshelf
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.BOOK, 3)),
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
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.of(new ItemCost(Items.PAPER, 4)),
                new ItemStack(ModItems.HEAL_SCROLL),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                Optional.of(new ItemCost(Items.BLAZE_POWDER, 2)),
                new ItemStack(ModItems.FIREBALL_SCROLL),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 8),
                Optional.of(new ItemCost(Items.GOLDEN_APPLE, 1)),
                new ItemStack(ModItems.ABSORPTION_SCROLL),
                6, 8, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.ENDER_PEARL, 1)),
                new ItemStack(ModItems.TELEPORT_SCROLL),
                8, 6, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 10),
                Optional.of(new ItemCost(Items.LAPIS_LAZULI, 8)),
                new ItemStack(ModItems.MANA_SHIELD_SCROLL),
                6, 10, 0.05F
        ));

        // ===== SPELL SCROLLS - Advanced =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse("minecraft:lightning_rod")), 1)),
                new ItemStack(ModItems.LIGHTNING_SCROLL),
                3, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 14),
                Optional.of(new ItemCost(Items.BLUE_ICE, 4)),
                new ItemStack(ModItems.FROST_NOVA_SCROLL),
                3, 15, 0.05F
        ));

        // ===== WANDS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                Optional.of(new ItemCost(Items.STICK, 1)),
                new ItemStack(ModItems.APPRENTICE_WAND),
                2, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 32),
                Optional.of(new ItemCost(Items.BLAZE_ROD, 2)),
                new ItemStack(ModItems.ADEPT_WAND),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 16),
                Optional.of(new ItemCost(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.MASTER_WAND),
                1, 30, 0.05F
        ));

        // ===== MAGE ABILITY ITEMS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 24),
                Optional.of(new ItemCost(Items.AMETHYST_SHARD, 8)),
                new ItemStack(ModItems.ARCANE_ORB),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 28),
                Optional.of(new ItemCost(Items.CLOCK, 1)),
                new ItemStack(ModItems.TEMPORAL_CRYSTAL),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                Optional.of(new ItemCost(Items.LAPIS_LAZULI, 16)),
                new ItemStack(ModItems.MANA_CATALYST),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 8),
                Optional.of(new ItemCost(Items.TOTEM_OF_UNDYING, 1)),
                new ItemStack(ModItems.BARRIER_CHARM),
                1, 25, 0.05F
        ));

        // ===== CONSUMABLES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 6),
                Optional.of(new ItemCost(Items.AMETHYST_SHARD, 4)),
                new ItemStack(ModItems.MANA_CRYSTAL),
                8, 8, 0.05F
        ));
    }

    private void addEnchantedBookTrade(ResourceKey<Enchantment> enchantmentKey, int level, int emeraldCost, int maxUses, int xp) {
        Level world = this.level();
        if (world == null) return;

        var registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        registry.get(enchantmentKey).ifPresent(entry -> {
            {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(
                        ItemEnchantments.EMPTY);
                builder.set(entry, level);
                book.set(DataComponents.STORED_ENCHANTMENTS, builder.toImmutable());

                this.staticOffers.add(new MerchantOffer(
                        new ItemCost(Items.EMERALD, emeraldCost),
                        Optional.of(new ItemCost(Items.BOOK, 1)),
                        book,
                        maxUses, xp, 0.05F
                ));
            }
        });
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
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.ENCHANTSMITH);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.ENCHANTSMITH);
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
        return Component.translatable("entity.dagmod.enchantsmith_npc");
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
        // Enchantsmith has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, this.getSoundSource(), 1.0F, 1.0F);
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
        return SoundEvents.ENCHANTMENT_TABLE_USE;
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
