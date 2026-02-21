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

import java.util.OptionalInt;

public class VillageMerchantNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;

    public VillageMerchantNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.offers = new TradeOfferList();

        // ===== LIGHTING & BASICS =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                java.util.Optional.empty(),
                new ItemStack(Items.TORCH, 16),
                24, 3, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.LANTERN, 4),
                16, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.CAMPFIRE, 2),
                12, 5, 0.05F
        ));

        // ===== HOME & SHELTER =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.RED_BED),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.GLASS, 16),
                16, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.CHEST, 2),
                16, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.CRAFTING_TABLE),
                12, 5, 0.05F
        ));

        // ===== TOOLS & UTILITY =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.BUCKET),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.COMPASS),
                8, 8, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.CLOCK),
                8, 8, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                java.util.Optional.empty(),
                new ItemStack(Items.SPYGLASS),
                6, 10, 0.05F
        ));

        // ===== TRAVEL & EXPLORATION =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.MAP),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.LEAD, 2),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.SADDLE),
                4, 10, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                java.util.Optional.empty(),
                new ItemStack(Items.NAME_TAG),
                6, 10, 0.05F
        ));

        // ===== FARMING SUPPLIES =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 1),
                java.util.Optional.empty(),
                new ItemStack(Items.BONE_MEAL, 8),
                24, 3, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 2),
                java.util.Optional.empty(),
                new ItemStack(Items.HAY_BLOCK, 4),
                16, 5, 0.05F
        ));

        // ===== DYES & DECORATIONS =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.PAINTING, 3),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.FLOWER_POT, 4),
                12, 5, 0.05F
        ));
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
                this.setCustomer(player);

                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(
                            Text.literal("<Village Merchant> ").styled(s -> s.withColor(net.minecraft.util.Formatting.GOLD))
                                    .append(Text.literal("Welcome to our village! I've got a bit of everything.").styled(s -> s.withColor(net.minecraft.util.Formatting.YELLOW))),
                            false
                    );
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

        if (optionalSyncId.isPresent() && player instanceof ServerPlayerEntity serverPlayer) {
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
        return Text.translatable("entity.dagmod.village_merchant_npc");
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
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_VILLAGER_YES, this.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    public void onSellingItem(ItemStack stack) {
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    public void setExperienceFromServer(int experience) {
    }

    @Override
    public boolean isLeveledMerchant() {
        return false;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    public void pushAwayFrom(net.minecraft.entity.Entity entity) {
    }
}
