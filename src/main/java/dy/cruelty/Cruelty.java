package dy.cruelty;

import dy.cruelty.blocks.ModBlocks;
import dy.cruelty.datagen.CrueltyRecipeProvider;
import dy.cruelty.utils.BlockRenderUtil;
import dy.cruelty.utils.SpawnUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cruelty implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {
	public static final String MOD_ID = "cruelty";
	public static final String MOD_NAME = "Cruelty";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Cruelty Injected!");
		ModBlocks.initialize();
		ModBlocks.registerItemGroups();
		SpawnUtil.registerNewSpawnPos();
	}

	@Override
	public void onInitializeClient() {
		BlockRenderUtil.initialize();
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(CrueltyRecipeProvider::new);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
