package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

@Mixin(PersistentProjectileEntity.class)
public interface PersistentProjectileEntityAccessor {

	@Accessor("punch")
	public int getPunch();

	@Invoker("onHit")
	public void invokeOnHit(LivingEntity targetle);

}
