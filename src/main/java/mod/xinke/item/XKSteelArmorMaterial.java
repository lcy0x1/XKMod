package mod.xinke.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class XKSteelArmorMaterial implements ArmorMaterial {

	public static final XKSteelArmorMaterial INSTANCE = new XKSteelArmorMaterial();

	private static final int[] PROTECT = { 2, 5, 6, 2 };

	@Override
	public int getDurability(EquipmentSlot slot) {
		return 120;
	}

	@Override
	public int getEnchantability() {
		return 5;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}

	@Override
	public String getName() {
		return "xinke_steel";
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		return PROTECT[slot.getEntitySlotId()];
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(Items.BEDROCK);
	}

	@Override
	public float getToughness() {
		return 0;
	}

}
