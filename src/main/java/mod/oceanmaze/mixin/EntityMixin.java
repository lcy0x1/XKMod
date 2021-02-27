package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.oceanmaze.main.OceanMaze;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(at = @At("HEAD"), cancellable = true, method = "isTouchingWaterOrRain()Z")
	public void isTouchingWaterOrRain(CallbackInfoReturnable<Boolean> info) {
		Entity self = (Entity) (Object) this;
		if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) self;
			if (le.getStatusEffect(OceanMaze.SPONGE_WET) != null)
				info.setReturnValue(true);
		}
	}

}
