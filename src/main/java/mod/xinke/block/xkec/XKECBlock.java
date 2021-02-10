package mod.xinke.block.xkec;

import java.util.function.Supplier;

import mod.xinke.block.BaseBlock.BaseBlockWithEntity;
import mod.xinke.block.BlockProp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class XKECBlock extends BaseBlockWithEntity {

	private static class XKECUse implements IClick {

		private static XKECUse INSTANCE = new XKECUse();

		@Override
		public ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult hit) {
			if (w.isClient)
				return ActionResult.SUCCESS;
			BlockEntity be = w.getBlockEntity(pos);
			if (be instanceof AbstractXKECBlockEntity) {
				AbstractXKECBlockEntity<?> xkec = (AbstractXKECBlockEntity<?>) be;
				if (xkec.isEmpty())
					if (pl.getActiveItem().isEmpty())
						xkec.activate();
					else
						xkec.setStack(0, pl.getActiveItem());
				else if (pl.getActiveItem().isEmpty())
					pl.giveItemStack(xkec.removeStack(0));
				else
					xkec.activate();
			}
			return ActionResult.CONSUME;
		}

	}

	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

	public XKECBlock(BlockProp prop, Supplier<BlockEntity> sup) {
		super(prop, (STE) sup, XKECUse.INSTANCE);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
