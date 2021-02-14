package mod.xinke.statuseffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;

public class XinkeEffect extends StatusEffect {

	public XinkeEffect() {
		super(StatusEffectType.BENEFICIAL, 0xFF0000);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		for (StatusEffectInstance eff : entity.getStatusEffects())
			if (eff.getEffectType().getType() == StatusEffectType.HARMFUL)
				entity.removeStatusEffect(eff.getEffectType());
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

}
