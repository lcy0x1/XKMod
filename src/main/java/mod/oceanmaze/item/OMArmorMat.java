package mod.oceanmaze.item;

import mod.oceanmaze.main.OceanMaze;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class OMArmorMat implements ArmorMaterial {

	public static final OMArmorMat WATER = new OMArmorMat("water_metal",
			OceanMaze.I_WATER_METAL_INGOT, 0);
	public static final OMArmorMat OCEAN = new OMArmorMat("ocean_metal",
			OceanMaze.I_OCEAN_METAL_INGOT, 1);
	public static final OMArmorMat DEEP = new OMArmorMat("deep_ocean_metal",
			OceanMaze.I_DEEP_OCEAN_METAL_INGOT, 2);

	private static final int[] PROT = { 2, 4, 4, 2 };
	private static final int[] DUR = new int[] { 13, 15, 16, 11 };

	private final String name;
	private final Item repair;
	private final int lv;

	public OMArmorMat(String name, Item repair, int lv) {
		this.name = name;
		this.repair = repair;
		this.lv = lv;
	}

	@Override
	public int getDurability(EquipmentSlot slot) {
		return DUR[slot.getEntitySlotId()] * (15 + lv * 3);
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		return PROT[slot.getEntitySlotId()] + lv;
	}

	@Override
	public int getEnchantability() {
		return 15;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(repair);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getToughness() {
		return lv * 2;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}

}
