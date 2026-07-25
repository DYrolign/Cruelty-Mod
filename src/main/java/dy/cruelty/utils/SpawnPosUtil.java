package dy.cruelty.utils;

import dy.cruelty.Cruelty;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SpawnPosUtil {
    @Nullable
    private static BlockPos findBiomePos(ServerWorld world, BlockPos origin, boolean doWaterCheck, int radius, int step, List<RegistryKey<Biome>> targetBiomeKeys) {
        // 螺旋搜索
        for (int r = 0; r <= radius; r += step) {
            for (int dx = -r; dx <= r; dx += step) {
                for (int dz = -r; dz <= r; dz += step) {
                    // 只检查当前半径的边界点
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    int x = origin.getX()+dx;
                    int z = origin.getZ()+dz;
                    int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
                    BlockPos pos = new BlockPos(x, y, z);

                    // 脚下水体检测
                    if (doWaterCheck) {
                        if (world.getFluidState(pos.down()).isIn(FluidTags.WATER)) continue;
                    }

                    // 生物群系匹配
                    var biomeKey = world.getBiome(pos).getKey();
                    if (biomeKey.isEmpty()) continue;
                    if (!targetBiomeKeys.contains(biomeKey.get())) continue;

                    boolean isBreathable = world.getBlockState(pos.up(1)).isAir() && world.getBlockState(pos.up(2)).isAir();
                    // 上方空气检查
                    if (isBreathable) return pos;
                }
            }
        }
        return null;
    }

    private static final List<RegistryKey<Biome>> BEACH_BIOMES_LIST = List.of(
            BiomeKeys.BEACH,
            BiomeKeys.STONY_SHORE,
            BiomeKeys.SNOWY_BEACH
    );

    private static final List<RegistryKey<Biome>> OCEAN_BIOMES_LIST = List.of(
            BiomeKeys.OCEAN,
            BiomeKeys.DEEP_OCEAN,
            BiomeKeys.LUKEWARM_OCEAN,
            BiomeKeys.DEEP_LUKEWARM_OCEAN,
            BiomeKeys.COLD_OCEAN,
            BiomeKeys.DEEP_COLD_OCEAN,
            BiomeKeys.WARM_OCEAN,
            BiomeKeys.FROZEN_OCEAN
    );

    public static void registerNewSpawnPos(){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerWorld world = server.getOverworld();

            WorldProperties.SpawnPoint currentSpawnPoint = world.getSpawnPoint();

            // 在出生点附近, 先搜索水体, 再搜索海岸
            BlockPos newSpawn = findBiomePos(
                    world,
                    findBiomePos(
                            world,
                            currentSpawnPoint.getPos(),
                            false,
                            1800,6,
                            OCEAN_BIOMES_LIST
                    ),
                    true,
                    1000,1,
                    BEACH_BIOMES_LIST
            );

            if (newSpawn != null){
                WorldProperties.SpawnPoint newSpawnPoint = new WorldProperties.SpawnPoint(
                        new GlobalPos(World.OVERWORLD, newSpawn),
                        0.0F,
                        0.0F
                );
                world.setSpawnPoint(newSpawnPoint);
                Cruelty.LOGGER.info("Successfully found new world spawn!");
                Cruelty.LOGGER.info("New world spawn:{}.", newSpawn);
            } else {
                Cruelty.LOGGER.warn("No suitable biome found near spawn, keeping original spawn.");
            }
        });
    }
}
