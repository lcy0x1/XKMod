package mod.lcy0x1.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.google.gson.JsonObject;

import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface SerialClass {

	public static class RecSerializer<R extends Recipe<I>, I extends Inventory> implements RecipeSerializer<R> {

		public final Class<R> cls;

		public RecSerializer(Class<R> cls) {
			this.cls = cls;
		}

		@Override
		public R read(Identifier id, JsonObject json) {
			return Serializer.from(json, cls,
					ExceptionHandler.get(() -> cls.getConstructor(Identifier.class).newInstance(id)));
		}

		@Override
		public R read(Identifier id, PacketByteBuf buf) {
			return Serializer.from(buf, cls,
					ExceptionHandler.get(() -> cls.getConstructor(Identifier.class).newInstance(id)));
		}

		@Override
		public void write(PacketByteBuf buf, R recipe) {
			Serializer.to(buf, recipe);
		}

	}

	@Documented
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface SerialField {

		boolean toClient() default false;

	}

}
