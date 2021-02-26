package mod.lcy0x1.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class BlockProp {

	private final FabricBlockSettings props;

	public BlockProp(FabricBlockSettings mat) {
		props = mat;
	}
	
	public BlockProp(FabricBlockSettings mat, float hard, float rest) {
		props = mat;
		props.hardness(hard).resistance(rest);
	}

	public BlockProp(Material mat, float hard, float rest) {
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
