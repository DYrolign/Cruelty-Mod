package dy.cruelty.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private LivingEntity entity = (LivingEntity) (Object) this;
    /**
     * 使玩家无法进行冲刺动作
     * 待实现
     *
    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), argsOnly = true)
    private boolean disablePlayerSprinting(boolean sprinting) {
        if (entity instanceof PlayerEntity && sprinting) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!player.isCreative() && player.isSwimming()) {
                player.setSwimming(false);
                return false;
            }
        }
        return sprinting;
    }*/

    /**
     * 使玩家氧气消耗量大幅增加
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void increaseOxygenConsumption(CallbackInfo ci) {
        if (entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            if(!player.getAbilities().invulnerable) {
                if (player.isSubmergedInWater()) {
                    int currentAir = player.getAir();
                    int extraCost = 1;
                    if (currentAir > 0 && currentAir < player.getMaxAir()) {
                        int newAir = Math.max(0, currentAir - extraCost);
                        player.setAir(newAir);
                    }
                }
            }
        }
    }

    /**
     * 使玩家下蹲时高度变为0.875格高
     */
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyCrouchingHitbox(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (entity instanceof PlayerEntity && pose == EntityPose.CROUCHING) {
            float width = entity.getDimensions(EntityPose.STANDING).width();
            float height = 14.0F / 16.0F;
            cir.setReturnValue(EntityDimensions.fixed(width, height));
        }
    }
}
