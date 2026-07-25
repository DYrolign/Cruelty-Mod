package dy.cruelty.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class StickMixin {
    /**
     * 修改木棍的挖掘速度
     */
    @Inject(method = "getMiningSpeed", at = @At("HEAD"), cancellable = true)
    private void modifyStickDestroySpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        // 检查当前物品是否为木棍
        if ((Object) this == Items.STICK) {
            float speed = 1.0F;
            // 如果目标方块可以被镐、斧、锹、锄开采，则速度为 2.0F
            if (state.isIn(BlockTags.PICKAXE_MINEABLE) ||
                    state.isIn(BlockTags.AXE_MINEABLE) ||
                    state.isIn(BlockTags.SHOVEL_MINEABLE) ||
                    state.isIn(BlockTags.HOE_MINEABLE)) {
                speed = 2.0F;
            }
            cir.setReturnValue(speed);
        }
    }

    /**
     * 修改木棍对工具掉落判定的影响
     */
    @Inject(method = "isCorrectForDrops", at = @At("HEAD"), cancellable = true)
    private void modifyStickCorrectness(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Items.STICK) {
            boolean suitable = false;
            // 对于镐类方块，木棍始终无效（即使不需要高级工具）
            if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
                suitable = false;
            }
            // 对于斧、锹、锄类方块，木棍有效
            else if (state.isIn(BlockTags.AXE_MINEABLE) ||
                    state.isIn(BlockTags.SHOVEL_MINEABLE) ||
                    state.isIn(BlockTags.HOE_MINEABLE)) {
                suitable = true;
            }
            cir.setReturnValue(suitable);
        }
    }
}