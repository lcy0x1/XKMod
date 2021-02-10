package mod.xinke.item;

import mod.xinke.main.XinkeMod;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class XKSteelToolMaterial implements ToolMaterial {

	public static final XKSteelToolMaterial INSTANCE = new XKSteelToolMaterial();

	@Override
	public float getAttackDamage() {
		return 0;
	}

	@Override
	public int getDurability() {
		return 80;
	}

	@Override
	public int getEnchantability() {
		return 5;
	}

	@Override
	public int getMiningLevel() {
		return 1;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return 6f;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(XinkeMod.I_XKSTEEL_INGOT);
	}

}
