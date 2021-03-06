package mod.oceanmaze.block;

import java.util.Random;
import java.util.function.Predicate;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public class DehydrateBlock extends BaseBlock {

	public static final IntProperty PROP = IntProperty.of("distance", 0, 31);

	public static class Dehydrate implements IState, IScheduledTick {

		public final Predicate<BlockState> target, trigger;

		public Dehydrate(Predicate<BlockState> target, Predicate<BlockState> trigger) {
			this.target = target;
			this.trigger = trigger;
		}

		public Dehydrate(Block b, Block t) {
			this.target = (bs) -> bs.getBlock() == b;
			this.trigger = (bs) -> bs.getBlock() == t;
		}

		public Dehydrate(Block b) {
			this.target = (bs) -> bs.getBlock() == b;
			this.trigger = (bs) -> false;
		}

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(PROP);

		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(PROP, 0);
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			if (world.isReceivingRedstonePower(pos))
				return;
			int wc = 0;
			for (Direction dir : Direction.values()) {
				BlockState bs = world.getBlockState(pos.offset(dir));
				if (trigger.test(bs))
					wc++;
			}
			if (wc == 0)
				world.setBlockState(pos, Blocks.AIR.getDefaultState());

			int max = state.get(PROP);
			if (max < 32)
				for (Direction dir : Direction.values()) {
					BlockState bs = world.getBlockState(pos.offset(dir));
					if (target.test(bs))
						if (dir.getAxis() == Axis.Y)
							world.setBlockState(pos.offset(dir), state.with(PROP, max));
						else if (max < 31)
							world.setBlockState(pos.offset(dir), state.with(PROP, max + 1));
					if (this == WATER && bs.contains(Properties.WATERLOGGED) && bs.get(Properties.WATERLOGGED))
						world.setBlockState(pos.offset(dir), bs.with(Properties.WATERLOGGED, false));
				}

		}

	}

	public static final Dehydrate WATER = new Dehydrate(
			(bs) -> bs.getMaterial() == Material.WATER || bs.getMaterial() == Material.REPLACEABLE_UNDERWATER_PLANT
					|| bs.getMaterial() == Material.UNDERWATER_PLANT,
			(bs) -> bs.getFluidState().getFluid() == Fluids.FLOWING_WATER
					|| bs.getFluidState().getFluid() == Fluids.WATER);
	public static final Dehydrate LAVA = new Dehydrate(Blocks.LAVA, Blocks.LAVA);
	public static final Dehydrate STONE = new Dehydrate(Blocks.STONE);
	public static final Dehydrate SAND = new Dehydrate(Blocks.SAND);
	public static final Dehydrate NETHER = new Dehydrate(Blocks.NETHERRACK);
	public static final Dehydrate END = new Dehydrate(Blocks.END_STONE);

	public DehydrateBlock(BlockProp p, Dehydrate impl) {
		super(p, impl);
	}

}
