package dev.lcy0x1.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FieldOrder implements Comparable<FieldOrder> {

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Order {
		int value();
	}

	private static final Map<Class<?>, Field[]> MAP = new HashMap<>();

	public static Field[] getDeclaredFields(Class<?> cls) {
		if (MAP.containsKey(cls))
			return MAP.get(cls);
		Field[] fs = cls.getDeclaredFields();
		FieldOrder[] fos = new FieldOrder[fs.length];
		for (int i = 0; i < fs.length; i++)
			fos[i] = new FieldOrder(fs[i]);
		Arrays.sort(fos);
		for (int i = 0; i < fs.length; i++)
			fs[i] = fos[i].field;
		MAP.put(cls, fs);
		return fs;
	}

	private final Field field;

	private final int order;

	private FieldOrder(Field f) {
		field = f;
		Order ord = f.getAnnotation(Order.class);
		order = ord == null ? Integer.MAX_VALUE : ord.value();
	}

	@Override
	public int compareTo(FieldOrder o) {
		return Integer.compare(order, o.order);
	}

}
