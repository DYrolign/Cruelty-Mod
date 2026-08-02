package dy.cruelty.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    private PlayerEntity player = (PlayerEntity) (Object) this;

    private boolean isTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() == Items.STICK) return true;
        if (stack.isIn(ItemTags.PICKAXES) ||
                stack.isIn(ItemTags.AXES) ||
                stack.isIn(ItemTags.SHOVELS) ||
                stack.isIn(ItemTags.HOES) ||
                //stack.isIn(ItemTags.SWORDS)||
                stack.getItem() == Items.STICK ||
                stack.getItem() == Items.SHEARS) {
            return true;
        }
        return stack.get(DataComponentTypes.TOOL) != null;
    }

    /**
     * 修改玩家破坏方块的速度
     * 根据饱食度进一步降低
     */
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        ItemStack stack = player.getMainHandStack();
        float multiplierTool = isTool(stack) ? 0.6F : 0.15F;
        float multiplierHunger = player.getHungerManager().getFoodLevel() < 6 ? 0.6F : 1F;
        cir.setReturnValue(originalSpeed * multiplierTool * multiplierHunger);
    }

    /**
     * 修改玩家地面移动速度
     * 根据饱食度进一步降低
     */
    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyMovementSpeed(CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        float multiplier = player.getHungerManager().getFoodLevel() < 6 ? 0.30F : 0.80F;
        if (!player.isCreative() && !player.isSpectator()) {
            cir.setReturnValue(originalSpeed * multiplier);
        }
    }

    /**
     * 修改玩家空中移动速度
     * 根据饱食度进一步降低
     */
    @Inject(method = "getOffGroundSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        float multiplier = player.getHungerManager().getFoodLevel() < 6 ? 0.08F : 0.40F;
        if (!player.isCreative() && !player.isSpectator()) {
            cir.setReturnValue(originalSpeed * multiplier);
        }
    }

    /**
     * 修改玩家各种动作的饱食度降低程度
     */
    @Overwrite
    public void addExhaustion(float exhaustion) {
        if (!player.getAbilities().invulnerable) {
            if (!player.getEntityWorld().isClient()) {
                float multiplier;
                if (player.isSprinting()) multiplier = 16.0F;
                else if (player.isSwimming()) multiplier = 64.0F;
                else multiplier = 2.0F;
                player.getHungerManager().addExhaustion(exhaustion * multiplier);
            }
        }
    }
}

