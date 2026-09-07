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
import com.github.hitman20081.dagmod.quest.QuestData;
import com.github.hitman20081.dagmod.quest.QuestManager;
import com.github.hitman20081.dagmod.trade.MerchantDialogue;
import com.github.hitman20081.dagmod.trade.MerchantType;
import com.github.hitman20081.dagmod.trade.RotatingTradeManager;
import com.github.hitman20081.dagmod.trade.RotatingTradeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
 * Mystery Merchant NPC - Sells weapons, armor sets, and special materials.
 * Tier gating: basic (always), advanced (night_watch complete), legendary (red_dragon_fury complete).
 */
public class MysteryMerchantNPC extends PathfinderMob implements Merchant {

    private Player customer;
    private MerchantOffers offers;
    private final MerchantOffers basicOffers;
    private final MerchantOffers advancedOffers;
    private final MerchantOffers legendaryOffers;

    public MysteryMerchantNPC(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.basicOffers = new MerchantOffers();
        this.advancedOffers = new MerchantOffers();
        this.legendaryOffers = new MerchantOffers();
        this.offers = new MerchantOffers();

        // ===== BASIC (Always available) =====

        // Mid-tier weapons
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 16), Optional.empty(), new ItemStack(ModItems.MYTHRIL_SWORD), 3, 5, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 24), Optional.of(new ItemCost(Items.GOLD_INGOT, 8)), new ItemStack(ModItems.GILDED_RAPIER), 2, 8, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 32), Optional.of(new ItemCost(Items.AMETHYST_SHARD, 16)), new ItemStack(ModItems.CRYSTAL_KATANA), 2, 10, 0.05F));

        // Basic shields
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 20), Optional.of(new ItemCost(Items.AMETHYST_SHARD, 8)), new ItemStack(ModItems.CRYSTAL_SHIELD), 2, 8, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 24), Optional.of(new ItemCost(Items.OAK_LOG, 32)), new ItemStack(ModItems.NATURE_SHIELD), 2, 8, 0.05F));

        // Mythril armor
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 24), Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 5)), new ItemStack(ModItems.MYTHRIL_HELMET), 2, 10, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 40), Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 8)), new ItemStack(ModItems.MYTHRIL_CHESTPLATE), 2, 10, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 36), Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 7)), new ItemStack(ModItems.MYTHRIL_LEGGINGS), 2, 10, 0.05F));
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 20), Optional.of(new ItemCost(ModItems.MYTHRIL_INGOT, 4)), new ItemStack(ModItems.MYTHRIL_BOOTS), 2, 10, 0.05F));

        // Materials
        this.basicOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 8), Optional.of(new ItemCost(Items.IRON_INGOT, 4)), new ItemStack(ModItems.MYTHRIL_INGOT), 8, 15, 0.1F));

        // ===== ADVANCED (Unlocked: survive the night watch) =====

        // High-tier weapons
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 48), Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 2)), new ItemStack(ModItems.DRAGONSCALE_SWORD), 1, 15, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 48), Optional.of(new ItemCost(Items.BLAZE_ROD, 8)), new ItemStack(ModItems.INFERNO_SWORD), 1, 15, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 40), Optional.of(new ItemCost(Items.ECHO_SHARD, 4)), new ItemStack(ModItems.SHADOWFANG_DAGGER), 1, 12, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 52), Optional.of(new ItemCost(Items.ECHO_SHARD, 8)), new ItemStack(ModItems.SHADOWFANG_SWORD), 1, 15, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 16), Optional.of(new ItemCost(Items.GHAST_TEAR, 4)), new ItemStack(ModItems.BLOODTHIRSTER_BLADE), 1, 15, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 56), Optional.of(new ItemCost(Items.BLUE_ICE, 16)), new ItemStack(ModItems.FROSTBITE_AXE), 1, 15, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 44), Optional.of(new ItemCost(Items.SPIDER_EYE, 8)), new ItemStack(ModItems.POISON_FANG_SPEAR), 1, 12, 0.05F));

        // Advanced shields
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 36), Optional.of(new ItemCost(Items.BLUE_ICE, 8)), new ItemStack(ModItems.FROST_SHIELD), 1, 12, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 40), Optional.of(new ItemCost(Items.BLAZE_ROD, 4)), new ItemStack(ModItems.INFERNO_SHIELD), 1, 12, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 40), Optional.of(new ItemCost(Items.ECHO_SHARD, 4)), new ItemStack(ModItems.SHADOW_SHIELD), 1, 12, 0.05F));
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 44), Optional.of(new ItemCost(Items.SUNFLOWER, 16)), new ItemStack(ModItems.SOLAR_SHIELD), 1, 15, 0.05F));

        // Dragon scale material (requires some proof of combat)
        this.advancedOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 4), Optional.of(new ItemCost(Items.NETHERITE_SCRAP, 1)), new ItemStack(ModItems.DRAGON_SCALE), 5, 20, 0.1F));

        // ===== LEGENDARY (Unlocked: slay the Red Dragon) =====

        // Epic weapons
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 24), Optional.of(new ItemCost(Items.NETHER_STAR, 1)), new ItemStack(ModItems.ETHEREAL_BLADE), 1, 20, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 20), Optional.of(new ItemCost(Items.TRIDENT, 1)), new ItemStack(ModItems.THUNDER_PIKE), 1, 20, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 28), Optional.of(new ItemCost(ModItems.SILMARIL, 1)), new ItemStack(ModItems.CRYSTALHAMMER), 1, 25, 0.05F));

        // Dragonscale armor
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 8), Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 5)), new ItemStack(ModItems.DRAGONSCALE_HELMET), 1, 15, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 12), Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 8)), new ItemStack(ModItems.DRAGONSCALE_CHESTPLATE), 1, 15, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 10), Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 7)), new ItemStack(ModItems.DRAGONSCALE_LEGGINGS), 1, 15, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 6), Optional.of(new ItemCost(ModItems.DRAGON_SCALE, 4)), new ItemStack(ModItems.DRAGONSCALE_BOOTS), 1, 15, 0.05F));

        // Shadow armor
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 32), Optional.of(new ItemCost(Items.ECHO_SHARD, 4)), new ItemStack(ModItems.SHADOW_HELMET), 1, 12, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 48), Optional.of(new ItemCost(Items.ECHO_SHARD, 6)), new ItemStack(ModItems.SHADOW_CHESTPLATE), 1, 12, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 44), Optional.of(new ItemCost(Items.ECHO_SHARD, 5)), new ItemStack(ModItems.SHADOW_LEGGINGS), 1, 12, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_COPPER, 28), Optional.of(new ItemCost(Items.ECHO_SHARD, 3)), new ItemStack(ModItems.SHADOW_BOOTS), 1, 12, 0.05F));

        // Legendary shields
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 16), Optional.of(new ItemCost(ModItems.DRAGON_BONE, 4)), new ItemStack(ModItems.DRAGONBONE_SHIELD), 1, 20, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 20), Optional.of(new ItemCost(Items.HEART_OF_THE_SEA, 1)), new ItemStack(ModItems.STORMGUARD_SHIELD), 1, 20, 0.05F));
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 32), Optional.of(new ItemCost(ModItems.DRAGON_HEART, 1)), new ItemStack(ModItems.CELESTIAL_SHIELD), 1, 25, 0.05F));

        // Premium materials
        this.legendaryOffers.add(new MerchantOffer(new ItemCost(ModItems.COIN_SILVER, 16), Optional.of(new ItemCost(Items.AMETHYST_SHARD, 32)), new ItemStack(ModItems.SILMARIL), 1, 25, 0.05F));

        this.offers.addAll(basicOffers);
    }

    private MerchantOffers buildOffersForPlayer(ServerPlayer player) {
        QuestData data = QuestManager.getInstance().getPlayerData(player);
        boolean advanced = data.isQuestCompleted("night_watch");
        boolean legendary = data.isQuestCompleted("red_dragon_fury");

        MerchantOffers result = new MerchantOffers();
        result.addAll(basicOffers);
        if (advanced) result.addAll(advancedOffers);
        if (legendary) result.addAll(legendaryOffers);

        if (RotatingTradeManager.getInstance().isInitialized()) {
            int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(MerchantType.MYSTERY_MERCHANT);
            List<MerchantOffer> rotatingTrades = RotatingTradeRegistry.getRotatingTrades(MerchantType.MYSTERY_MERCHANT, rotationIndex);
            result.addAll(rotatingTrades);
        }

        return result;
    }

    private void sendUnlockHints(ServerPlayer player, boolean advanced, boolean legendary) {
        if (!advanced) {
            player.sendSystemMessage(Component.literal("[Mystery Merchant] ").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD)
                    .append(Component.literal("Want the good weapons? Prove you can handle yourself in the dark first.").withStyle(ChatFormatting.GRAY)));
        } else if (!legendary) {
            player.sendSystemMessage(Component.literal("[Mystery Merchant] ").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD)
                    .append(Component.literal("My finest gear is forged from dragon parts. Bring me proof of a kill.").withStyle(ChatFormatting.DARK_RED)));
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
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestData data = QuestManager.getInstance().getPlayerData(serverPlayer);
                    boolean advanced = data.isQuestCompleted("night_watch");
                    boolean legendary = data.isQuestCompleted("red_dragon_fury");

                    this.offers = buildOffersForPlayer(serverPlayer);
                    this.setTradingPlayer(player);

                    MerchantDialogue.sendGreeting(serverPlayer, MerchantType.MYSTERY_MERCHANT);
                    MerchantDialogue.sendRotationHint(serverPlayer, MerchantType.MYSTERY_MERCHANT);
                    sendUnlockHints(serverPlayer, advanced, legendary);
                } else {
                    this.setTradingPlayer(player);
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
                    syncId, this.getOffers(), level, this.getVillagerXp(), this.showProgressBar(), this.canRefreshTrades()));
        }
    }

    public boolean canRefreshTrades() { return false; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.dagmod.mystery_merchant_npc");
    }

    public boolean hasCustomer() { return this.customer != null; }

    @Override
    public boolean isClientSide() { return this.level().isClientSide(); }

    @Override
    public Player getTradingPlayer() { return this.customer; }

    @Override
    public void setTradingPlayer(Player player) { this.customer = player; }

    @Override
    public MerchantOffers getOffers() { return this.offers; }

    @Override
    public void overrideOffers(MerchantOffers offers) {}

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {}

    @Override
    public int getVillagerXp() { return 0; }

    @Override
    public void overrideXp(int experience) {}

    @Override
    public boolean showProgressBar() { return false; }

    @Override
    public SoundEvent getNotifyTradeSound() { return SoundEvents.VILLAGER_YES; }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && this.distanceTo(player) <= 6.0;
    }

    @Override
    public boolean isPersistenceRequired() { return true; }

    public boolean damage(DamageSource source, float amount) { return false; }

    public void pushAwayFrom(net.minecraft.world.entity.Entity entity) {}
}
