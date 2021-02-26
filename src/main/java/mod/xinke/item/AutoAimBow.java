package mod.xinke.item;

import java.util.function.Predicate;

import mod.lcy0x1.util.math.AutoAim;
import mod.lcy0x1.util.math.AutoAim.ShootConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.world.World;

public class AutoAimBow extends BowItem implements XinkeEnergyItem {

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
			Entity entity = AutoAim.getEntity(config);
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
