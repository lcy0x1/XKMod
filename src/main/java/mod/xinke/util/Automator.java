package mod.xinke.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;

/**
 * Capable of handing primitive types, array, BlockPos, ItemStack, inheritance
 * <br>
 * Not capable of handing collections
 */
public class Automator {

	private static class ClassHandler<R extends Tag, T> {

		private final Function<Tag, ?> fromTag;
		private final Function<Object, Tag> toTag;

		@SuppressWarnings("unchecked")
		private ClassHandler(Class<T> cls, Function<R, T> ft, Function<T, Tag> tt, Class<?>... alt) {
			fromTag = (Function<Tag, ?>) ft;
			toTag = (Function<Object, Tag>) tt;
			MAP.put(cls, this);
			for (Class<?> c : alt)
				MAP.put(c, this);
		}

	}

	private static final Map<Class<?>, ClassHandler<?, ?>> MAP = new HashMap<>();

	static {
		new ClassHandler<LongTag, Long>(Long.class, tag -> tag.getLong(), LongTag::of, long.class);
		new ClassHandler<IntTag, Integer>(Integer.class, tag -> tag.getInt(), IntTag::of, int.class);
		new ClassHandler<ShortTag, Short>(Short.class, tag -> tag.getShort(), ShortTag::of, short.class);
		new ClassHandler<ByteTag, Byte>(Byte.class, tag -> tag.getByte(), ByteTag::of, byte.class);
		new ClassHandler<ByteTag, Boolean>(Boolean.class, tag -> tag.getByte() != 0, ByteTag::of, boolean.class);
		new ClassHandler<FloatTag, Float>(Float.class, tag -> tag.getFloat(), FloatTag::of, float.class);
		new ClassHandler<DoubleTag, Double>(Double.class, tag -> tag.getDouble(), DoubleTag::of, double.class);
		new ClassHandler<LongArrayTag, long[]>(long[].class, LongArrayTag::getLongArray, LongArrayTag::new);
		new ClassHandler<IntArrayTag, int[]>(int[].class, IntArrayTag::getIntArray, IntArrayTag::new);
		new ClassHandler<ByteArrayTag, byte[]>(byte[].class, ByteArrayTag::getByteArray, ByteArrayTag::new);
		new ClassHandler<StringTag, String>(String.class, Tag::asString, StringTag::of);
		new ClassHandler<CompoundTag, ItemStack>(ItemStack.class, tag -> ItemStack.fromTag(tag),
				is -> is.toTag(new CompoundTag()));
		new ClassHandler<CompoundTag, BlockPos>(BlockPos.class, tag -> {
			CompoundTag nbt = tag;
			return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
		}, obj -> {
			CompoundTag tag = new CompoundTag();
			tag.putInt("x", obj.getX());
			tag.putInt("y", obj.getY());
			tag.putInt("z", obj.getZ());
			return tag;
		});
		new ClassHandler<IntArrayTag, UUID>(UUID.class, NbtHelper::toUuid, NbtHelper::fromUuid);
	}

	public static Object fromTag(CompoundTag tag, Class<?> cls, Object obj, Predicate<SerialField> pred)
			throws Exception {
		if (obj == null)
			obj = cls.newInstance();
		while (cls.getAnnotation(SerialClass.class) != null) {
			for (Field f : cls.getDeclaredFields()) {
				if (!pred.test(f.getAnnotation(SerialField.class)))
					continue;
				f.set(obj, fromTagRaw(tag.get(f.getName()), f.getType(), pred));
			}
			cls = cls.getSuperclass();
		}
		return obj;
	}

	public static Object fromTagRaw(Tag tag, Class<?> cls, Predicate<SerialField> pred) throws Exception {
		if (tag == null)
			if (cls == ItemStack.class)
				return ItemStack.EMPTY;
			else
				return null;
		if (MAP.containsKey(cls))
			return MAP.get(cls).fromTag.apply(tag);
		if (cls.isArray()) {
			ListTag list = (ListTag) tag;
			int n = list.size();
			Class<?> com = cls.getComponentType();
			Object ans = Array.newInstance(com, n);
			for (int i = 0; i < n; i++) {
				Array.set(ans, i, fromTagRaw(list.get(i), com, pred));
			}
			return ans;
		}
		if (cls.getAnnotation(SerialClass.class) != null)
			return fromTag((CompoundTag) tag, cls, null, pred);
		throw new Exception("unsupported class " + cls);
	}

	public static CompoundTag toTag(CompoundTag tag, Class<?> cls, Object obj, Predicate<SerialField> pred)
			throws Exception {
		if (obj == null)
			return tag;
		while (cls.getAnnotation(SerialClass.class) != null) {
			for (Field f : cls.getDeclaredFields()) {
				if (!pred.test(f.getAnnotation(SerialField.class)))
					continue;
				if (f.get(obj) != null)
					tag.put(f.getName(), toTagRaw(f.getType(), f.get(obj), pred));
			}
			cls = cls.getSuperclass();
		}
		return tag;
	}

	public static Tag toTagRaw(Class<?> cls, Object obj, Predicate<SerialField> pred) throws Exception {
		if (MAP.containsKey(cls))
			return MAP.get(cls).toTag.apply(obj);
		if (cls.isArray()) {
			ListTag list = new ListTag();
			int n = Array.getLength(obj);
			Class<?> com = cls.getComponentType();
			for (int i = 0; i < n; i++) {
				list.add(toTagRaw(com, Array.get(obj, i), pred));
			}
			return list;
		}
		if (cls.getAnnotation(SerialClass.class) != null)
			return toTag(new CompoundTag(), cls, obj, pred);
		throw new Exception("unsupported class " + cls);
	}

}