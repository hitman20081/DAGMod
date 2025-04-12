package com.github.hitman20081.dagmod;

import com.github.hitman20081.dagmod.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DagMod implements ModInitializer {
	public static final String MOD_ID = "dagmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModItems.registerModItems();

	}
}