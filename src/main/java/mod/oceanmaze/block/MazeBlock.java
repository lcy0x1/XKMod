package mod.oceanmaze.block;

import java.util.Random;

import com.google.common.base.Predicates;

import mod.lcy0x1.block.BaseBlock;
import mod.lcy0x1.block.BlockProp;
import mod.oceanmaze.main.OceanMaze;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MazeBlock extends BaseBlock {

	public static final Neighbor NEIGHBOR = new Neighbor();
	public static final DireState HOR_DIRE_STATE = new DireState();
	public static final AllDireState ALL_DIRE_STATE = new AllDireState();
	public static final FloorProt FLOOR_PROT = new FloorProt();
	public static final Fatigue FATIGUE = new Fatigue();
	public static final Spawner SPAWNER = new Spawner();

	public static final int DELAY = 2;

	public MazeBlock(BlockProp p, IImpl... impl) {
		super(p, impl);
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

	public static class FloorProt implements IScheduledTick {

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = OceanMaze.B_OMC_FLOOR.getDefaultState();
			for (int dir = 0; dir < 4; dir++) {
				BlockPos uppos = pos.offset(Direction.fromHorizontal(dir));
				BlockState torep = world.getBlockState(uppos);
				if (!torep.getMaterial().isReplaceable())
					continue;
				world.setBlockState(uppos, rep);
			}
		}

	}

	public static class DireState implements IState, IScheduledTick {

		public static final BooleanProperty[] PROPS = { Properties.NORTH, Properties.SOUTH, Properties.EAST,
				Properties.WEST };

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(PROPS);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			for (BooleanProperty bp : PROPS)
				bs = bs.with(bp, true);
			return bs;
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = OceanMaze.B_OMC_WALL.getDefaultState();
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

	}

	public static class Fatigue implements IEntityCollision {

		@Override
		public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
			if (world.isClient())
				return;
			if (entity instanceof PlayerEntity) {
				PlayerEntity le = (PlayerEntity) entity;
				le.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 2, 6000));
			}
		}

	}

	public static class Spawner implements INeighbor, IScheduledTick, IState {

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			boolean pow = world.isReceivingRedstonePower(pos);
			if (state.get(Properties.POWERED) != pow)
				world.setBlockState(pos, state.with(Properties.POWERED, pow));
			if (!pow)
				return;
			int count = world.getEntitiesByType(EntityType.DROWNED, new Box(pos).offset(0, 2, 0).expand(2),
					Predicates.alwaysTrue()).size();
			if (count > 0)
				return;
			DrownedEntity e = new DrownedEntity(EntityType.DROWNED, world);
			e.setPos(pos.getX(), pos.getY() + 2, pos.getZ());
			e.initialize(world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
			ItemStack trident = new ItemStack(Items.TRIDENT);
			e.equipStack(EquipmentSlot.MAINHAND, trident);
			ItemStack helmet = new ItemStack(OceanMaze.I_DEEP_OCEAN_METAL_HELMET);
			helmet.addEnchantment(Enchantments.PROTECTION, 4);
			helmet.addEnchantment(Enchantments.THORNS, 3);
			e.equipStack(EquipmentSlot.HEAD, helmet);
			ItemStack chestplate = new ItemStack(OceanMaze.I_DEEP_OCEAN_METAL_CHESTPLATE);
			chestplate.addEnchantment(Enchantments.PROTECTION, 4);
			chestplate.addEnchantment(Enchantments.THORNS, 3);
			e.equipStack(EquipmentSlot.CHEST, chestplate);
			ItemStack leggings = new ItemStack(OceanMaze.I_DEEP_OCEAN_METAL_LEGGINGS);
			leggings.addEnchantment(Enchantments.PROTECTION, 4);
			leggings.addEnchantment(Enchantments.THORNS, 3);
			e.equipStack(EquipmentSlot.LEGS, leggings);
			ItemStack boots = new ItemStack(OceanMaze.I_DEEP_OCEAN_METAL_BOOTS);
			boots.addEnchantment(Enchantments.PROTECTION, 4);
			boots.addEnchantment(Enchantments.THORNS, 3);
			e.equipStack(EquipmentSlot.FEET, boots);
			world.spawnEntity(e);
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
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(Properties.POWERED);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.POWERED, false);
		}

	}

	public static class AllDireState implements IState, IScheduledTick {

		public static final BooleanProperty[] PROPS = { Properties.DOWN, Properties.UP, Properties.NORTH,
				Properties.SOUTH, Properties.WEST, Properties.EAST };

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(PROPS);
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			for (BooleanProperty bp : PROPS)
				bs = bs.with(bp, false);
			return bs;
		}

		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
			BlockState rep = OceanMaze.B_OMO_WALL.getDefaultState();
			for (BooleanProperty bp : PROPS)
				rep = rep.with(bp, state.get(bp));
			for (int i = 0; i < 6; i++) {
				if (!state.get(PROPS[i]))
					continue;
				BlockPos uppos = pos.offset(Direction.values()[i]);
				if (World.isOutOfBuildLimitVertically(uppos))
					continue;
				BlockState torep = world.getBlockState(uppos);
				if (torep.getMaterial().isReplaceable())
					world.setBlockState(uppos, rep);
				else if (torep.getBlock() == OceanMaze.B_OMO_WALL) {
					BlockState target = torep;
					for (BooleanProperty bp : PROPS)
						target = target.with(bp, target.get(bp) && rep.get(bp));
					if (target != torep)
						world.setBlockState(uppos, target);
				}
			}
		}

	}

}