package com.github.hitman20081.dagmod.enchantment;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
    public static int getEnchantmentLevel(ItemStack stack, World world, String enchantId) {
        if (stack.isEmpty()) return 0;

        var enchReg = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryKey<Enchantment> key = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("dagmod", enchantId));
        Optional<RegistryEntry.Reference<Enchantment>> entry = enchReg.getOptional(key);

        if (entry.isEmpty()) return 0;

        ItemEnchantmentsComponent enchantments = stack.getOrDefault(
                DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        return enchantments.getLevel(entry.get());
    }

    private static void registerBlockBreakEffects() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return;

            ItemStack mainHand = serverPlayer.getMainHandStack();
            ServerWorld serverWorld = (ServerWorld) world;

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

    private static void handleMidasTouch(ServerWorld world, BlockState state, BlockPos pos) {
        if (state.isOf(Blocks.GILDED_BLACKSTONE)) {
            Block.dropStack(world, pos, new ItemStack(Items.GOLD_BLOCK));
        }
    }

    private static void handleMudCollector(ServerWorld world, BlockState state, BlockPos pos) {
        if (!world.isRaining()) return;

        Block block = state.getBlock();
        if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK
                || block == Blocks.COARSE_DIRT || block == Blocks.ROOTED_DIRT) {
            Block.dropStack(world, pos, new ItemStack(Items.MUD));
        }
    }

    private static void handleTunneling(ServerWorld world, ServerPlayerEntity player, BlockPos center) {
        // Raycast to determine which face the player is mining
        HitResult hitResult = player.raycast(5.0, 0.0f, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) return;

        Direction face = blockHit.getSide();
        // Get the two axes perpendicular to the mining direction
        Direction.Axis axis = face.getAxis();

        TUNNELING_ACTIVE.set(true);
        try {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // Skip center (already broken)

                    BlockPos offset;
                    switch (axis) {
                        case X -> offset = center.add(0, i, j);
                        case Y -> offset = center.add(i, 0, j);
                        case Z -> offset = center.add(i, j, 0);
                        default -> { continue; }
                    }

                    BlockState targetState = world.getBlockState(offset);
                    if (targetState.isAir() || targetState.isOf(Blocks.BEDROCK)
                            || !targetState.getFluidState().isEmpty()
                            || targetState.getHardness(world, offset) < 0) {
                        continue;
                    }

                    world.breakBlock(offset, true, player);
                }
            }
        } finally {
            TUNNELING_ACTIVE.set(false);
        }
    }

    private static void registerDeathEffects() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.getEntityWorld().isClient()) return;
            if (!(damageSource.getAttacker() instanceof ServerPlayerEntity player)) return;

            // Lucky Looter — check HEAD slot
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            int luckyLevel = getEnchantmentLevel(helmet, player.getEntityWorld(), "lucky_looter_enchantment");
            if (luckyLevel > 0) {
                handleLuckyLooter((ServerWorld) player.getEntityWorld(), player, entity instanceof HostileEntity);
            }
        });
    }

    private static void handleLuckyLooter(ServerWorld world, ServerPlayerEntity player, boolean isHostile) {
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

        Block.dropStack(world, player.getBlockPos(), loot);
    }
}
