package dy.cruelty.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        float original = cir.getReturnValue();
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();
        if(isTool(stack)){
            cir.setReturnValue(original * 0.6f);
        }
        else cir.setReturnValue(original * 0.15f);
    }

    private boolean isTool(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (stack.getItem() == Items.STICK || stack.getItem() == Items.SHEARS) {
            return true;
        }

        // 方法1：检查是否属于工具标签（推荐）
        if (stack.isIn(ItemTags.PICKAXES) ||
                stack.isIn(ItemTags.AXES) ||
                stack.isIn(ItemTags.SHOVELS) ||
                stack.isIn(ItemTags.HOES) ||
                stack.isIn(ItemTags.SWORDS)) {
            return true;
        }

        // 方法2：检查是否具有 ToolComponent（兜底）
        return stack.get(DataComponentTypes.TOOL) != null;
    }
}
