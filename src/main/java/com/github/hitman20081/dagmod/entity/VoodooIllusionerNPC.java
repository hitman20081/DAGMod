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
import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.block.RaceSelectionAltarBlock;
import com.github.hitman20081.dagmod.data.PlayerDataManager;
import com.github.hitman20081.dagmod.item.ModItems;
import com.github.hitman20081.dagmod.trade.MerchantDialogue;
import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.github.hitman20081.dagmod.trade.RotatingTradeRegistry;
import net.minecraft.ChatFormatting;
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

public class VoodooIllusionerNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers staticOffers;

    public VoodooIllusionerNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.staticOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== DARK INGREDIENTS =====
        // Ender Pearls
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 4),
                Optional.empty(),
                new ItemStack(Items.ENDER_PEARL, 2),
                8, 6, 0.05F
        ));
        // Echo Shards
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                Optional.empty(),
                new ItemStack(Items.ECHO_SHARD, 2),
                4, 12, 0.05F
        ));
        // Wither Rose
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                Optional.empty(),
                new ItemStack(Items.WITHER_ROSE, 1),
                4, 12, 0.05F
        ));
        // Skulls
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 12),
                Optional.empty(),
                new ItemStack(Items.SKELETON_SKULL, 1),
                4, 15, 0.05F
        ));

        // ===== REBIRTH POTIONS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 24),
                Optional.of(new ItemCost(Items.GHAST_TEAR, 2)),
                new ItemStack(ModItems.POTION_OF_RACIAL_REBIRTH),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 24),
                Optional.of(new ItemCost(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.POTION_OF_CLASS_REBIRTH),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_SILVER, 16),
                Optional.of(new ItemCost(Items.NETHER_STAR, 1)),
                new ItemStack(ModItems.POTION_OF_TOTAL_REBIRTH),
                1, 30, 0.05F
        ));

        // ===== RESET CRYSTALS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 32),
                Optional.of(new ItemCost(Items.AMETHYST_SHARD, 16)),
                new ItemStack(ModItems.RACE_RESET_CRYSTAL),
                2, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_SILVER, 8),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.CLASS_RESET_CRYSTAL),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_SILVER, 16),
                Optional.of(new ItemCost(ModItems.DRAGON_HEART, 1)),
                new ItemStack(ModItems.CHARACTER_RESET_CRYSTAL),
                1, 30, 0.05F
        ));

        // ===== ROGUE ITEMS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 20),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 4)),
                new ItemStack(ModItems.VOID_BLADE),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 24),
                Optional.of(new ItemCost(Items.PHANTOM_MEMBRANE, 4)),
                new ItemStack(ModItems.VANISH_CLOAK),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 16),
                Optional.of(new ItemCost(Items.SPIDER_EYE, 8)),
                new ItemStack(ModItems.POISON_VIAL),
                3, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_SILVER, 8),
                Optional.of(new ItemCost(Items.WITHER_ROSE, 1)),
                new ItemStack(ModItems.ASSASSINS_MARK),
                1, 25, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 18),
                Optional.of(new ItemCost(Items.BONE, 8)),
                new ItemStack(ModItems.ROGUE_ABILITY_TOME),
                2, 15, 0.05F
        ));

        // ===== SHADOW WEAPONS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 28),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOWFANG_DAGGER),
                1, 18, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 36),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 8)),
                new ItemStack(ModItems.SHADOWFANG_SWORD),
                1, 20, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 32),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 6)),
                new ItemStack(ModItems.SHADOW_SHIELD),
                1, 18, 0.05F
        ));

        // ===== MYSTICAL CONSUMABLES =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                Optional.of(new ItemCost(Items.FERMENTED_SPIDER_EYE, 2)),
                new ItemStack(ModItems.VAMPIRE_DUST),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 8),
                Optional.of(new ItemCost(Items.PHANTOM_MEMBRANE, 2)),
                new ItemStack(ModItems.PHANTOM_DUST),
                6, 10, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 10),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 2)),
                new ItemStack(ModItems.SHADOW_BLEND),
                4, 12, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 16),
                Optional.of(new ItemCost(Items.GHAST_TEAR, 1)),
                new ItemStack(ModItems.LAST_STAND_POWDER),
                2, 18, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 14),
                Optional.of(new ItemCost(Items.CLOCK, 1)),
                new ItemStack(ModItems.TIME_DISTORTION),
                2, 15, 0.05F
        ));
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 12),
                Optional.of(new ItemCost(Items.PHANTOM_MEMBRANE, 2)),
                new ItemStack(ModItems.PERFECT_DODGE),
                2, 15, 0.05F
        ));

        // ===== ECHO DUST & POWDERS =====
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(ModItems.COIN_COPPER, 6),
                Optional.of(new ItemCost(Items.ECHO_SHARD, 1)),
                new ItemStack(ModItems.ECHO_DUST, 4),
                8, 8, 0.05F
        ));

        // ===== BUY FROM PLAYERS =====
        // Player sells bones
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.BONE, 32),
                Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 1),
                16, 2, 0.05F
        ));
        // Player sells spider eyes
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.SPIDER_EYE, 16),
                Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 1),
                12, 3, 0.05F
        ));
        // Player sells rotten flesh
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.ROTTEN_FLESH, 32),
                Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 1),
                16, 2, 0.05F
        ));
        // Player sells phantom membrane
        this.staticOffers.add(new MerchantOffer(
                new ItemCost(Items.PHANTOM_MEMBRANE, 4),
                Optional.empty(),
                new ItemStack(ModItems.COIN_COPPER, 2),
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
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.VOODOO_ILLUSIONER);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.VOODOO_ILLUSIONER, rotationIndex);
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
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack held = player.getMainHandItem();

            // Check for reset items before opening trade menu
            if (held.getItem() == ModItems.RACE_RESET_CRYSTAL
                    || held.getItem() == ModItems.POTION_OF_RACIAL_REBIRTH) {
                handleRaceReset(serverPlayer, held);
                return InteractionResult.CONSUME;
            }
            if (held.getItem() == ModItems.CLASS_RESET_CRYSTAL
                    || held.getItem() == ModItems.POTION_OF_CLASS_REBIRTH) {
                handleClassReset(serverPlayer, held);
                return InteractionResult.CONSUME;
            }
            if (held.getItem() == ModItems.CHARACTER_RESET_CRYSTAL
                    || held.getItem() == ModItems.POTION_OF_TOTAL_REBIRTH) {
                handleCharacterReset(serverPlayer, held);
                return InteractionResult.CONSUME;
            }
        }
        if (!this.level().isClientSide()) {
            if (this.isAlive() && this.canInteract(player) && !this.hasCustomer() && !player.isShiftKeyDown()) {
                // Rebuild offers to include current rotating trades
                rebuildOffers();

                this.setTradingPlayer(player);

                // Send merchant dialogue
                if (player instanceof ServerPlayer serverPlayer) {
                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.VOODOO_ILLUSIONER);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.VOODOO_ILLUSIONER);
                }

                this.openOfferScreen(player, this.getDisplayName(), 1);
                return InteractionResult.CONSUME;
            } else if (this.hasCustomer() && this.getTradingPlayer() == player) {
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void handleRaceReset(ServerPlayer player, ItemStack item) {
        String oldRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
        if (oldRace.equals("none")) {
            sendLine(player, "You have no heritage registered, mon.", ChatFormatting.RED);
            return;
        }
        RaceSelectionAltarBlock.resetPlayerRace(player.getUUID());
        PlayerDataManager.savePlayerData(player);
        item.shrink(1);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ILLUSIONER_CAST_SPELL, this.getSoundSource(), 1.0F, 0.8F);
        sendLine(player, "Your heritage is unbound. " + oldRace + " no more...", ChatFormatting.LIGHT_PURPLE);
        player.sendSystemMessage(Component.empty());
        InnkeeperGarrickNPC.showRaceMenu(player);
    }

    private void handleClassReset(ServerPlayer player, ItemStack item) {
        String oldClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (oldClass.equals("none")) {
            sendLine(player, "You have no calling registered, mon.", ChatFormatting.RED);
            return;
        }
        ClassSelectionAltarBlock.resetPlayerClass(player.getUUID());
        PlayerDataManager.savePlayerData(player);
        item.shrink(1);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ILLUSIONER_CAST_SPELL, this.getSoundSource(), 1.0F, 0.8F);
        sendLine(player, "Your calling fades into shadow. " + oldClass + " no more...", ChatFormatting.LIGHT_PURPLE);
        player.sendSystemMessage(Component.empty());
        InnkeeperGarrickNPC.showClassMenu(player);
    }

    private void handleCharacterReset(ServerPlayer player, ItemStack item) {
        String oldRace = RaceSelectionAltarBlock.getPlayerRace(player.getUUID());
        String oldClass = ClassSelectionAltarBlock.getPlayerClass(player.getUUID());
        if (oldRace.equals("none") && oldClass.equals("none")) {
            sendLine(player, "You have nothing to unbind, mon.", ChatFormatting.RED);
            return;
        }
        RaceSelectionAltarBlock.resetPlayerRace(player.getUUID());
        ClassSelectionAltarBlock.resetPlayerClass(player.getUUID());
        PlayerDataManager.savePlayerData(player);
        item.shrink(1);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ILLUSIONER_CAST_SPELL, this.getSoundSource(), 1.0F, 0.6F);
        sendLine(player, "Both bindings severed. " + oldRace + " " + oldClass + " no more...", ChatFormatting.LIGHT_PURPLE);
        player.sendSystemMessage(Component.empty());
        InnkeeperGarrickNPC.showRaceMenu(player);
    }

    private void sendLine(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(
            Component.literal("[Voodoo Illusioner] ").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
                .append(Component.literal(message).withStyle(color)));
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
        return Component.translatable("entity.dagmod.voodoo_illusioner_npc");
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
        // Voodoo Illusioner has static offers
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ILLUSIONER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
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
        return SoundEvents.ILLUSIONER_CAST_SPELL;
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
