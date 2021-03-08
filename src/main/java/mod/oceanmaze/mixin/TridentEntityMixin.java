package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

	@Inject(at = @At("HEAD"), cancellable = true, method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V")
	public void onEntityHit(EntityHitResult entityHitResult, CallbackInfo info) {
		TridentEntity self = (TridentEntity) (Object) this;
		TridentEntityAccessor tself = (TridentEntityAccessor) this;
		PersistentProjectileEntityAccessor pself = (PersistentProjectileEntityAccessor) this;
		Entity target = entityHitResult.getEntity();
		double f = self.getDamage();
		if (f > 7.5f) {
			if (target instanceof LivingEntity) {
				LivingEntity targetle = (LivingEntity) target;
				f += EnchantmentHelper.getAttackDamage(tself.getTridentStack(), targetle.getGroup());
			}
			f *= MathHelper.clamp(self.getVelocity().length() / 2, 1, 3);

			Entity owner = self.getOwner();
			DamageSource damageSource = DamageSource.trident(self, (Entity) (owner == null ? self : owner));
			tself.setDealtDamage(true);
			SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
			if (target.damage(damageSource, (float) f)) {
				if (target.getType() == EntityType.ENDERMAN)
					return;
				if (target instanceof LivingEntity) {
					LivingEntity targetle = (LivingEntity) target;
					if (owner instanceof LivingEntity) {
						EnchantmentHelper.onUserDamaged(targetle, owner);
						EnchantmentHelper.onTargetDamaged((LivingEntity) owner, targetle);
					}
					if (pself.getPunch() > 0) {
						Vec3d vec3d = self.getVelocity().multiply(1.0D, 0.0D, 1.0D).normalize()
								.multiply((double) pself.getPunch() * 0.6D);
						if (vec3d.lengthSquared() > 0.0D) {
							targetle.addVelocity(vec3d.x, 0.1D, vec3d.z);
						}
					}
					pself.invokeOnHit(targetle);
				}
			}

			self.setVelocity(self.getVelocity().multiply(-0.01D, -0.1D, -0.01D));
			float g = 1.0F;
			if (self.world instanceof ServerWorld && self.world.isThundering()
					&& EnchantmentHelper.hasChanneling(tself.getTridentStack())) {
				BlockPos blockPos = target.getBlockPos();
				if (self.world.isSkyVisible(blockPos)) {
					LightningEntity lightningEntity = (LightningEntity) EntityType.LIGHTNING_BOLT.create(self.world);
					lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
					lightningEntity
							.setChanneler(owner instanceof ServerPlayerEntity ? (ServerPlayerEntity) owner : null);
					self.world.spawnEntity(lightningEntity);
					soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
					g = 5.0F;
				}
			}
			self.playSound(soundEvent, g, 1.0F);
			info.cancel();
		}
	}

}
