package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(StatusEffectInstance.class)
public interface StatusEffectInstanceAccessor {
	
	@Accessor("duration")
    public void setDuration (int duration);

}
