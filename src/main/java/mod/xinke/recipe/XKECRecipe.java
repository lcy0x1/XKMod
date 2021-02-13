package mod.xinke.recipe;

import java.util.List;

import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@SerialClass
public class XKECRecipe implements Recipe<XKECRecipe.Inv> {

	public static interface Inv extends Inventory {

		public void clearAll();

		public InvLayer[] getLayers();

	}

	public static class InvLayer {

		public ItemStack[] is;

		public InvLayer(List<ItemStack> cur) {
			is = cur.toArray(new ItemStack[0]);
		}

	}

	@SerialClass
	public static class RecLayer {

		@SerialField
		public Ingredient in;

		@SerialField
		public int count;

	}

	@SerialField
	public Ingredient core;

	@SerialField
	public RecLayer[] layers;

	@SerialField
	public ItemStack output;

	public final Identifier id;

	public XKECRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public ItemStack craft(Inv inv) {
		inv.clearAll();
		return output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecReg.RS_XKEC;
	}

	@Override
	public RecipeType<?> getType() {
		return RecReg.RT_XKEC;
	}

	@Override
	public boolean matches(Inv inv, World world) {
		if (!core.test(inv.getStack(0)))
			return false;
		InvLayer[] invl = inv.getLayers();
		int i = 0;
		for (InvLayer il : invl) {
			int count = 0;
			for (ItemStack isi : il.is)
				if (isi != ItemStack.EMPTY) {
					if (i == layers.length)
						return false;
					if (!layers[i].in.test(isi))
						return false;
					count++;
				}
			if (count == 0)
				continue;
			if (count != layers[i].count)
				return false;
			i++;
		}
		return i == layers.length;
	}

}
