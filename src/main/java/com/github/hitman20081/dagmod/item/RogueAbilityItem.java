package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.block.ClassSelectionAltarBlock;
import com.github.hitman20081.dagmod.class_system.rogue.RogueAbilityManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RogueAbilityItem extends Item {

    public enum RogueAbility {
        SMOKE_BOMB("Smoke Bomb", Formatting.DARK_GRAY),
        POISON_DAGGER("Poison Dagger", Formatting.DARK_GREEN),
        SHADOW_STEP("Shadow Step", Formatting.DARK_PURPLE);

        private final String name;
        private final Formatting color;

        RogueAbility(String name, Formatting color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Formatting getColor() {
            return color;
        }

        public RogueAbility next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    private static final String SELECTED_ABILITY_KEY = "SelectedAbility";

    public RogueAbilityItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // FIXED: use getUuid() and case-insensitive check
        String playerClass = ClassSelectionAltarBlock.getPlayerClass(serverPlayer.getUuid());
        if (!playerClass.equalsIgnoreCase("rogue")) {
            player.sendMessage(Text.literal("Only Rogues can use this item! (You are: " + playerClass + ")")
                    .formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        if (player.isSneaking()) {
            cycleAbility(stack, serverPlayer);
            return ActionResult.SUCCESS;
        }

        RogueAbility selectedAbility = getSelectedAbility(stack);
        boolean success = useAbility(serverPlayer, selectedAbility);

        return success ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    private void cycleAbility(ItemStack stack, ServerPlayerEntity player) {
        RogueAbility current = getSelectedAbility(stack);
        RogueAbility next = current.next();

        //
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putString(SELECTED_ABILITY_KEY, next.name());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        player.sendMessage(Text.literal("Selected: " + next.getName())
                .formatted(next.getColor()), true);
    }

    private RogueAbility getSelectedAbility(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        if (!component.isEmpty()) {
            NbtCompound nbt = component.copyNbt();
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

    private boolean useAbility(ServerPlayerEntity player, RogueAbility ability) {
        return switch (ability) {
            case SMOKE_BOMB -> RogueAbilityManager.useSmokeBomb(player);
            case POISON_DAGGER -> RogueAbilityManager.usePoisonDagger(player);
            case SHADOW_STEP -> RogueAbilityManager.useShadowStep(player);
        };
    }

    @Override
    public Text getName(ItemStack stack) {
        RogueAbility selected = getSelectedAbility(stack);
        return Text.literal("Rogue Tome: " + selected.getName())
                .formatted(selected.getColor());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}