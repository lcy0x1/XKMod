package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

	@Invoker("onStatusEffectUpgraded")
	public void invokeOnStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect);

}
