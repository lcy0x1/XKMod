package mod.oceanmaze.effects;

import mod.oceanmaze.main.OceanMaze;
import mod.oceanmaze.mixin.StatusEffectInstanceAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeWetEffect extends StatusEffect {

	public SpongeWetEffect() {
		super(StatusEffectType.BENEFICIAL, 0x7777FF);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity.fallDistance > 20) {
			World w = entity.getEntityWorld();
			if (w.getDimension().isUltrawarm())
				return;
			BlockPos water = entity.getBlockPos().down();
			BlockPos look = entity.getBlockPos().down(2);
			BlockState wbs = w.getBlockState(water);
			BlockState lbs = w.getBlockState(look);
			if ((look.getY() <= 0 || lbs.isSolidBlock(w, look)) && wbs.isAir()) {
				w.setBlockState(water, Blocks.WATER.getDefaultState());
				entity.removeStatusEffect(this);
			}
		}
		if (entity.isOnFire() && entity.getStatusEffect(StatusEffects.FIRE_RESISTANCE) == null) {
			entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200));
			StatusEffectInstance eff = entity.getStatusEffect(OceanMaze.SPONGE_WET);
			((StatusEffectInstanceAccessor) eff).setDuration(Math.max(1, eff.getDuration() - 200));
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

}
