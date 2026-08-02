package dy.cruelty.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GrassTrapBlock extends Block {
    // 视觉模型: 12~16
    private static final VoxelShape SHAPE = createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
    // 碰撞箱: 14~16
    private static final VoxelShape COLLISION_SHAPE = createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);

    public GrassTrapBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 只能放置在侧面（不能放在顶面或底面）
        Direction clickedFace = ctx.getSide();
        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            return null;
        }
        return this.getDefaultState();
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient() && entity instanceof LivingEntity) {
            // 潜行时不触发
            if (entity instanceof PlayerEntity player && player.isSneaking()) {
                return;
            }
            world.playSound(
                    null, pos,
                    SoundEvents.BLOCK_AZALEA_LEAVES_BREAK, SoundCategory.BLOCKS,
                    1.0f, 0.8f + world.random.nextFloat() * 0.4f
            );
            world.breakBlock(pos, false);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (entity instanceof LivingEntity) {
            super.onLandedUpon(world, state, pos, entity, fallDistance * 0.2f);
        } else {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        // 检查水平方向是否有支撑
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            // 检查支撑面
            if (Block.isFaceFullSquare(neighborState.getCollisionShape(world, neighborPos), direction.getOpposite())) {
                return true;
            }
            // 相邻方块是同类型（互相支撑）
            if (neighborState.getBlock() == this) {
                return true;
            }
            // 相邻方块是水 → 不能放置
            if (neighborState.getBlock() == Blocks.WATER) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }
}