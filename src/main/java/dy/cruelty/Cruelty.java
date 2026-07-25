package dy.cruelty;

import dy.cruelty.utils.SpawnPosUtil;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cruelty implements ModInitializer {
	public static final String MOD_ID = "cruelty";
	public static final String MOD_NAME = "Cruelty";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Cruelty Injected!");
		SpawnPosUtil.registerNewSpawnPos();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
