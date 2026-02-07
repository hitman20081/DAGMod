package com.github.hitman20081.dagmod.screen;

import com.github.hitman20081.dagmod.DagMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<GemPolishingStationScreenHandler> GEM_POLISHING_STATION_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(DagMod.MOD_ID, "gem_polishing_station_screen_handler"),
                    new ExtendedScreenHandlerType<>(GemPolishingStationScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ScreenHandlerType<GemInfusingStationScreenHandler> GEM_INFUSING_STATION_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(DagMod.MOD_ID, "gem_infusing_station_screen_handler"),
                    new ExtendedScreenHandlerType<>(GemInfusingStationScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ScreenHandlerType<GemCuttingStationScreenHandler> GEM_CUTTING_STATION_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(DagMod.MOD_ID, "gem_cutting_station_screen_handler"),
                    new ExtendedScreenHandlerType<>(GemCuttingStationScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static final ScreenHandlerType<IronChestScreenHandler> IRON_CHEST_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(DagMod.MOD_ID, "iron_chest_screen_handler"),
                    new ExtendedScreenHandlerType<>(IronChestScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static void registerScreenHandlers() {
        DagMod.LOGGER.info("Registering Screen Handlers for " + DagMod.MOD_ID);
    }
}
