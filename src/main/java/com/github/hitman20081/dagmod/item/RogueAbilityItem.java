package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbilityManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class RogueAbilityItem extends Item {

    public enum RogueAbility {
        SMOKE_BOMB("Smoke Bomb", ChatFormatting.DARK_GRAY),
        POISON_DAGGER("Poison Dagger", ChatFormatting.DARK_GREEN),
        SHADOW_STEP("Shadow Step", ChatFormatting.DARK_PURPLE);

        private final String name;
        private final ChatFormatting color;

        RogueAbility(String name, ChatFormatting color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public ChatFormatting getColor() {
            return color;
        }

        public RogueAbility next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    private static final String SELECTED_ABILITY_KEY = "SelectedAbility";

    public RogueAbilityItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // FIXED: use getUuid() and case-insensitive check
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(serverPlayer.getUUID());
        if (!playerClass.equalsIgnoreCase("rogue")) {
            player.sendSystemMessage(Component.literal("Only Rogues can use this item! (You are: " + playerClass + ")")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            cycleAbility(stack, serverPlayer);
            return InteractionResult.SUCCESS;
        }

        RogueAbility selectedAbility = getSelectedAbility(stack);
        boolean success = useAbility(serverPlayer, selectedAbility);

        return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    private void cycleAbility(ItemStack stack, ServerPlayer player) {
        RogueAbility current = getSelectedAbility(stack);
        RogueAbility next = current.next();

        //
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putString(SELECTED_ABILITY_KEY, next.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        player.sendOverlayMessage(Component.literal("Selected: " + next.getName())
                .withStyle(next.getColor()));
    }

    private RogueAbility getSelectedAbility(ItemStack stack) {
        CustomData component = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!component.isEmpty()) {
            CompoundTag nbt = component.copyTag();
            // FIXED: Handle Optional<String>
            if (nbt.contains(SELECTED_ABILITY_KEY)) {
                return nbt.getString(SELECTED_ABILITY_KEY)
                        .map(str -> {
                            try {
                                return RogueAbility.valueOf(str);
                            } catch (IllegalArgumentException e) {
                                return RogueAbility.SMOKE_BOMB;
                            }
                        })
                        .orElse(RogueAbility.SMOKE_BOMB);
            }
        }
        return RogueAbility.SMOKE_BOMB;
    }

    private boolean useAbility(ServerPlayer player, RogueAbility ability) {
        return switch (ability) {
            case SMOKE_BOMB -> RogueAbilityManager.useSmokeBomb(player);
            case POISON_DAGGER -> RogueAbilityManager.usePoisonDagger(player);
            case SHADOW_STEP -> RogueAbilityManager.useShadowStep(player);
        };
    }

    @Override
    public Component getName(ItemStack stack) {
        RogueAbility selected = getSelectedAbility(stack);
        return Component.literal("Rogue Tome: " + selected.getName())
                .withStyle(selected.getColor());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}