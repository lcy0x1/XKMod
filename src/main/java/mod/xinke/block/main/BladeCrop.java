package mod.xinke.block.main;

import java.util.Random;

import mod.xinke.main.XinkeMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BladeCrop extends CropBlock {

	private static final VoxelShape[] SHAPES = new VoxelShape[] {
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
			Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D) };

	public BladeCrop(Settings settings) {
		super(settings);
	}

	public VoxelShape getShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(this.getAgeProperty())];
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBaseLightLevel(pos, 0) < 9)
			return;
		int i = this.getAge(state);
		if (i >= this.getMaxAge())
			return;
		float f = getAvailableMoisture(this, world, pos);
		f = f * f / 10;
		if (!world.isSkyVisible(pos))
			f /= 2;
		f *= world.getBaseLightLevel(pos, 0) / 16f;
		if (random.nextInt((int) (25.0F / f) + 1) == 0)
			world.setBlockState(pos, this.withAge(i + 1), 2);
	}

	@Override
	protected int getGrowthAmount(World world) {
		return 1;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected ItemConvertible getSeedsItem() {
		return XinkeMod.I_BLADE;
	}

}
