package dy.cruelty.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * 使玩家无法进行冲刺动作
     */
    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), argsOnly = true)
    private boolean disablePlayerSprinting(boolean sprinting) {
        if ((Object) this instanceof PlayerEntity && sprinting) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (!player.isCreative()) return false;
        }
        return sprinting;
    }

    /**
     * 使玩家下蹲时高度变为0.875格高
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyCrouchingHitbox(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // 只对玩家生效
        if (entity instanceof PlayerEntity && pose == EntityPose.CROUCHING) {
            float width = entity.getDimensions(EntityPose.STANDING).width();
            float height = 14.0F / 16.0F;
            cir.setReturnValue(EntityDimensions.fixed(width, height));
        }
    }
}
