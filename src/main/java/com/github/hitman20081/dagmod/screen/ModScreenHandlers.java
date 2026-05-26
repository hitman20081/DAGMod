package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

public class ModScreenHandlers {
    public static final MenuType<GemPolishingStationScreenHandler> GEM_POLISHING_STATION_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_polishing_station_screen_handler"),
                    new ExtendedMenuType<>(GemPolishingStationScreenHandler::new, BlockPos.STREAM_CODEC)
            );

    public static final MenuType<GemInfusingStationScreenHandler> GEM_INFUSING_STATION_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_infusing_station_screen_handler"),
                    new ExtendedMenuType<>(GemInfusingStationScreenHandler::new, BlockPos.STREAM_CODEC)
            );

    public static final MenuType<GemCuttingStationScreenHandler> GEM_CUTTING_STATION_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "gem_cutting_station_screen_handler"),
                    new ExtendedMenuType<>(GemCuttingStationScreenHandler::new, BlockPos.STREAM_CODEC)
            );

    public static final MenuType<IronChestScreenHandler> IRON_CHEST_SCREEN_HANDLER =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "iron_chest_screen_handler"),
                    new ExtendedMenuType<>(IronChestScreenHandler::new, BlockPos.STREAM_CODEC)
            );

    public static void registerScreenHandlers() {
        DagMod.LOGGER.info("Registering Screen Handlers for " + DagMod.MOD_ID);
    }
}
