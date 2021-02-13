package mod.xinke.recipe;

import mod.xinke.item.XinkeEnergyItem;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

@SerialClass
public class XKFillRecipe extends XKECRecipe {

	@SerialField
	public int energy;

	public XKFillRecipe(Identifier id) {
		super(id);
	}

	@Override
	public ItemStack craft(Inv inv) {
		ItemStack core = inv.getStack(0).copy();
		inv.clearAll();
		core.setDamage(0);
		XinkeEnergyItem.setMaxEnergy(core, energy);
		XinkeEnergyItem.setEnergy(core, energy);
		return core;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecReg.RS_XKFILL;
	}

	@Override
	public RecipeType<?> getType() {
		return RecReg.RT_XKFILL;
	}

}
