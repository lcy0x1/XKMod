package mod.xinke.item;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class XKSteelArmor extends ArmorItem {

	private static final int TIME_REF = 300, TIME = 500;

	private static void addEffect(ItemStack stack, LivingEntity e, int lv, StatusEffect eff) {
		Map<StatusEffect, StatusEffectInstance> map = e.getActiveStatusEffects();
		StatusEffectInstance ins = map.get(eff);
		if (ins != null) {
			if (ins.getAmplifier() > lv)
				return;
			if (ins.getDuration() > TIME_REF)
				return;
		}
		if (e.addStatusEffect(new StatusEffectInstance(eff, TIME, lv)))
			stack.setDamage(stack.getDamage() + 1);
	}

	private static boolean isWearing(ItemStack stack, Entity entity) {
		for (ItemStack is : entity.getArmorItems())
			if (is == stack)
				return true;
		return false;
	}

	public XKSteelArmor(EquipmentSlot slot, Settings settings) {
		super(XKSteelArmorMaterial.INSTANCE, slot, settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world.isClient())
			return;
		if (!(entity instanceof LivingEntity))
			return;
		if (!isWearing(stack, entity))
			return;
		if (stack.getDamage() >= stack.getMaxDamage() * 0.75)
			return;
		if (stack.getItem() == this && this.getSlotType() == EquipmentSlot.HEAD)
			addEffect(stack, (LivingEntity) entity, 1, StatusEffects.NIGHT_VISION);
		if (stack.getItem() == this && this.getSlotType() == EquipmentSlot.CHEST)
			addEffect(stack, (LivingEntity) entity, 1, StatusEffects.RESISTANCE);
		if (stack.getItem() == this && this.getSlotType() == EquipmentSlot.LEGS)
			addEffect(stack, (LivingEntity) entity, 1, StatusEffects.SPEED);
		if (stack.getItem() == this && this.getSlotType() == EquipmentSlot.FEET)
			addEffect(stack, (LivingEntity) entity, 1, StatusEffects.JUMP_BOOST);
	}

}
