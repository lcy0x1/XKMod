package mod.xinke.recipe;

import mod.lcy0x1.util.SerialClass;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class RecReg {

	public static final RecipeType<XKECRecipe> RT_XKEC = RecipeType.register("xinke:xkec");
	public static final RecipeSerializer<XKECRecipe> RS_XKEC = RecipeSerializer.register("xinke:xkec",
			new SerialClass.RecSerializer<>(XKECRecipe.class));

	public static final RecipeType<XKFillRecipe> RT_XKFILL = RecipeType.register("xinke:fill");
	public static final RecipeSerializer<XKFillRecipe> RS_XKFILL = RecipeSerializer.register("xinke:fill",
			new SerialClass.RecSerializer<>(XKFillRecipe.class));

}
