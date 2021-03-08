package mod.oceanmaze.block;

import java.util.Random;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.main.BIReg;
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
		public void appendProperties(Builder<Block, BlockState> builder) {
			builder.add(Properties.PERSISTENT);
			builder.add(Properties.DISTANCE_0_7);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.PERSISTENT, true).with(Properties.DISTANCE_0_7, 0);
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState drep = state.with(Properties.PERSISTENT, false);
			if (state.get(Properties.PERSISTENT) && world.isReceivingRedstonePower(pos))
				return;
			int min = 8;
			for (Direction dir : Direction.values()) {
				BlockState bs = world.getBlockState(pos.offset(dir));
				if (bs.getBlock() instanceof MazeBlock || bs.getBlock() == Blocks.SEA_LANTERN) {
					min = Math.min(-1, min);
					world.setBlockState(pos.offset(dir), drep.with(Properties.DISTANCE_0_7, 0));
				} else if (bs.getBlock() instanceof DegradeBlock)
					min = Math.min(bs.get(Properties.DISTANCE_0_7), min);
			}
			min++;
			if (min < 8 && state.get(Properties.DISTANCE_0_7) != min)
				world.setBlockState(pos, drep.with(Properties.DISTANCE_0_7, min));
			else {
				BlockState bs = world.getBlockState(pos.up());
				if (bs.isAir() || bs.getBlock() == Blocks.WATER || bs.getBlock() == BIReg.B_OMO_DEGRADE) {
					BlockState rep = Blocks.WATER.getDefaultState();
					double ra = r.nextDouble();
					if (ra < 1e-4)
						rep = BIReg.B_CLEAR.getDefaultState();
					else if (ra < 3e-4)
						rep = Blocks.ANCIENT_DEBRIS.getDefaultState();
					else if (ra < 7e-4)
						rep = Blocks.CRYING_OBSIDIAN.getDefaultState();
					else if (ra < 15e-4)
						rep = Blocks.OBSIDIAN.getDefaultState();
					world.setBlockState(pos, rep);
				} else
					world.setBlockState(pos, Blocks.PRISMARINE.getDefaultState());
			}

		}

	}

	public DegradeBlock(BlockProp p) {
		super(p, MazeBlock.CLICK, INSTANCE);
	}

}
