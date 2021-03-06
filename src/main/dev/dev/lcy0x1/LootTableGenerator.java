package dev.lcy0x1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dev.lcy0x1.LootTableGenerator.LootTable.*;
import dev.lcy0x1.json.JsonClass;
import dev.lcy0x1.json.JsonEncoder;
import dev.lcy0x1.json.JsonField;

public class LootTableGenerator {

	@JsonClass(noTag = JsonClass.NoTag.LOAD)
	public static class LootTable {

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class Count {

			public int min, max;
			public String type = "minecraft:uniform";

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static abstract class Function {

			@JsonClass(noTag = JsonClass.NoTag.LOAD)
			public static class EnchantLevel extends Function {

				public Count levels;
				public boolean treasure;

				public EnchantLevel(int min, int max) {
					super("minecraft:enchant_with_levels");
					levels = new Count();
					levels.min = min;
					levels.max = max;
					levels.type = "minecraft:uniform";
				}

			}

			@JsonClass(noTag = JsonClass.NoTag.LOAD)
			public static class SetCount extends Function {

				public Count count;

				public SetCount(int min, int max) {
					super("minecraft:set_count");
					count = new Count();
					count.min = min;
					count.max = max;
					count.type = "minecraft:uniform";
				}

			}

			public String function;

			public Function(String func) {
				this.function = func;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class FunctionPoolEntry extends PoolEntry {

			@JsonField(generic = Function.class)
			public List<Function> functions = new ArrayList<>();

			public FunctionPoolEntry(int w, String item) {
				super(w, item);
			}

			public FunctionPoolEntry add(Function f) {
				functions.add(f);
				return this;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static abstract class Pool {

			@JsonField(generic = PoolEntry.class)
			public List<PoolEntry> entries = new ArrayList<>();

			public Pool add(PoolEntry ent) {
				entries.add(ent);
				return this;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class PoolEntry {

			public String type = "minecraft:item";
			public int weight = 0;
			public String name;

			public PoolEntry(int w, String item) {
				this.weight = w;
				this.name = item;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class RandomPool extends Pool {

			public Count rolls;

			public RandomPool(int min, int max) {
				rolls = new Count();
				rolls.min = min;
				rolls.max = max;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class StaticPool extends Pool {

			public int rolls;

			public StaticPool(int c) {
				rolls = c;
			}

		}

		@JsonClass(noTag = JsonClass.NoTag.LOAD)
		public static class StringFuncPoolEntry extends PoolEntry {

			@JsonField(generic = String.class)
			public List<String> functions = new ArrayList<>();

			public StringFuncPoolEntry(int w, String item, String func) {
				super(w, item);
				this.add(func);
			}

			public StringFuncPoolEntry add(String f) {
				functions.add(f);
				return this;
			}

		}

		public String type = "minecraft:chest";

		@JsonField(generic = Pool.class)
		public List<Pool> pools = new ArrayList<>();

	}

	public static String[] equips = { "helmet", "chestplate", "leggings", "boots" };
	public static String[] metals = { "water_metal_", "ocean_metal_", "deep_ocean_metal_" };

	public static void main(String[] args) throws IOException {
		{
			LootTable lv0 = new LootTable();
			lv0.pools.add(new RandomPool(5, 10)// break
					.add(new PoolEntry(5, "minecraft:trident"))// break
					.add(item("minecraft:wet_sponge", 5, 1, 2))// break
					.add(item("minecraft:golden_apple", 5, 1, 4))// break
					.add(item("minecraft:prismarine_shard", 10, 1, 8))// break
					.add(item("minecraft:prismarine_crystals", 10, 1, 4))// break
					.add(item("minecraft:iron_ingot", 10, 1, 8))// break
					.add(item("minecraft:diamond", 10, 1, 1))// break
					.add(item("minecraft:nautilus_shell", 10, 1, 2))// break
					.add(item("minecraft:scute", 10, 1, 2))// break
					.add(item("oceanmaze:water_metal_ingot", 10, 1, 2))// break
					.add(item("oceanmaze:ocean_metal_nugget", 10, 1, 4))// break
					.add(item("oceanmaze:deep_ocean_metal_nugget", 10, 1, 1))// break
			);
			write(lv0, "top.json");
		}

		{
			LootTable lv1 = new LootTable();
			lv1.pools.add(new StaticPool(1).add(new PoolEntry(10, "minecraft:heart_of_the_sea")));
			Function.EnchantLevel enc = new Function.EnchantLevel(5, 10);
			lv1.pools.add(injectEquips(new StaticPool(1), enc, 0, 10));
			lv1.pools.add(new RandomPool(5, 10)// break
					.add(new PoolEntry(1, "minecraft:enchanted_golden_apple"))// break
					.add(new PoolEntry(4, "minecraft:trident"))// break
					.add(item("minecraft:wet_sponge", 5, 1, 2))// break
					.add(item("minecraft:golden_apple", 5, 1, 4))// break
					.add(item("minecraft:prismarine_shard", 10, 1, 32))// break
					.add(item("minecraft:prismarine_crystals", 10, 1, 16))// break
					.add(item("minecraft:iron_ingot", 10, 1, 32))// break
					.add(item("minecraft:diamond", 10, 1, 4))// break
					.add(item("minecraft:nautilus_shell", 10, 1, 8))// break
					.add(item("minecraft:scute", 10, 1, 8))// break
					.add(item("oceanmaze:water_metal_ingot", 10, 1, 8))// break
					.add(item("oceanmaze:ocean_metal_nugget", 10, 1, 16))// break
					.add(item("oceanmaze:deep_ocean_metal_nugget", 10, 1, 4))// break
			);
			write(lv1, "ocean_maze_end_lv1.json");
		}

		{
			LootTable lv2 = new LootTable();
			lv2.pools.add(new StaticPool(1).add(new PoolEntry(10, "minecraft:heart_of_the_sea")));
			Function.EnchantLevel enc = new Function.EnchantLevel(10, 20);
			lv2.pools.add(injectEquips(new StaticPool(1), enc, 1, 10));
			lv2.pools.add(new RandomPool(10, 20)// break
					.add(new PoolEntry(1, "minecraft:enchanted_golden_apple"))// break
					.add(new PoolEntry(9, "minecraft:trident"))// break
					.add(item("minecraft:wet_sponge", 5, 1, 4))// break
					.add(item("minecraft:golden_apple", 5, 1, 4))// break
					.add(item("minecraft:prismarine_shard", 5, 1, 32))// break
					.add(item("minecraft:prismarine_crystals", 5, 1, 16))// break
					.add(item("minecraft:iron_ingot", 5, 1, 32))// break
					.add(item("minecraft:diamond", 10, 1, 6))// break
					.add(item("minecraft:nautilus_shell", 10, 1, 16))// break
					.add(item("minecraft:scute", 10, 1, 16))// break
					.add(item("oceanmaze:water_metal_block", 5, 1, 4))// break
					.add(item("oceanmaze:ocean_metal_ingot", 10, 1, 4))// break
					.add(item("oceanmaze:deep_ocean_metal_nugget", 10, 1, 8))// break
			);
			write(lv2, "ocean_maze_end_lv2.json");
		}

		{
			LootTable lv3 = new LootTable();
			lv3.pools.add(new StaticPool(1).add(new PoolEntry(10, "minecraft:heart_of_the_sea")));
			Function.EnchantLevel enc = new Function.EnchantLevel(20, 40);
			lv3.pools.add(injectEquips(new StaticPool(1), enc, 2, 10));
			lv3.pools.add(new RandomPool(15, 25)// break
					.add(new PoolEntry(3, "minecraft:enchanted_golden_apple"))// break
					.add(new PoolEntry(7, "minecraft:trident"))// break
					.add(item("minecraft:wet_sponge", 5, 1, 8))// break
					.add(item("minecraft:diamond", 10, 1, 8))// break
					.add(item("oceanmaze:water_metal_block", 5, 1, 8))// break
					.add(item("oceanmaze:ocean_metal_ingot", 5, 1, 16))// break
					.add(item("oceanmaze:deep_ocean_metal_nugget", 10, 1, 16))// break
			);
			write(lv3, "ocean_maze_end_lv3.json");
		}
	}

	private static Pool injectEquips(Pool pool, Function.EnchantLevel enc, int lv, int w) {
		for (int i = 0; i < 4; i++) {
			pool.add(new FunctionPoolEntry(w, "oceanmaze:" + metals[lv] + equips[i]).add(enc));
		}
		return pool;
	}

	private static FunctionPoolEntry item(String item, int w, int min, int max) {
		return new FunctionPoolEntry(w, item).add(new Function.SetCount(min, max));
	}

	private static void write(LootTable table, String path) throws IOException {
		File f = new File("./resources/oceanmaze/DATA/@loot_tables/@chests/" + path);
		ResourceManager.check(f);
		PrintStream ps = new PrintStream(f);
		ps.println(JsonEncoder.encode(table).toString());
		ps.close();
	}

}
