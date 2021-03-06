package mod.oceanmaze.block;

import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
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

		public final int range;
		public final Block fin;
		public final Predicate<BlockState> target, trigger = (bs) -> !bs.getFluidState().isEmpty();

		public Dehydrate(int range, Block fin, Predicate<BlockState> target) {
			this.range = range;
			this.fin = fin;
			this.target = target;
		}

		public Dehydrate(int range, Block fin, Block... b) {
			this.range = range;
			this.fin = fin;
			this.target = (bs) -> ImmutableList.copyOf(b).contains(bs.getBlock());
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
			int wt = 0, wc = 0;
			for (Direction dir : Direction.values()) {
				BlockState bs = world.getBlockState(pos.offset(dir));
				if (target.test(bs))
					wt++;
				if (trigger.test(bs))
					wc++;
			}
			if (wc == 0)
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			else if (wt == 0)
				world.setBlockState(pos, fin.getDefaultState());

			int max = state.get(PROP);
			if (max < range)
				for (Direction dir : Direction.values()) {
					BlockState bs = world.getBlockState(pos.offset(dir));
					if (target.test(bs)) {
						if (dir.getAxis() == Axis.Y)
							world.setBlockState(pos.offset(dir), state.with(PROP, max));
						else if (max < range - 1)
							world.setBlockState(pos.offset(dir), state.with(PROP, max + 1));
					} else
						special(world, pos, dir, state, bs);
				}

		}

		public void special(ServerWorld world, BlockPos pos, Direction dir, BlockState state, BlockState bs) {
			if (this == WATER && bs.contains(Properties.WATERLOGGED) && bs.get(Properties.WATERLOGGED))
				world.setBlockState(pos.offset(dir), bs.with(Properties.WATERLOGGED, false));
		}

	}

	public static final Dehydrate WATER = new Dehydrate(32, Blocks.COBBLESTONE,
			(bs) -> ImmutableList.of(Material.WATER, Material.REPLACEABLE_UNDERWATER_PLANT, Material.UNDERWATER_PLANT)
					.contains(bs.getMaterial()));
	public static final Dehydrate LAVA = new Dehydrate(32, Blocks.COBBLESTONE, Blocks.LAVA);
	public static final Dehydrate STONE = new Dehydrate(16, Blocks.COBBLESTONE, Blocks.COBBLESTONE, Blocks.STONE,
			Blocks.GRANITE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.DIRT, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA);
	public static final Dehydrate SAND = new Dehydrate(24, Blocks.COBBLESTONE, Blocks.SAND, Blocks.RED_SAND,
			Blocks.GRAVEL);
	public static final Dehydrate NETHER = new Dehydrate(16, Blocks.NETHERRACK, Blocks.NETHERRACK, Blocks.BLACKSTONE,
			Blocks.BASALT, Blocks.LAVA, Blocks.NETHER_BRICKS, Blocks.GRAVEL, Blocks.SOUL_SOIL, Blocks.SOUL_SAND);
	public static final Dehydrate END = new Dehydrate(24, Blocks.END_STONE, Blocks.END_STONE);

	public DehydrateBlock(BlockProp p, Dehydrate impl) {
		super(p, impl);
	}

}
