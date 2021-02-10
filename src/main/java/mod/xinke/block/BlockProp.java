package mod.xinke.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class BlockProp {

	public static final BlockProp FBC_XKN = new BlockProp(
			FabricBlockSettings.of(Material.GLASS, MaterialColor.DIAMOND).luminance(bs -> 15).nonOpaque(), 3, 3);

	private final FabricBlockSettings props;

	private BlockProp(FabricBlockSettings mat, float hard, float rest) {
		props = mat;
		props.hardness(hard).resistance(rest);
	}

	private BlockProp(Material mat, float hard, float rest) {
		this(FabricBlockSettings.of(mat), hard, rest);
	}

	public FabricBlockSettings getProps() {
		return props;
	}

	public BlockProp setTool(Tag<Item> tool, int level) {
		props.requiresTool().breakByTool(tool, level);
		return this;
	}

}
