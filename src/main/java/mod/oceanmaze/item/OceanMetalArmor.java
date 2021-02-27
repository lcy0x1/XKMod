package mod.oceanmaze.item;

import mod.oceanmaze.main.OceanMaze;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OceanMetalArmor extends ArmorItem {

	public OceanMetalArmor(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world.isClient())
			return;
		if (!(entity instanceof LivingEntity))
			return;
		LivingEntity le = (LivingEntity) entity;
		for (ItemStack is : le.getArmorItems()) {
			if (is.getItem() == this)
				applyEffects(le);
			else if (is.getItem() instanceof OceanMetalArmor)
				return;
		}
	}

	private void applyEffects(LivingEntity le) {

		boolean inWater = le.isSubmergedInWater();
		int breath = 0;
		int wet = 0;
		boolean vi = false;
		int hs = 0;
		int res = 0;
		int deep = 0;
		int enc = 0;

		ItemStack is = le.getEquippedStack(EquipmentSlot.HEAD);
		if (is.getItem() instanceof OceanMetalArmor) {
			OMArmorMat am = (OMArmorMat) ((OceanMetalArmor) is.getItem()).getMaterial();
			if (!inWater)
				breath = am == OMArmorMat.WATER ? 200 : am == OMArmorMat.OCEAN ? 600 : 1800;
			else {
				vi = am != OMArmorMat.WATER;
				if (am == OMArmorMat.DEEP) {
					hs++;
					wet += 600;
					deep += 5;
				}
			}
		}
		is = le.getEquippedStack(EquipmentSlot.CHEST);
		enc += EnchantmentHelper.getLevel(OceanMaze.SPONGE_PROT, is);
		if ((is.getItem() instanceof OceanMetalArmor) && inWater) {
			OMArmorMat am = (OMArmorMat) ((OceanMetalArmor) is.getItem()).getMaterial();
			res += am == OMArmorMat.WATER ? 1 : am == OMArmorMat.OCEAN ? 2 : 3;
			wet += am == OMArmorMat.WATER ? 0 : 600;
			if (am == OMArmorMat.DEEP)
				deep += 7;
		}
		is = le.getEquippedStack(EquipmentSlot.LEGS);
		enc += EnchantmentHelper.getLevel(OceanMaze.SPONGE_PROT, is);
		enc += EnchantmentHelper.getLevel(OceanMaze.SPONGE_PROT, is);
		if ((is.getItem() instanceof OceanMetalArmor) && inWater) {
			OMArmorMat am = (OMArmorMat) ((OceanMetalArmor) is.getItem()).getMaterial();
			res += am == OMArmorMat.WATER ? 1 : am == OMArmorMat.OCEAN ? 2 : 3;
			if (am == OMArmorMat.DEEP) {
				wet += 1200;
				deep += 6;
			}
		}
		is = le.getEquippedStack(EquipmentSlot.FEET);
		enc += EnchantmentHelper.getLevel(OceanMaze.SPONGE_PROT, is);
		if ((is.getItem() instanceof OceanMetalArmor) && inWater) {
			OMArmorMat am = (OMArmorMat) ((OceanMetalArmor) is.getItem()).getMaterial();
			if (am == OMArmorMat.DEEP) {
				wet += 1200;
				deep += 4;
				res++;
			}
		}

		wet = (int) (wet * (1 + enc / 4.0));

		if (vi)
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 215));
		if (breath > 0)
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, breath + 14));
		if (res > 0)
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 213, (res - 1) / 2));
		if (deep >= 10)
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 212));
		if (hs > 0)
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 211, hs - 1));
		if (wet > 0)
			le.addStatusEffect(new StatusEffectInstance(OceanMaze.SPONGE_WET, wet + 10));
	}

}
