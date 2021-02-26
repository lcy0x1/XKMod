package mod.xinke.block.xkec;

import mod.lcy0x1.block.BlockProp;
import mod.lcy0x1.block.BaseBlock.BaseBlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class XKECBlock extends BaseBlockWithEntity {

	private static class XKECUse implements IClick, IRep {

		private static XKECUse INSTANCE = new XKECUse();

		@Override
		public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
			if (worldIn.isClient)
				return;
			if (state.getBlock() == newState.getBlock())
				return;
			BlockEntity be = worldIn.getBlockEntity(pos);
			if (be instanceof AbstractXKECBlockEntity) {
				AbstractXKECBlockEntity<?> abe = (AbstractXKECBlockEntity<?>) be;
				abe.onDestroy();
			}
		}

		@Override
		public ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult hit) {
			if (w.isClient)
				return ActionResult.SUCCESS;
			BlockEntity be = w.getBlockEntity(pos);
			if (be instanceof AbstractXKECBlockEntity) {
				AbstractXKECBlockEntity<?> xkec = (AbstractXKECBlockEntity<?>) be;
				if (xkec.isEmpty())
					if (pl.getMainHandStack().isEmpty())
						xkec.activate(pl);
					else {
						xkec.setStack(0, pl.getMainHandStack());
						w.setBlockState(pos, bs.with(Properties.LIT, true));
					}
				else if (pl.getMainHandStack().isEmpty()) {
					pl.giveItemStack(xkec.removeStack(0));
					w.setBlockState(pos, bs.with(Properties.LIT, false));
				} else
					xkec.activate(pl);
			}
			return ActionResult.CONSUME;
		}

	}

	private static class XKState implements IState, ILight {

		public static final XKState INSTANCE = new XKState();

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(Properties.LIT);
		}

		@Override
		public int getLightValue(BlockState bs) {
			return bs.get(Properties.LIT) ? 15 : 7;
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.LIT, false);
		}

	}

	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

	public XKECBlock(BlockProp prop, STE sup) {
		super(prop, sup, XKState.INSTANCE, XKECUse.INSTANCE);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(Properties.LIT) ? super.getOutlineShape(state, world, pos, context) : SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
