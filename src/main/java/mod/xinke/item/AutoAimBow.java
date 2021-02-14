package mod.xinke.item;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;

import mod.xinke.util.Estimator;
import mod.xinke.util.Estimator.EstiResult;
import mod.xinke.util.Estimator.EstiType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class AutoAimBow extends BowItem implements XinkeEnergyItem {

	public static class ShootConfig {

		public ItemStack bow;
		public ItemStack ammo;
		public World world;
		public PlayerEntity player;
		public float pullProgress;
		public boolean omitConsume;

		public double g, k;
		public int r, t;
		public float velo;

		public void setData(double g, double k, int r, int t) {
			this.g = g;
			this.k = k;
			this.r = r;
			this.t = t;
		}

	}

	private static Entity getEntity(ShootConfig config) {
		config.velo = MathHelper.clamp(config.pullProgress, 0f, 1f) * 3;
		if (config.ammo.getItem() == Items.TNT)
			return getTNTEntity(config);
		return getProcessedPE(config);
	}

	private static PersistentProjectileEntity getPersistentProjectileEntity(ShootConfig config) {
		if (config.ammo.getItem() instanceof ArrowItem) {
			return ((ArrowItem) config.ammo.getItem()).createArrow(config.world, config.ammo, config.player);
		}
		if (config.ammo.getItem() == Items.TRIDENT) {
			return new TridentEntity(config.world, config.player, config.ammo);
		}
		return null;
	}

	private static ProjectileEntity getProcessedPE(ShootConfig config) {
		ProjectileEntity e = getProjectileEntity(config);
		EstiResult er = setAim(config.player, config.velo, config.r, e, config.g, config.k, config.t);
		if (er.getType() == EstiType.ZERO)
			e.setVelocity(er.getVec());
		else if (er.getType() == EstiType.FAIL)
			e.setProperties(config.player, config.player.pitch, config.player.yaw, 0, config.velo, 0);
		return e;
	}

	private static ProjectileEntity getProcessedPPE(ShootConfig config) {
		PersistentProjectileEntity entity = getPersistentProjectileEntity(config);
		if (config.pullProgress == 1.0F)
			entity.setCritical(true);
		int j = EnchantmentHelper.getLevel(Enchantments.POWER, config.bow);
		if (j > 0)
			entity.setDamage(entity.getDamage() + j * 0.5D + 0.5D);
		int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, config.bow);
		if (k > 0)
			entity.setPunch(k);
		if (EnchantmentHelper.getLevel(Enchantments.FLAME, config.bow) > 0)
			entity.setOnFireFor(100);
		if (config.omitConsume || config.player.abilities.creativeMode)
			entity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
		return entity;
	}

	private static ProjectileEntity getProjectileEntity(ShootConfig config) {
		config.setData(0.05, 0.01, 128, 120);
		if (config.ammo.getItem() instanceof ThrowablePotionItem) {
			PotionEntity p = new PotionEntity(config.world, config.player);
			p.setItem(config.ammo);
			return p;
		}
		config.setData(0.03, 0.01, 128, 120);
		if (config.ammo.getItem() == Items.ENDER_PEARL)
			return new EnderPearlEntity(config.world, config.player);
		if (config.ammo.getItem() == Items.SNOWBALL)
			return new SnowballEntity(config.world, config.player);
		config.setData(0.05, 0.01, 128, 120);
		return getProcessedPPE(config);
	}

	private static Vec3d getRayTerm(Vec3d pos, float pitch, float yaw, double reach) {
		float f2 = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
		float f5 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		return pos.add(f6 * reach, f5 * reach, f7 * reach);
	}

	private static Entity getTNTEntity(ShootConfig config) {
		TntEntity e = new TntEntity(config.world, config.player.getX(), config.player.getEyeY() - 0.1,
				config.player.getZ(), config.player);
		EstiResult er = setAim(config.player, config.velo, 128, e, 0.04, 0.02, 80);
		if (er.getType() == EstiType.ZERO) {
			e.setVelocity(er.getVec());
			e.setFuse((int) Math.round(er.getT()));
		} else if (er.getType() == EstiType.FAIL)
			setDire(config.player, config.velo, e);
		else
			return null;
		return e;
	}

	private static BlockHitResult rayTraceBlock(World worldIn, PlayerEntity player, double reach) {
		float f = player.pitch;
		float f1 = player.yaw;
		Vec3d vec3d = new Vec3d(player.getX(), player.getEyeY(), player.getZ());
		Vec3d vec3d1 = getRayTerm(vec3d, f, f1, reach);
		return worldIn.raycast(new RaycastContext(vec3d, vec3d1, RaycastContext.ShapeType.OUTLINE,
				RaycastContext.FluidHandling.NONE, player));
	}

	private static EntityHitResult rayTraceEntity(PlayerEntity player, double reach) {
		World world = player.world;
		Vec3d pos = new Vec3d(player.getX(), player.getEyeY(), player.getZ());
		Vec3d end = getRayTerm(pos, player.pitch, player.yaw, reach);
		Box box = new Box(pos, end).expand(1);
		double d0 = reach * reach;
		Entity entity = null;
		Vec3d vec3d = null;
		for (Entity e : world.getOtherEntities(player, box)) {
			Box aabb = e.getBoundingBox().expand(e.getTargetingMargin());
			Optional<Vec3d> optional = aabb.raycast(pos, end);
			if (aabb.contains(pos)) {
				if (d0 >= 0.0D) {
					entity = e;
					vec3d = optional.orElse(pos);
					d0 = 0.0D;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d1 = optional.get();
				double d1 = pos.squaredDistanceTo(vec3d1);
				if (d1 < d0 || d0 == 0.0D) {
					if (e.getRootVehicle() == player.getRootVehicle()) {
						if (d0 == 0.0D) {
							entity = e;
							vec3d = vec3d1;
						}
					} else {
						entity = e;
						vec3d = vec3d1;
						d0 = d1;
					}
				}
			}
		}
		return entity == null ? null : new EntityHitResult(entity, vec3d);
	}

	private static EstiResult setAim(PlayerEntity pl, double velo, double reach, Entity e, double g, double k,
			int maxt) {
		EntityHitResult ertr = rayTraceEntity(pl, reach);
		if (ertr != null && ertr.getType() == EntityHitResult.Type.ENTITY) {
			if (ertr.getPos().distanceTo(pl.getPos()) < velo)
				return EstiType.CLOSE;
			LogManager.getLogger().info("targeting entity: " + ertr.getEntity().toString());
			Vec3d mot = ertr.getEntity().getVelocity();
			Vec3d tar = ertr.getPos();
			Vec3d pos = e.getPos();
			Estimator.EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, mot).getAnswer();
			LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
			if (er.getType() == EstiType.ZERO)
				return er;
		}
		BlockHitResult brtr = rayTraceBlock(pl.world, pl, reach);
		if (brtr != null && brtr.getType() == BlockHitResult.Type.BLOCK) {
			if (brtr.getPos().distanceTo(pl.getPos()) < velo)
				return EstiType.CLOSE;
			LogManager.getLogger().info("targeting block: " + brtr.getPos());
			Vec3d tar = brtr.getPos();
			Vec3d pos = e.getPos();
			Estimator.EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, Vec3d.ZERO).getAnswer();
			LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
			if (er.getType() == EstiType.ZERO)
				return er;
		}
		return EstiType.FAIL;
	}

	private static void setDire(PlayerEntity pl, float velo, Entity ent) {
		float yaw = pl.yaw;
		float pitch = pl.pitch;
		float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin(pitch * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		Vec3d vec3d = new Vec3d(f, f1, f2).normalize().multiply(velo);
		ent.setVelocity(vec3d);
		float f3 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
		ent.yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180F / (float) Math.PI));
		ent.pitch = (float) (MathHelper.atan2(vec3d.y, f3) * (180F / (float) Math.PI));
		ent.prevYaw = ent.yaw;
		ent.prevPitch = ent.pitch;
	}

	public AutoAimBow(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return (is) -> is.getItem().isIn(ItemTags.ARROWS) || is.getItem() == Items.TNT || is.getItem() == Items.TRIDENT
				|| is.getItem() instanceof ThrowablePotionItem || is.getItem() == Items.SNOWBALL
				|| is.getItem() == Items.ENDER_PEARL;
	}

	@Override
	public int getRange() {
		return 64;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity))
			return;
		ShootConfig config = new ShootConfig();
		config.world = world;
		config.bow = stack;
		config.player = (PlayerEntity) user;
		boolean omitArrow = config.player.abilities.creativeMode
				|| EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
		config.ammo = config.player.getArrowType(stack);
		if (config.ammo.isEmpty() && !omitArrow)
			return;
		if (config.ammo.isEmpty())
			config.ammo = new ItemStack(Items.ARROW);
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		config.pullProgress = getPullProgress(i);
		if (config.pullProgress < 0.1)
			return;
		config.omitConsume = omitArrow && config.ammo.getItem() == Items.ARROW;
		if (!world.isClient) {
			Entity entity = getEntity(config);
			config.bow.damage(1, config.player, p -> p.sendToolBreakStatus(config.player.getActiveHand()));
			world.spawnEntity(entity);
		}
		world.playSound((PlayerEntity) null, config.player.getX(), config.player.getY(), config.player.getZ(),
				SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
				1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + config.pullProgress * 0.5F);
		if (!config.omitConsume && !config.player.abilities.creativeMode) {
			config.ammo.decrement(1);
			if (config.ammo.isEmpty())
				config.player.inventory.removeOne(config.ammo);
		}
		config.player.incrementStat(Stats.USED.getOrCreateStat(this));
	}

}
