package mod.xinke.recipe;

import mod.xinke.util.SerialClass;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class RecReg {

	public static final RecipeType<XKECRecipe> RT_XKEC = RecipeType.register("xinke:xkec");
	public static final RecipeSerializer<XKECRecipe> RS_XKEC = RecipeSerializer.register("xinke:xkec",
			new SerialClass.RecSerializer<>(XKECRecipe.class));

}
