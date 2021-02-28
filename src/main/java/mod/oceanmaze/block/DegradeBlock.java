package mod.oceanmaze.block;

import java.util.Random;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DegradeBlock extends BaseBlock {

	public static final Degrade INSTANCE = new Degrade();

	public static class Degrade implements IState, IScheduledTick {

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(Properties.DISTANCE_0_7);

		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.DISTANCE_0_7, 0);
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			if (world.isReceivingRedstonePower(pos))
				return;
			int min = 8;
			for (Direction dir : Direction.values()) {
				BlockState bs = world.getBlockState(pos.offset(dir));
				if (bs.getBlock() instanceof MazeBlock) {
					min = Math.min(-1, min);
					if (!world.isReceivingRedstonePower(pos.offset(dir)))
						world.setBlockState(pos.offset(dir), state.with(Properties.DISTANCE_0_7, 0));
				} else if (bs.getBlock() instanceof DegradeBlock)
					min = Math.min(bs.get(Properties.DISTANCE_0_7), min);
			}
			min++;
			if (min < 8 && state.get(Properties.DISTANCE_0_7) != min)
				world.setBlockState(pos, state.with(Properties.DISTANCE_0_7, min));
			else
				world.setBlockState(pos, Blocks.WATER.getDefaultState());

		}

	}

	public DegradeBlock(BlockProp p) {
		super(p, INSTANCE);
	}

}
