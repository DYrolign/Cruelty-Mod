package dy.cruelty.utils;

import dy.cruelty.blocks.ModBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BlockRenderLayer;

public class BlockRenderUtil {
    public static void initialize(){
        BlockRenderLayerMap.putBlock(ModBlocks.GRASS_TRAP, BlockRenderLayer.CUTOUT);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return 0x8EB881; // 默认绿色
            }
            return BiomeColors.getGrassColor(world, pos);
        }, ModBlocks.GRASS_TRAP);
    }
}
