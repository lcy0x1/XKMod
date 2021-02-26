package mod.oceanmaze.structure;

import com.mojang.serialization.Codec;

import mod.oceanmaze.main.OceanMazeStructureReg;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class UnderWaterStructureProcessor extends StructureProcessor {

	public static final UnderWaterStructureProcessor INSTANCE = new UnderWaterStructureProcessor();
	public static final Codec<UnderWaterStructureProcessor> CODEC = Codec.unit(INSTANCE);

	@Override
	public StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pos2, StructureBlockInfo sbi,
			StructureBlockInfo sbi2, StructurePlacementData spd) {
		if (sbi2.state.isAir())
			return null;
		if (sbi2.state.getBlock() == Blocks.STRUCTURE_BLOCK)
			return new Structure.StructureBlockInfo(sbi2.pos, Blocks.WATER.getDefaultState(), sbi2.tag);
		return sbi2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return OceanMazeStructureReg.SPCT_UW;
	}

}
