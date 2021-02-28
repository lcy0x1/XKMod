package mod.oceanmaze.block;

import java.util.Random;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.main.OceanMaze;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class OpenBlock extends BaseBlock {

	public static final Open INSTANCE = new Open();

	public static class Open implements IScheduledTick {

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			if (pos.getY() == 0 || world.isReceivingRedstonePower(pos))
				return;
			for (BlockRotation br : BlockRotation.values()) {
				BlockPos p10 = pos.offset(br.rotate(Direction.NORTH));
				BlockPos p11 = p10.offset(br.rotate(Direction.EAST));
				BlockPos p20 = p10.offset(br.rotate(Direction.NORTH));
				BlockPos p21 = p20.offset(br.rotate(Direction.EAST));
				BlockPos p22 = p20.offset(br.rotate(Direction.WEST));
				world.setBlockState(p10, Blocks.WATER.getDefaultState());
				world.setBlockState(p11, Blocks.WATER.getDefaultState());
				world.setBlockState(p10.down(), Blocks.WATER.getDefaultState());
				world.setBlockState(p11.down(), Blocks.WATER.getDefaultState());
				getState(p20, world, br, true);
				getState(p21, world, br, true);
				getState(p22, world, br, true);
				getState(p20.down(), world, br, false);
				getState(p21.down(), world, br, false);
				getState(p22.down(), world, br, false);
			}
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
			world.setBlockState(pos.down(), Blocks.WATER.getDefaultState());

		}

		private static void getState(BlockPos pos, ServerWorld world, BlockRotation rot, boolean upper) {
			BlockState wall = OceanMaze.B_OMC_WALL.getDefaultState().with(Properties.SOUTH, true);
			BlockState out = OceanMaze.B_OMO_WALL.getDefaultState().with(upper ? Properties.UP : Properties.DOWN, true)
					.with(Properties.SOUTH, true);
			BlockState bs = world.getBlockState(pos);
			if (bs.getBlock() == OceanMaze.B_OMC_CORE)
				world.setBlockState(pos, wall.rotate(rot));
			else if (bs.getBlock() == OceanMaze.B_OMC_FLOOR || bs.getBlock() == OceanMaze.B_OMO_WALL)
				world.setBlockState(pos, out.rotate(rot));
		}

	}

	public OpenBlock(BlockProp p) {
		super(p, INSTANCE);
	}

}
