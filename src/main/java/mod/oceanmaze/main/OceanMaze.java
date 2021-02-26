package mod.oceanmaze.main;

import java.util.function.Supplier;

import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.block.MazeBlock;
import mod.oceanmaze.item.OceanMetalArmor;
import mod.oceanmaze.item.OceanMetalArmorMaterial;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OceanMaze implements ModInitializer {

	public static final String MODID = "oceanmaze";

	public static final ItemGroup IG_GENERAL = FabricItemGroupBuilder.build(new Identifier(MODID, "general"),
			itemGroupIcon("general"));

	public static final BlockProp BP_OM = new BlockProp(
			FabricBlockSettings.of(Material.STONE).dropsNothing().ticksRandomly(), 20, 100);
	public static final FabricBlockSettings BP_METAL = FabricBlockSettings.copyOf(Blocks.IRON_BLOCK);

	
	public static final Block B_OMC_CORE = new MazeBlock(BP_OM, MazeBlock.NEIGHBOR, MazeBlock.HOR_DIRE_STATE, MazeBlock.FLOOR_PROT);
	public static final Block B_OMC_WALL = new MazeBlock(BP_OM, MazeBlock.NEIGHBOR, MazeBlock.HOR_DIRE_STATE);
	public static final Block B_OMC_FLOOR = new MazeBlock(BP_OM, MazeBlock.NEIGHBOR, MazeBlock.FLOOR_PROT, MazeBlock.FATIGUE);
	public static final Block B_OMC_SPAWNER = new MazeBlock(BP_OM, MazeBlock.SPAWNER, MazeBlock.FATIGUE);
	public static final Block B_OMO_WALL = new MazeBlock(BP_OM, MazeBlock.NEIGHBOR, MazeBlock.ALL_DIRE_STATE);

	public static final BlockItem BI_OMC_CORE = toBI(B_OMC_CORE);
	public static final BlockItem BI_OMC_WALL = toBI(B_OMC_WALL);
	public static final BlockItem BI_OMC_FLOOR = toBI(B_OMC_FLOOR);
	public static final BlockItem BI_OMC_SPAWNER = toBI(B_OMC_SPAWNER);
	public static final BlockItem BI_OMO_WALL = toBI(B_OMO_WALL);
	
	public static final Block B_WATER_METAL_BLOCK = new Block(BP_METAL);
	public static final Block B_OCEAN_METAL_BLOCK = new Block(BP_METAL);
	public static final Block B_DEEP_OCEAN_METAL_BLOCK = new Block(BP_METAL);
	public static final BlockItem BI_WATER_METAL_BLOCK = toBI(B_WATER_METAL_BLOCK);
	public static final BlockItem BI_OCEAN_METAL_BLOCK = toBI(B_OCEAN_METAL_BLOCK);
	public static final BlockItem BI_DEEP_OCEAN_METAL_BLOCK = toBI(B_DEEP_OCEAN_METAL_BLOCK);
	public static final Item I_WATER_METAL_INGOT = new Item(newFIS());
	public static final Item I_OCEAN_METAL_INGOT = new Item(newFIS());
	public static final Item I_DEEP_OCEAN_METAL_INGOT = new Item(newFIS());
	public static final Item I_WATER_METAL_NUGGET = new Item(newFIS());
	public static final Item I_OCEAN_METAL_NUGGET = new Item(newFIS());
	public static final Item I_DEEP_OCEAN_METAL_NUGGET = new Item(newFIS());
	
	public static final Item I_WATER_METAL_HELMET = new OceanMetalArmor(OceanMetalArmorMaterial.AM_WATER_METAL, EquipmentSlot.HEAD, newFIS());
	public static final Item I_WATER_METAL_CHESTPLATE = new OceanMetalArmor(OceanMetalArmorMaterial.AM_WATER_METAL, EquipmentSlot.CHEST, newFIS());
	public static final Item I_WATER_METAL_LEGGINGS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_WATER_METAL, EquipmentSlot.LEGS, newFIS());
	public static final Item I_WATER_METAL_BOOTS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_WATER_METAL, EquipmentSlot.FEET, newFIS());
	public static final Item I_OCEAN_METAL_HELMET = new OceanMetalArmor(OceanMetalArmorMaterial.AM_OCEAN_METAL, EquipmentSlot.HEAD, newFIS());
	public static final Item I_OCEAN_METAL_CHESTPLATE = new OceanMetalArmor(OceanMetalArmorMaterial.AM_OCEAN_METAL, EquipmentSlot.CHEST, newFIS());
	public static final Item I_OCEAN_METAL_LEGGINGS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_OCEAN_METAL, EquipmentSlot.LEGS, newFIS());
	public static final Item I_OCEAN_METAL_BOOTS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_OCEAN_METAL, EquipmentSlot.FEET, newFIS());
	public static final Item I_DEEP_OCEAN_METAL_HELMET = new OceanMetalArmor(OceanMetalArmorMaterial.AM_DEEP_OCEAN_METAL, EquipmentSlot.HEAD, newFIS());
	public static final Item I_DEEP_OCEAN_METAL_CHESTPLATE = new OceanMetalArmor(OceanMetalArmorMaterial.AM_DEEP_OCEAN_METAL, EquipmentSlot.CHEST, newFIS());
	public static final Item I_DEEP_OCEAN_METAL_LEGGINGS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_DEEP_OCEAN_METAL, EquipmentSlot.LEGS, newFIS());
	public static final Item I_DEEP_OCEAN_METAL_BOOTS = new OceanMetalArmor(OceanMetalArmorMaterial.AM_DEEP_OCEAN_METAL, EquipmentSlot.FEET, newFIS());

	private static Supplier<ItemStack> itemGroupIcon(String id) {
		if (id.equals("general")) {
			return () -> new ItemStack(BI_OMC_CORE);
		}
		return () -> new ItemStack(Blocks.STONE);
	}

	private static BlockItem toBI(Block b) {
		return new BlockItem(b, new FabricItemSettings().group(IG_GENERAL));
	}
	
	private static FabricItemSettings newFIS() {
		return new FabricItemSettings().group(IG_GENERAL);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "maze_cell_core"), B_OMC_CORE);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "maze_cell_wall"), B_OMC_WALL);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "maze_cell_floor"), B_OMC_FLOOR);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "maze_cell_spawner"), B_OMC_SPAWNER);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "maze_out_wall"), B_OMO_WALL);
		
		Registry.register(Registry.ITEM, new Identifier(MODID, "maze_cell_core"), BI_OMC_CORE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "maze_cell_wall"), BI_OMC_WALL);
		Registry.register(Registry.ITEM, new Identifier(MODID, "maze_cell_floor"), BI_OMC_FLOOR);
		Registry.register(Registry.ITEM, new Identifier(MODID, "maze_cell_spawner"), BI_OMC_SPAWNER);
		Registry.register(Registry.ITEM, new Identifier(MODID, "maze_out_wall"), BI_OMO_WALL);

		Registry.register(Registry.BLOCK, new Identifier(MODID, "water_metal_block"), B_WATER_METAL_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "ocean_metal_block"), B_OCEAN_METAL_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "deep_ocean_wall_block"), B_DEEP_OCEAN_METAL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_block"), BI_WATER_METAL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_block"), BI_OCEAN_METAL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_wall_block"), BI_DEEP_OCEAN_METAL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_ingot"), I_WATER_METAL_INGOT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_ingot"), I_OCEAN_METAL_INGOT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_wall_ingot"), I_DEEP_OCEAN_METAL_INGOT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_nugget"), I_WATER_METAL_NUGGET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_nugget"), I_OCEAN_METAL_NUGGET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_wall_nugget"), I_DEEP_OCEAN_METAL_NUGGET);

		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_helmet"), I_WATER_METAL_HELMET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_chestplate"), I_WATER_METAL_CHESTPLATE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_leggings"), I_WATER_METAL_LEGGINGS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "water_metal_boots"), I_WATER_METAL_BOOTS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_helmet"), I_OCEAN_METAL_HELMET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_chestplate"), I_OCEAN_METAL_CHESTPLATE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_leggings"), I_OCEAN_METAL_LEGGINGS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ocean_metal_boots"), I_OCEAN_METAL_BOOTS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_metal_helmet"), I_DEEP_OCEAN_METAL_HELMET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_metal_chestplate"), I_DEEP_OCEAN_METAL_CHESTPLATE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_metal_leggings"), I_DEEP_OCEAN_METAL_LEGGINGS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "deep_ocean_metal_boots"), I_DEEP_OCEAN_METAL_BOOTS);
		
		OceanMazeStructureReg.onInit();
	}
}
