package dy.cruelty.blocks;

import dy.cruelty.Cruelty;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    private ModBlocks() {}

    private static Block register(String name,
                                  Function<AbstractBlock.Settings, Block> blockFactory,
                                  AbstractBlock.Settings settings,
                                  boolean shouldRegisterItem) {
        // 1. 创建方块的 RegistryKey
        RegistryKey<Block> blockKey = RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Cruelty.MOD_ID, name));

        // 2. 设置方块的 ID 并创建方块实例
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // 3. 如果需要注册 BlockItem
        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), Identifier.of(Cruelty.MOD_ID, name));
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useItemPrefixedTranslationKey());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        // 4. 注册方块到 BLOCK 注册表
        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    /**
     * 注册方块（同时注册 BlockItem）
     */
    private static Block register(String name,
                                  Function<AbstractBlock.Settings, Block> blockFactory,
                                  AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, true);
    }

    /**
     * 注册方块（仅方块，不注册 BlockItem）
     */
    private static Block registerBlockOnly(String name,
                                           Function<AbstractBlock.Settings, Block> blockFactory,
                                           AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, false);
    }

    public static final Block GRASS_TRAP = register(
            "grass_trap",
            GrassTrapBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.2f)
                    .nonOpaque()
                    .blockVision((state, world, pos) -> false)
                    .solidBlock((state, world, pos) -> false)
                    .sounds(BlockSoundGroup.AZALEA_LEAVES)
    );

    private static final LandPathNodeTypesRegistry.StaticPathNodeTypeProvider PATH_WALKABLE = (state, neighbor) -> PathNodeType.WALKABLE;

    public static void initialize() {
        LandPathNodeTypesRegistry.register(GRASS_TRAP, PATH_WALKABLE);
    }

    public static void registerItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register((itemGroup) -> {
                    itemGroup.add(GRASS_TRAP.asItem());
                });
    }
}
