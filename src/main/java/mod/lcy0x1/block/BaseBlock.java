package mod.lcy0x1.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BaseBlock extends Block {

	public static class BaseBlockWithEntity extends BaseBlock implements BlockEntityProvider {

		public BaseBlockWithEntity(BlockImplementor bimpl) {
			super(bimpl);
		}

		public BaseBlockWithEntity(BlockProp p, IImpl... impl) {
			this(construct(p).addImpls(impl));
		}

		@Override
		public final BlockEntity createBlockEntity(BlockView world) {
			if (impl.ite != null)
				return impl.ite.createTileEntity(world);
			return null;
		}

		@Override
		@Nullable
		public final NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
		}

		@Override
		public final boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
			super.onSyncedBlockEvent(state, world, pos, type, data);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			return blockEntity == null ? false : blockEntity.onSyncedBlockEvent(type, data);
		}

	}

	public static class BlockImplementor {

		private final FabricBlockSettings props;
		private final List<IState> stateList = new ArrayList<>();
		private final List<IRep> repList = new ArrayList<>();
		private final List<IScheduledTick> schetickList = new ArrayList<>();

		private IRotMir rotmir;
		private IFace face;
		private ITE ite;
		private IPower power;
		private IClick click;
		private INeighbor neigh;
		private IEntityCollision entcoll;

		public BlockImplementor(FabricBlockSettings p) {
			props = p;
		}

		public BlockImplementor addImpl(IImpl impl) {
			if (impl instanceof IState)
				stateList.add((IState) impl);
			if (impl instanceof IRep)
				repList.add((IRep) impl);
			if (impl instanceof IScheduledTick)
				schetickList.add((IScheduledTick) impl);
			if (impl instanceof STE)
				impl = new TEPvd((STE) impl);
			if (impl instanceof ILight)
				props.luminance(((ILight) impl)::getLightValue);
			if (impl instanceof IRotMir)
				rotmir = (IRotMir) impl;
			if (impl instanceof IFace)
				face = (IFace) impl;
			if (impl instanceof ITE)
				ite = (ITE) impl;
			if (impl instanceof IPower)
				power = (IPower) impl;
			if (impl instanceof INeighbor)
				neigh = (INeighbor) impl;
			if (impl instanceof IEntityCollision)
				entcoll = (IEntityCollision) impl;
			if (impl instanceof IClick && (!(impl instanceof TEPvd) || click == null))
				click = (IClick) impl;
			return this;
		}

		public BlockImplementor addImpls(IImpl... impls) {
			for (IImpl impl : impls)
				if (impl != null)
					addImpl(impl);
			return this;
		}

	}

	public static interface IClick extends IImpl {

		public ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult hit);

	}

	public static interface IImpl {
	}

	public static interface IEntityCollision extends IImpl {

		public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity);

	}

	public static interface IScheduledTick extends IImpl {

		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r);

	}

	public static interface INeighbor extends IImpl {

		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
				boolean notify);

	}

	public static interface ILight extends IImpl {

		public int getLightValue(BlockState bs);

	}

	public static interface IRep extends IImpl {

		public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving);

	}

	public static interface IState extends IImpl {

		public void fillStateContainer(StateManager.Builder<Block, BlockState> builder);

		public BlockState setDefaultState(BlockState bs);

	}

	@FunctionalInterface
	public static interface STE extends IImpl, Supplier<BlockEntity> {

		@Override
		public BlockEntity get();

	}

	private static class AllDireBlock implements BaseBlock.IFace, BaseBlock.IState {

		private AllDireBlock() {
		}

		@Override
		public void fillStateContainer(StateManager.Builder<Block, BlockState> builder) {
			builder.add(FACING);
		}

		@Override
		public BlockState getStateForPlacement(BlockState def, ItemPlacementContext context) {
			return def.with(FACING, context.getPlayerLookDirection().getOpposite());
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(FACING, Direction.NORTH);
		}

	}

	private static class HorizontalBlock implements IRotMir, IState, IFace {

		private HorizontalBlock() {
		}

		@Override
		public void fillStateContainer(StateManager.Builder<Block, BlockState> builder) {
			builder.add(HORIZONTAL_FACING);
		}

		@Override
		public BlockState getStateForPlacement(BlockState def, ItemPlacementContext context) {
			return def.with(HORIZONTAL_FACING, context.getPlayerFacing().getOpposite());
		}

		@Override
		public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
			return state.rotate(mirrorIn.getRotation(state.get(HORIZONTAL_FACING)));
		}

		@Override
		public BlockState rotate(BlockState state, BlockRotation rot) {
			return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(HORIZONTAL_FACING, Direction.NORTH);
		}
	}

	private static interface IFace extends IImpl {

		public BlockState getStateForPlacement(BlockState def, ItemPlacementContext context);

	}

	private static interface IPower extends IImpl {

		public int getWeakPower(BlockState bs, BlockView r, BlockPos pos, Direction d);

	}

	private static interface IRotMir extends IImpl {

		public BlockState mirror(BlockState state, BlockMirror mirrorIn);

		public BlockState rotate(BlockState state, BlockRotation rot);
	}

	private static interface ITE extends IImpl {

		public BlockEntity createTileEntity(BlockView world);

	}

	private static class Power implements IState, IPower {

		private Power() {
		}

		@Override
		public void fillStateContainer(StateManager.Builder<Block, BlockState> builder) {
			builder.add(POWER_0_15);
		}

		@Override
		public int getWeakPower(BlockState bs, BlockView r, BlockPos pos, Direction d) {
			return bs.get(POWER_0_15);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(POWER_0_15, 0);
		}

	}

	private static class TEPvd implements ITE, IClick {

		private final Supplier<? extends BlockEntity> f;

		private TEPvd(Supplier<? extends BlockEntity> sup) {
			f = sup;
		}

		@Override
		public BlockEntity createTileEntity(BlockView world) {
			return f.get();
		}

		@Override
		public ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult hit) {
			if (w.isClient())
				return ActionResult.SUCCESS;
			BlockEntity te = w.getBlockEntity(pos);
			if (te instanceof NamedScreenHandlerFactory)
				pl.openHandledScreen((NamedScreenHandlerFactory) te);
			return ActionResult.CONSUME;
		}

	}

	public static final Power POW = new Power();
	public static final AllDireBlock ALD = new AllDireBlock();
	public static final HorizontalBlock HOR = new HorizontalBlock();

	public static final IntProperty POWER_0_15 = Properties.POWER;
	public static final DirectionProperty FACING = Properties.FACING;
	public static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;

	private static BlockImplementor TEMP;

	public static BlockImplementor construct(BlockProp bb) {
		return new BlockImplementor(bb.getProps());
	}

	private static FabricBlockSettings handler(BlockImplementor bi) {
		if (TEMP != null)
			throw new RuntimeException("concurrency error");
		TEMP = bi;
		return bi.props;
	}

	protected BlockImplementor impl;

	public BaseBlock(BlockImplementor bimpl) {
		super(handler(bimpl));
		BlockState bs = this.getDefaultState();
		for (IState ist : impl.stateList)
			bs = ist.setDefaultState(bs);
		this.setDefaultState(bs);
	}

	public BaseBlock(BlockProp p, IImpl... impl) {
		this(construct(p).addImpls(impl));
	}

	@Override
	public final boolean emitsRedstonePower(BlockState bs) {
		return impl.power != null;
	}

	@Override
	public final int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
		if (impl.ite == null)
			return 0;
		BlockEntity te = worldIn.getBlockEntity(pos);
		return te == null ? 0 : ScreenHandler.calculateComparatorOutput(te);
	}

	@Override
	public final BlockState getPlacementState(ItemPlacementContext context) {
		if (impl.face == null)
			return getDefaultState();
		return impl.face.getStateForPlacement(getDefaultState(), context);
	}

	@Override
	public final int getWeakRedstonePower(BlockState bs, BlockView r, BlockPos pos, Direction d) {
		return impl.power == null ? 0 : impl.power.getWeakPower(bs, r, pos, d);
	}

	@Override
	public final boolean hasComparatorOutput(BlockState bs) {
		return impl.ite != null;
	}

	@Override
	public final BlockState mirror(BlockState state, BlockMirror mirrorIn) {
		if (impl.rotmir != null)
			return impl.rotmir.mirror(state, mirrorIn);
		return state;
	}

	@Override
	public final void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState,
			boolean isMoving) {
		for (IRep irep : impl.repList)
			irep.onReplaced(state, worldIn, pos, newState, isMoving);
		if (impl.ite != null && state.getBlock() != newState.getBlock()) {
			BlockEntity blockentity = worldIn.getBlockEntity(pos);
			if (blockentity != null) {
				if (blockentity instanceof Inventory) {
					ItemScatterer.spawn(worldIn, pos, (Inventory) blockentity);
					worldIn.updateComparators(pos, this);
				}
				worldIn.removeBlockEntity(pos);
			}
		}
	}

	@Override
	public final ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult r) {
		return impl.click == null ? ActionResult.PASS : impl.click.onUse(bs, w, pos, pl, h, r);
	}

	@Override
	public final BlockState rotate(BlockState state, BlockRotation rot) {
		if (impl.rotmir != null)
			return impl.rotmir.rotate(state, rot);
		return state;
	}

	@Override
	protected final void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		impl = TEMP;
		TEMP = null;
		for (IState is : impl.stateList)
			is.fillStateContainer(builder);
	}

	public final void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
			boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		if (impl.neigh != null)
			impl.neigh.neighborUpdate(state, world, pos, block, fromPos, notify);
	}

	public final void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
		super.scheduledTick(state, world, pos, r);
		for (IScheduledTick ticker : impl.schetickList)
			ticker.scheduledTick(state, world, pos, r);
	}

	public final void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (impl.entcoll != null)
			impl.entcoll.onEntityCollision(state, world, pos, entity);
	}

}
