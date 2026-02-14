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

public class BakerNPC extends PathAwareEntity implements Merchant {

    private PlayerEntity customer;
    private TradeOfferList offers;

    public BakerNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.offers = new TradeOfferList();

        // ===== BAKED GOODS =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.BREAD, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 4),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKIE, 16),
                12, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 5),
                java.util.Optional.empty(),
                new ItemStack(Items.PUMPKIN_PIE, 4),
                10, 8, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 8),
                java.util.Optional.empty(),
                new ItemStack(Items.CAKE),
                6, 10, 0.05F
        ));

        // ===== PREMIUM FOODS =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 12),
                java.util.Optional.empty(),
                new ItemStack(Items.GOLDEN_APPLE, 2),
                4, 15, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 6),
                java.util.Optional.empty(),
                new ItemStack(Items.GOLDEN_CARROT, 8),
                8, 8, 0.05F
        ));

        // ===== COOKED MEATS =====
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKED_BEEF, 8),
                16, 5, 0.05F
        ));
        this.offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 3),
                java.util.Optional.empty(),
                new ItemStack(Items.COOKED_PORKCHOP, 8),
                16, 5, 0.05F
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
                            Text.literal("<Baker> ").styled(s -> s.withColor(net.minecraft.util.Formatting.GOLD))
                                    .append(Text.literal("Fresh from the oven! Take a look at what I've got today.").styled(s -> s.withColor(net.minecraft.util.Formatting.YELLOW))),
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
        return Text.translatable("entity.dagmod.baker_npc");
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
        // Baker has static offers
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
