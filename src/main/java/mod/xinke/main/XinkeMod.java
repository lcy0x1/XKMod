package mod.xinke.main;

import java.util.function.Supplier;

import mod.xinke.block.BaseBlock.STE;
import mod.xinke.block.BladeCrop;
import mod.xinke.block.BlockProp;
import mod.xinke.block.CTESReg;
import mod.xinke.block.xkec.XKECBlock;
import mod.xinke.block.xkec.XKECCoreEntity;
import mod.xinke.block.xkec.XKECSideEntity;
import mod.xinke.block.xkec.XKITEntity;
import mod.xinke.block.xkec.XKNodeEntity;
import mod.xinke.item.XKSteelSword;
import mod.xinke.recipe.RecReg;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class XinkeMod implements ModInitializer {

	public static final String MODID = "xinke";

	public static final ItemGroup IG_GENERAL = FabricItemGroupBuilder.build(new Identifier(MODID, "general"),
			itemGroupIcon("general"));

	public static final Block B_BLADE_CROP = new BladeCrop(FabricBlockSettings.of(Material.PLANT).noCollision()
			.ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static final Block B_XKSTEEL_BLOCK = new Block(
			FabricBlockSettings.of(Material.METAL).hardness(3f).resistance(8f));

	public static final Block B_XK_NODE = new XKECBlock(BlockProp.FBC_XKN, (STE) XKNodeEntity::new);
	public static final Block B_XKIT_SOURCE = new XKECBlock(BlockProp.FBC_XKN, (STE) XKITEntity::new);
	public static final Block B_XKIT_MIDDLE = new XKECBlock(BlockProp.FBC_XKN, (STE) XKITEntity::new);
	public static final Block B_XKIT_TARGET = new XKECBlock(BlockProp.FBC_XKN, (STE) XKITEntity::new);
	public static final Block B_XKEC_CORE_0 = new XKECBlock(BlockProp.FBC_XKN, (STE) XKECCoreEntity::new);
	public static final Block B_XKEC_CORE_1 = new XKECBlock(BlockProp.FBC_XKN, (STE) XKECCoreEntity::new);
	public static final Block B_XKEC_CORE_2 = new XKECBlock(BlockProp.FBC_XKN, (STE) XKECCoreEntity::new);
	public static final Block B_XKEC_SIDE = new XKECBlock(BlockProp.FBC_XKN, (STE) XKECSideEntity::new);

	public static final BlockItem BI_XKSTEEL_BLOCK = toBI(B_XKSTEEL_BLOCK);
	public static final BlockItem BI_XK_NODE = toBI(B_XK_NODE);
	public static final BlockItem BI_XKIT_SOURCE = toBI(B_XKIT_SOURCE);
	public static final BlockItem BI_XKIT_MIDDLE = toBI(B_XKIT_MIDDLE);
	public static final BlockItem BI_XKIT_TARGET = toBI(B_XKIT_TARGET);
	public static final BlockItem BI_XKEC_CORE_0 = toBI(B_XKEC_CORE_0);
	public static final BlockItem BI_XKEC_CORE_1 = toBI(B_XKEC_CORE_1);
	public static final BlockItem BI_XKEC_CORE_2 = toBI(B_XKEC_CORE_2);
	public static final BlockItem BI_XKEC_SIDE = toBI(B_XKEC_SIDE);

	public static final Item I_BLADE = new AliasedBlockItem(B_BLADE_CROP, new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_XKSTEEL_NUGGET = new Item(new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_XKSTEEL_INGOT = new Item(new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_XKSTEEL_SWORD = new XKSteelSword(new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_XKCRYSTAL = new XKSteelSword(new FabricItemSettings().group(IG_GENERAL));

	private static Supplier<ItemStack> itemGroupIcon(String id) {
		if (id.equals("general")) {
			return () -> new ItemStack(I_BLADE);
		}
		return () -> new ItemStack(Blocks.STONE);
	}

	private static BlockItem toBI(Block b) {
		return new BlockItem(b, new FabricItemSettings().group(IG_GENERAL));
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		Registry.register(Registry.BLOCK, new Identifier(MODID, "blade_crop"), B_BLADE_CROP);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xinke_steel_block"), B_XKSTEEL_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xk_node"), B_XK_NODE);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkit_source"), B_XKIT_SOURCE);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkit_middle"), B_XKIT_MIDDLE);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkit_target"), B_XKIT_TARGET);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkec_core_0"), B_XKEC_CORE_0);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkec_core_1"), B_XKEC_CORE_1);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkec_core_2"), B_XKEC_CORE_2);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xkec_side"), B_XKEC_SIDE);

		Registry.register(Registry.ITEM, new Identifier(MODID, "blade_crop"), I_BLADE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_nugget"), I_XKSTEEL_NUGGET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_ingot"), I_XKSTEEL_INGOT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_block"), BI_XKSTEEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_sword"), I_XKSTEEL_SWORD);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_crystal"), I_XKCRYSTAL);

		Registry.register(Registry.ITEM, new Identifier(MODID, "xk_node"), BI_XK_NODE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkit_source"), BI_XKIT_SOURCE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkit_middle"), BI_XKIT_MIDDLE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkit_target"), BI_XKIT_TARGET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkec_core_0"), BI_XKEC_CORE_0);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkec_core_1"), BI_XKEC_CORE_1);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkec_core_2"), BI_XKEC_CORE_2);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xkec_side"), BI_XKEC_SIDE);

		new RecReg();
		CTESReg.register();
	}

}
