package mod.oceanmaze.block;

import java.util.List;
import java.util.Random;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.main.BIReg;
import mod.oceanmaze.main.OceanMaze;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class MazeBlock extends BaseBlock {

	public static class AllDireState implements IState, IScheduledTick, IRotMir {

		public static final BooleanProperty[] PROPS = { Properties.DOWN, Properties.UP, Properties.NORTH,
				Properties.SOUTH, Properties.WEST, Properties.EAST };

		@Override
		public void appendProperties(Builder<Block, BlockState> builder) {
			builder.add(PROPS);
		}

		@Override
		public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
			BlockState ans = state;
			for (int i = 2; i < 6; i++) {
				Direction d0 = Direction.values()[i];
				Direction d1 = mirrorIn.apply(d0);
				ans = ans.with(PROPS[d1.ordinal()], state.get(PROPS[d0.ordinal()]));
			}
			return ans;
		}

		@Override
		public BlockState rotate(BlockState state, BlockRotation rot) {
			BlockState ans = state;
			for (int i = 2; i < 6; i++) {
				Direction d0 = Direction.values()[i];
				Direction d1 = rot.rotate(d0);
				ans = ans.with(PROPS[d1.ordinal()], state.get(PROPS[d0.ordinal()]));
			}
			return ans;
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = BIReg.B_OMO_WALL.getDefaultState();
			BlockState self = rep;
			for (int i = 0; i < 6; i++) {
				BlockPos uppos = pos.offset(Direction.values()[i]);
				BlockState torep = world.getBlockState(uppos);
				self = self.with(PROPS[i], torep.getBlock() != self.getBlock());
				if (state.get(PROPS[i]))
					continue;
				if (World.isOutOfBuildLimitVertically(uppos))
					continue;
				if (torep.getMaterial().isReplaceable()) {
					BlockState tar = rep;
					for (int j = 0; j < 6; j++) {
						BlockPos look = uppos.offset(Direction.values()[j]);
						BlockState near = world.getBlockState(look);
						tar = tar.with(PROPS[j], near.getBlock() != rep.getBlock());
					}
					world.setBlockState(uppos, tar);
				}
			}
			if (self != state)
				world.setBlockState(pos, self);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			for (BooleanProperty bp : PROPS)
				bs = bs.with(bp, false);
			return bs;
		}

	}

	public static class DireState implements IState, IScheduledTick, IRotMir {

		public static final BooleanProperty[] PROPS = { Properties.NORTH, Properties.SOUTH, Properties.WEST,
				Properties.EAST };

		@Override
		public void appendProperties(Builder<Block, BlockState> builder) {
			builder.add(PROPS);
		}

		@Override
		public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
			BlockState ans = state;
			for (int i = 0; i < 4; i++) {
				Direction d0 = Direction.values()[i + 2];
				Direction d1 = mirrorIn.apply(d0);
				ans = ans.with(PROPS[d1.ordinal() - 2], state.get(PROPS[d0.ordinal() - 2]));
			}
			return ans;
		}

		@Override
		public BlockState rotate(BlockState state, BlockRotation rot) {
			BlockState ans = state;
			for (int i = 0; i < 4; i++) {
				Direction d0 = Direction.values()[i + 2];
				Direction d1 = rot.rotate(d0);
				ans = ans.with(PROPS[d1.ordinal() - 2], state.get(PROPS[d0.ordinal() - 2]));
			}
			return ans;
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = BIReg.B_OMC_WALL.getDefaultState();
			for (BooleanProperty bp : PROPS)
				rep = rep.with(bp, state.get(bp));
			BlockPos uppos = pos.up();
			if (World.isOutOfBuildLimitVertically(uppos))
				return;
			BlockState torep = world.getBlockState(uppos);
			if (!torep.getMaterial().isReplaceable())
				return;
			world.setBlockState(uppos, rep);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			for (BooleanProperty bp : PROPS)
				bs = bs.with(bp, false);
			return bs;
		}

	}

	public static class FloorProt implements IScheduledTick {

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = BIReg.B_OMC_FLOOR.getDefaultState();
			for (int dir = 0; dir < 4; dir++) {
				BlockPos uppos = pos.offset(Direction.fromHorizontal(dir));
				BlockState torep = world.getBlockState(uppos);
				if (!torep.getMaterial().isReplaceable())
					continue;
				world.setBlockState(uppos, rep);
			}
		}

	}

	public static class Neighbor implements INeighbor {

		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
				boolean notify) {
			if (world.isClient())
				return;
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), DELAY);
		}

	}

	public static class Click implements IClick {

		@Override
		public ActionResult onUse(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockHitResult hit) {
			if (!w.isClient())
				w.getBlockTickScheduler().schedule(pos, bs.getBlock(), DELAY);
			return ActionResult.SUCCESS;
		}

	}

	public static class Spawner implements INeighbor, IScheduledTick, IState {

		@Override
		public void appendProperties(Builder<Block, BlockState> builder) {
			builder.add(Properties.POWERED);
		}

		@Override
		public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
				boolean notify) {
			if (world.isClient())
				return;
			if (state.get(Properties.POWERED) != world.isReceivingRedstonePower(pos))
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2);
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			if (world.getDifficulty() == Difficulty.PEACEFUL)
				return;
			boolean pow = world.isReceivingRedstonePower(pos);
			if (state.get(Properties.POWERED) != pow)
				world.setBlockState(pos, state.with(Properties.POWERED, pow));
			if (!pow)
				return;
			Direction dir = null;
			for (int i = 0; i < 4; i++) {
				Direction diri = Direction.fromHorizontal(i);
				if (world.getBlockState(pos.offset(diri, 2).up()).getBlock() instanceof MazeBlock)
					continue;
				dir = diri;
				break;
			}
			if (dir == null)
				return;
			pos = pos.offset(dir, 5).up();
			int count = world.getEntitiesByType(EntityType.DROWNED, new Box(pos).offset(0, 1, 0).expand(2),
					Predicates.alwaysTrue()).size();
			if (count > 0)
				return;
			DrownedEntity e = EntityType.DROWNED.create(world);
			e.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
			e.initialize(world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
			hand(e, r);
			addEnc(BIReg.I_DOM_HELMET, e, r, EquipmentSlot.HEAD);
			addEnc(BIReg.I_DOM_CHESTPLATE, e, r, EquipmentSlot.CHEST);
			addEnc(BIReg.I_DOM_LEGGINGS, e, r, EquipmentSlot.LEGS);
			addEnc(BIReg.I_DOM_BOOTS, e, r, EquipmentSlot.FEET);
			e.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 2, false, false));
			e.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6000, 3, false, false));
			e.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 6000, 1, false, false));
			world.spawnEntity(e);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.POWERED, false);
		}

		private void hand(DrownedEntity e, Random r) {
			if (r.nextBoolean()) {
				ItemStack is = new ItemStack(Items.TRIDENT);
				is.addEnchantment(OceanMaze.TRIDENT_SHARP, r.nextInt(4) + 1);
				e.equipStack(EquipmentSlot.MAINHAND, is);
				e.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.22f);
			} else {
				ItemStack is = new ItemStack(Items.IRON_SWORD);
				is.addEnchantment(Enchantments.SHARPNESS, 5);
				e.equipStack(EquipmentSlot.MAINHAND, is);
				e.setEquipmentDropChance(EquipmentSlot.MAINHAND, 1f);
			}
			if (r.nextBoolean()) {
				ItemStack is = new ItemStack(BIReg.I_TRIDENT_BOW);
				is.addEnchantment(OceanMaze.TRIDENT_BOW, r.nextInt(2) + 1);
				e.equipStack(EquipmentSlot.OFFHAND, is);
				e.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.22f);
			} else {
				e.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
				e.setEquipmentDropChance(EquipmentSlot.OFFHAND, 1f);
			}
		}

		private void addEnc(Item i, DrownedEntity e, Random r, EquipmentSlot s) {
			ItemStack is = new ItemStack(i);
			if (r.nextBoolean())
				is.addEnchantment(OceanMaze.SPONGE_PROT, r.nextInt(2) + 1);
			else
				is.addEnchantment(Enchantments.PROTECTION, 4);
			is.addEnchantment(Enchantments.THORNS, 3);
			if (r.nextBoolean()) {
				is.addEnchantment(Enchantments.BINDING_CURSE, 1);
				e.setEquipmentDropChance(s, 1f);
			} else
				e.setEquipmentDropChance(s, 0.22f);
			e.equipStack(s, is);
		}

	}

	public static class RandomTick implements IRandomTick {

		public static final ImmutableList<EntityType<? extends MobEntity>> LIST = ImmutableList
				.of(EntityType.ELDER_GUARDIAN);

		@Override
		public boolean randomTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			convert(LIST.get(r.nextInt(LIST.size())), 4, world, pos, r);
			return false;
		}

		public <T extends MobEntity> void convert(EntityType<T> type, int size, ServerWorld world, BlockPos pos,
				Random r) {
			List<T> le = world.getEntitiesByType(type, new Box(pos).expand(32), (e) -> true);
			if (le.size() > size)
				return;
			List<GuardianEntity> lg = world.getEntitiesByType(EntityType.GUARDIAN, new Box(pos).expand(10),
					(e) -> true);
			GuardianEntity ge = lg.get(r.nextInt(lg.size()));
			ge.remove();
			T ege = type.create(world);
			ege.refreshPositionAndAngles(ge.getX(), ge.getY(), ge.getZ(), ge.yaw, ge.pitch);
			ege.initialize(world, world.getLocalDifficulty(pos), SpawnReason.CONVERSION, null, null);
			if (ge.hasCustomName()) {
				ege.setCustomName(ge.getCustomName());
				ege.setCustomNameVisible(ge.isCustomNameVisible());
			}
			ege.setPersistent();
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 3, false, false));
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 1, false, false));
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 3, false, false));
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6000, 1, false, false));
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 6000, 0, false, false));
			ege.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 6000, 1, false, false));
			world.spawnEntityAndPassengers(ege);
		}

	}

	public static final Neighbor NEI = new Neighbor();
	public static final DireState HOR = new DireState();
	public static final AllDireState ALL_DIRE_STATE = new AllDireState();
	public static final FloorProt FLOOR = new FloorProt();
	public static final Spawner SPAWNER = new Spawner();
	public static final Click CLICK = new Click();
	public static final RandomTick RANDOM = new RandomTick();

	public static final int DELAY = 4;

	public MazeBlock(BlockProp p, IImpl... impl) {
		super(p, impl);
	}

}
