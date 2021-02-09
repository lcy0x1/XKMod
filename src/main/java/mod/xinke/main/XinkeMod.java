package mod.xinke.main;

import java.util.function.Supplier;

import mod.xinke.block.BladeCrop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
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

	public static final Block B_BLADE_CROP = new BladeCrop(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static final Block B_XKSTEEL_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).hardness(3f).resistance(8f));
	public static final BlockItem BI_XKSTEEL_BLOCK = new BlockItem(B_XKSTEEL_BLOCK, new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_BLADE = new AliasedBlockItem(B_BLADE_CROP,new FabricItemSettings().group(IG_GENERAL));
	public static final Item I_XKSTEEL_INGOT = new Item(new FabricItemSettings().group(IG_GENERAL));	
	
	private static Supplier<ItemStack> itemGroupIcon(String id) {
		if (id.equals("general")) {
			return () -> new ItemStack(I_BLADE);
		}
		return () -> new ItemStack(Blocks.STONE);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		Registry.register(Registry.BLOCK, new Identifier(MODID, "blade_crop"), B_BLADE_CROP);
		Registry.register(Registry.BLOCK, new Identifier(MODID, "xinke_steel_block"), B_XKSTEEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_block"),BI_XKSTEEL_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "xinke_steel_ingot"), I_XKSTEEL_INGOT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "blade_crop"), I_BLADE);
		
		BlockRenderLayerMap.INSTANCE.putBlock(B_BLADE_CROP, RenderLayer.getCutout());
	}

}
