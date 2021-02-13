package mod.xinke.main.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.xinke.enchantment.LavaFrozingEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(at = @At("TAIL"), method = "applyMovementEffects(Lnet/minecraft/util/math/BlockPos;)V")
	public void applyMovementEffects(BlockPos pos, CallbackInfo info) {
		LivingEntity self = (LivingEntity) (Object) this;
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, self);
		if (i > 0) {
			LavaFrozingEnchantment.freezeLava(self, self.world, pos, i);
		}
	}

}
