package mod.oceanmaze.main;

import java.util.function.Supplier;

import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.block.DegradeBlock;
import mod.oceanmaze.block.DehydrateBlock;
import mod.oceanmaze.block.MazeBlock;
import mod.oceanmaze.block.OpenBlock;
import mod.oceanmaze.item.OMArmorMat;
import mod.oceanmaze.item.OceanMazeMap;
import mod.oceanmaze.item.OceanMetalArmor;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BIReg {

	public static final ItemGroup IG_GENERAL = FabricItemGroupBuilder.build(new Identifier(OceanMaze.MODID, "general"),
			itemGroupIcon("general"));

	public static final BlockProp BP_OM = new BlockProp(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).dropsNothing());
	public static final BlockProp BP_OMT = new BlockProp(
			FabricBlockSettings.copyOf(Blocks.OBSIDIAN).dropsNothing().ticksRandomly());
	public static final BlockProp BP_DEH = new BlockProp(
			FabricBlockSettings.copyOf(Blocks.STONE).dropsNothing().ticksRandomly());
	public static final FabricBlockSettings BP_METAL = FabricBlockSettings.copyOf(Blocks.IRON_BLOCK);

	public static final Block B_OMC_CORE = new MazeBlock(BP_OM, MazeBlock.NEI, MazeBlock.HOR, MazeBlock.FLOOR);
	public static final Block B_OMC_WALL = new MazeBlock(BP_OM, MazeBlock.NEI, MazeBlock.HOR);
	public static final Block B_OMC_FLOOR = new MazeBlock(BP_OM, MazeBlock.NEI, MazeBlock.FLOOR);
	public static final Block B_OMC_SPAWNER = new MazeBlock(BP_OM, MazeBlock.SPAWNER);
	public static final Block B_OMO_WALL = new MazeBlock(BP_OM, MazeBlock.NEI, MazeBlock.ALL_DIRE_STATE);
	public static final Block B_OMO_OPEN = new OpenBlock(BP_OMT);
	public static final Block B_OMO_DEGRADE = new DegradeBlock(BP_OMT);

	public static final Block B_CLEAR = new Block(FabricBlockSettings.copy(Blocks.STONE));
	public static final Block B_CLEAR_WATER = new DehydrateBlock(BP_DEH, DehydrateBlock.WATER);
	public static final Block B_CLEAR_LAVA = new DehydrateBlock(BP_DEH, DehydrateBlock.LAVA);
	public static final Block B_CLEAR_STONE = new DehydrateBlock(BP_DEH, DehydrateBlock.STONE);
	public static final Block B_CLEAR_SAND = new DehydrateBlock(BP_DEH, DehydrateBlock.SAND);
	public static final Block B_CLEAR_NETHER = new DehydrateBlock(BP_DEH, DehydrateBlock.NETHER);
	public static final Block B_CLEAR_END = new DehydrateBlock(BP_DEH, DehydrateBlock.END);

	public static final Block B_WATER_METAL_BLOCK = new Block(BP_METAL);
	public static final Block B_OCEAN_METAL_BLOCK = new Block(BP_METAL);
	public static final Block B_DEEP_OCEAN_METAL_BLOCK = new Block(BP_METAL);
	
	public static final Item I_WATER_METAL_INGOT = new Item(newFIS());
	public static final Item I_OCEAN_METAL_INGOT = new Item(newFIS());
	public static final Item I_DEEP_OCEAN_METAL_INGOT = new Item(newFIS());
	public static final Item I_WATER_METAL_NUGGET = new Item(newFIS());
	public static final Item I_OCEAN_METAL_NUGGET = new Item(newFIS());
	public static final Item I_DEEP_OCEAN_METAL_NUGGET = new Item(newFIS());
	public static final Item I_WM_HELMET = new OceanMetalArmor(OMArmorMat.WATER, EquipmentSlot.HEAD, newFIS());
	public static final Item I_WM_CHESTPLATE = new OceanMetalArmor(OMArmorMat.WATER, EquipmentSlot.CHEST, newFIS());
	public static final Item I_WM_LEGGINGS = new OceanMetalArmor(OMArmorMat.WATER, EquipmentSlot.LEGS, newFIS());
	public static final Item I_WM_BOOTS = new OceanMetalArmor(OMArmorMat.WATER, EquipmentSlot.FEET, newFIS());
	public static final Item I_OM_HELMET = new OceanMetalArmor(OMArmorMat.OCEAN, EquipmentSlot.HEAD, newFIS());
	public static final Item I_OM_CHESTPLATE = new OceanMetalArmor(OMArmorMat.OCEAN, EquipmentSlot.CHEST, newFIS());
	public static final Item I_OM_LEGGINGS = new OceanMetalArmor(OMArmorMat.OCEAN, EquipmentSlot.LEGS, newFIS());
	public static final Item I_OM_BOOTS = new OceanMetalArmor(OMArmorMat.OCEAN, EquipmentSlot.FEET, newFIS());
	public static final Item I_DOM_HELMET = new OceanMetalArmor(OMArmorMat.DEEP, EquipmentSlot.HEAD, newFIS());
	public static final Item I_DOM_CHESTPLATE = new OceanMetalArmor(OMArmorMat.DEEP, EquipmentSlot.CHEST, newFIS());
	public static final Item I_DOM_LEGGINGS = new OceanMetalArmor(OMArmorMat.DEEP, EquipmentSlot.LEGS, newFIS());
	public static final Item I_DOM_BOOTS = new OceanMetalArmor(OMArmorMat.DEEP, EquipmentSlot.FEET, newFIS());

	public static final Item I_MAZE_MAP = new OceanMazeMap(newFIS().maxCount(1));

	private static Supplier<ItemStack> itemGroupIcon(String id) {
		if (id.equals("general")) {
			return () -> new ItemStack(Items.TRIDENT);
		}
		return () -> new ItemStack(Blocks.STONE);
	}

	private static FabricItemSettings newFIS() {
		return new FabricItemSettings().group(IG_GENERAL);
	}

	private static BlockItem toBI(Block b) {
		return new BlockItem(b, new FabricItemSettings().group(IG_GENERAL));
	}

	private static void regBlock(String id, Block b) {
		Registry.register(Registry.BLOCK, new Identifier(OceanMaze.MODID, id), b);
		regItem(id, toBI(b));
	}
	
	private static void regItem(String id, Item i) {
		Registry.register(Registry.ITEM, new Identifier(OceanMaze.MODID, id), i);
	}
	
	

	public static void init() {
		regBlock( "maze_cell_core", B_OMC_CORE);
		regBlock( "maze_cell_wall", B_OMC_WALL);
		regBlock( "maze_cell_floor", B_OMC_FLOOR);
		regBlock( "maze_cell_spawner", B_OMC_SPAWNER);
		regBlock( "maze_out_wall", B_OMO_WALL);
		regBlock( "maze_open", B_OMO_OPEN);
		regBlock( "maze_degrade", B_OMO_DEGRADE);
		regBlock( "clear", B_CLEAR);
		regBlock( "clear_water", B_CLEAR_WATER);
		regBlock( "clear_lava", B_CLEAR_LAVA);
		regBlock( "clear_stone", B_CLEAR_STONE);
		regBlock( "clear_sand", B_CLEAR_SAND);
		regBlock( "clear_nether", B_CLEAR_NETHER);
		regBlock( "clear_end", B_CLEAR_END);
		regBlock( "water_metal_block", B_WATER_METAL_BLOCK);
		regBlock( "ocean_metal_block", B_OCEAN_METAL_BLOCK);
		regBlock( "deep_ocean_metal_block", B_DEEP_OCEAN_METAL_BLOCK);
		
		regItem( "water_metal_ingot", I_WATER_METAL_INGOT);
		regItem( "ocean_metal_ingot", I_OCEAN_METAL_INGOT);
		regItem( "deep_ocean_metal_ingot", I_DEEP_OCEAN_METAL_INGOT);
		regItem( "water_metal_nugget", I_WATER_METAL_NUGGET);
		regItem( "ocean_metal_nugget", I_OCEAN_METAL_NUGGET);
		regItem( "deep_ocean_metal_nugget", I_DEEP_OCEAN_METAL_NUGGET);
		regItem( "water_metal_helmet", I_WM_HELMET);
		regItem( "water_metal_chestplate", I_WM_CHESTPLATE);
		regItem( "water_metal_leggings", I_WM_LEGGINGS);
		regItem( "water_metal_boots", I_WM_BOOTS);
		regItem( "ocean_metal_helmet", I_OM_HELMET);
		regItem( "ocean_metal_chestplate", I_OM_CHESTPLATE);
		regItem( "ocean_metal_leggings", I_OM_LEGGINGS);
		regItem( "ocean_metal_boots", I_OM_BOOTS);
		regItem( "deep_ocean_metal_helmet", I_DOM_HELMET);
		regItem( "deep_ocean_metal_chestplate", I_DOM_CHESTPLATE);
		regItem( "deep_ocean_metal_leggings", I_DOM_LEGGINGS);
		regItem( "deep_ocean_metal_boots", I_DOM_BOOTS);
		regItem( "maze_map", I_MAZE_MAP);

	}

}
