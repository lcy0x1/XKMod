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

public class XKSteelSword extends SwordItem implements XinkeEnergyItem {

	public XKSteelSword(Settings settings) {
		super(XKSteelToolMaterial.INSTANCE, 3, -2.4f, settings);
	}

	@Override
	public boolean postHit(ItemStack is, LivingEntity target, LivingEntity attacker) {
		if (XinkeEnergyItem.getEnergy(is) >= 10 && target.getHealth() <= 0) {
			if (EnchantmentHelper.getLooting(attacker) + 5 >= Math.random() * 64) {
				if (target.getClass() == ZombieEntity.class)
					target.dropItem(Items.ZOMBIE_HEAD);
				if (target.getClass() == SkeletonEntity.class)
					target.dropItem(Items.SKELETON_SKULL);
				if (target.getClass() == CreeperEntity.class)
					target.dropItem(Items.CREEPER_HEAD);
				if (target.getClass() == WitherSkeletonEntity.class)
					target.dropItem(Items.WITHER_SKELETON_SKULL);
				if (target instanceof PlayerEntity) {
					ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
					GameProfile gameProfile = ((PlayerEntity) target).getGameProfile();
					stack.getOrCreateTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
					target.dropStack(stack);
				}
				XinkeEnergyItem.setEnergy(is, XinkeEnergyItem.getEnergy(is) - 9);
			} else
				XinkeEnergyItem.setEnergy(is, XinkeEnergyItem.getEnergy(is) - 3);
		} else
			XinkeEnergyItem.setEnergy(is, Math.max(0, XinkeEnergyItem.getEnergy(is) - 1));
		return super.postHit(is, target, attacker);
	}

}
