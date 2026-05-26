package com.github.hitman20081.dagmod.enchantment;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CustomEnchantmentEffects {

    private static final ThreadLocal<Boolean> TUNNELING_ACTIVE = ThreadLocal.withInitial(() -> false);

    public static void register() {
        registerBlockBreakEffects();
        registerDeathEffects();
        DagMod.LOGGER.info("Custom enchantment effects registered!");
    }

    /**
     * Get the level of a dagmod enchantment on an item stack.
     */
    public static int getEnchantmentLevel(ItemStack stack, Level world, String enchantId) {
        if (stack.isEmpty()) return 0;

        var enchReg = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("dagmod", enchantId));
        var entry = enchReg.get(key);

        if (entry.isEmpty()) return 0;

        ItemEnchantments enchantments = stack.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return enchantments.getLevel(entry.get());
    }

    private static void registerBlockBreakEffects() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;

            ItemStack mainHand = serverPlayer.getMainHandItem();
            ServerLevel serverWorld = (ServerLevel) world;

            // Midas Touch
            if (getEnchantmentLevel(mainHand, world, "midas_touch_enchantment") > 0) {
                handleMidasTouch(serverWorld, state, pos);
            }

            // Mud Collector
            if (getEnchantmentLevel(mainHand, world, "mud_collector") > 0) {
                handleMudCollector(serverWorld, state, pos);
            }

            // Tunneling
            if (!TUNNELING_ACTIVE.get() && getEnchantmentLevel(mainHand, world, "tunneling_enchantment") > 0) {
                handleTunneling(serverWorld, serverPlayer, pos);
            }
        });
    }

    private static void handleMidasTouch(ServerLevel world, BlockState state, BlockPos pos) {
        if (state.getBlock() == Blocks.GILDED_BLACKSTONE) {
            Block.popResource(world, pos, new ItemStack(Items.GOLD_BLOCK));
        }
    }

    private static void handleMudCollector(ServerLevel world, BlockState state, BlockPos pos) {
        if (!world.isRaining()) return;

        Block block = state.getBlock();
        if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK
                || block == Blocks.COARSE_DIRT || block == Blocks.ROOTED_DIRT) {
            Block.popResource(world, pos, new ItemStack(Items.MUD));
        }
    }

    private static void handleTunneling(ServerLevel world, ServerPlayer player, BlockPos center) {
        // Raycast to determine which face the player is mining
        HitResult hitResult = player.pick(5.0, 0.0f, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) return;

        Direction face = blockHit.getDirection();
        // Get the two axes perpendicular to the mining direction
        Direction.Axis axis = face.getAxis();

        TUNNELING_ACTIVE.set(true);
        try {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // Skip center (already broken)

                    BlockPos offset;
                    switch (axis) {
                        case X -> offset = center.offset(0, i, j);
                        case Y -> offset = center.offset(i, 0, j);
                        case Z -> offset = center.offset(i, j, 0);
                        default -> { continue; }
                    }

                    BlockState targetState = world.getBlockState(offset);
                    if (targetState.isAir() || targetState.getBlock() == Blocks.BEDROCK
                            || !targetState.getFluidState().isEmpty()
                            || targetState.getDestroySpeed(world, offset) < 0) {
                        continue;
                    }

                    world.destroyBlock(offset, true, player);
                }
            }
        } finally {
            TUNNELING_ACTIVE.set(false);
        }
    }

    private static void registerDeathEffects() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.level().isClientSide()) return;
            if (!(damageSource.getEntity() instanceof ServerPlayer player)) return;

            // Lucky Looter — check HEAD slot
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            int luckyLevel = getEnchantmentLevel(helmet, player.level(), "lucky_looter_enchantment");
            if (luckyLevel > 0) {
                handleLuckyLooter((ServerLevel) player.level(), player, entity instanceof Monster);
            }
        });
    }

    private static void handleLuckyLooter(ServerLevel world, ServerPlayer player, boolean isHostile) {
        // 25% chance per kill
        if (world.getRandom().nextFloat() >= 0.25f) return;

        ItemStack loot;
        if (isHostile) {
            // Better drops from hostile mobs
            int roll = world.getRandom().nextInt(5);
            loot = switch (roll) {
                case 0 -> new ItemStack(Items.DIAMOND, 1);
                case 1 -> new ItemStack(Items.EMERALD, 1 + world.getRandom().nextInt(2));
                case 2 -> new ItemStack(Items.GOLD_INGOT, 1 + world.getRandom().nextInt(3));
                case 3 -> new ItemStack(Items.IRON_INGOT, 2 + world.getRandom().nextInt(3));
                default -> new ItemStack(Items.EXPERIENCE_BOTTLE, 1 + world.getRandom().nextInt(2));
            };
        } else {
            // Modest drops from passive mobs
            int roll = world.getRandom().nextInt(3);
            loot = switch (roll) {
                case 0 -> new ItemStack(Items.IRON_NUGGET, 3 + world.getRandom().nextInt(5));
                case 1 -> new ItemStack(Items.GOLD_NUGGET, 2 + world.getRandom().nextInt(4));
                default -> new ItemStack(Items.EXPERIENCE_BOTTLE, 1);
            };
        }

        Block.popResource(world, player.blockPosition(), loot);
    }
}
