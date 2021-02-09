package mod.xinke.item;

import com.mojang.authlib.GameProfile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;

public class XKSteelSword extends SwordItem {

	public XKSteelSword(Settings settings) {
		super(XKSteelToolMaterial.INSTANCE, 3, -2.4f, settings);
	}

	public boolean postHit(ItemStack is, LivingEntity target, LivingEntity attacker) {
		if (target.getHealth() <= 0 && EnchantmentHelper.getLooting(attacker) >= Math.random() * 4 - 1) {
			if (target.getClass() == ZombieEntity.class)
				target.dropItem(Items.ZOMBIE_HEAD);
			if (target.getClass() == SkeletonEntity.class)
				target.dropItem(Items.SKELETON_SKULL);
			if(target.getClass()==CreeperEntity.class)
				target.dropItem(Items.CREEPER_HEAD);
			if (target.getClass() == WitherSkeletonEntity.class)
				target.dropItem(Items.WITHER_SKELETON_SKULL);
			if (target instanceof PlayerEntity) {
				ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
				GameProfile gameProfile = ((PlayerEntity) target).getGameProfile();
				stack.getOrCreateTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
				target.dropStack(stack);
			}
		}
		return super.postHit(is, target, attacker);
	}

}
