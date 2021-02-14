package mod.xinke.block.main;

import java.util.List;

import mod.xinke.block.BaseBlock.BaseBlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import mod.xinke.block.BlockProp;
import mod.xinke.block.CTESReg;
import mod.xinke.block.InvBlockEntity;
import mod.xinke.block.InvContainer;
import mod.xinke.main.XinkeMod;
import mod.xinke.util.NBTManager;
import mod.xinke.util.SerialClass;

public class TeleportBlock extends BaseBlockWithEntity {

	public static class Cont extends InvContainer<Cont> {

		public Cont(int syncId, PlayerInventory plInv) {
			this(syncId, plInv, new SimpleInventory(INV_SIZE));
		}

		public Cont(int syncId, PlayerInventory plInv, Inventory inv) {
			super(CTESReg.SHT_TELE, syncId, plInv, inv);
		}

	}

	@SerialClass
	public static class TE extends InvBlockEntity<TE> implements SidedInventory, Tickable {

		public TE() {
			super(CTESReg.BET_TELE, INV_SIZE);
		}

		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return dir != Direction.UP && (slot == SLOT_BIND || slot == SLOT_BIND_OUT);
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack, Direction dir) {
			return dir != Direction.UP && isValid(slot, stack);
		}

		@Override
		public int[] getAvailableSlots(Direction side) {
			return new int[] { 3, 4 };
		}

		public ItemStack getBind() {
			ItemStack ans = new ItemStack(XinkeMod.I_TELE_BIND);
			NBTManager nbt = new NBTManager(ans.getTag());
			nbt.putBlockPos("pos", getPos());
			nbt.tag.putString("dim", getWorld().getRegistryKey().getValue().toString());
			return ans;
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			if (slot == SLOT_CHARGE)
				return !getWorld().getBlockState(getPos()).get(Properties.LIT)
						&& stack.getItem() == XinkeMod.I_TELE_CHARGE;
			if (slot == SLOT_BIND_IN)
				return stack.getItem() == XinkeMod.I_TELE_CHARGE || stack.getItem() == XinkeMod.I_TELE_BIND;
			if (slot == SLOT_ACTIVATE)
				return stack.getItem() == Items.ENDER_PEARL;
			return stack.getItem() == XinkeMod.I_TELE_BIND;
		}

		@Override
		public void tick() {
			if (getWorld().isClient())
				return;
			boolean charged = getWorld().getBlockState(getPos()).get(Properties.LIT);
			if (!getStack(SLOT_CHARGE).isEmpty() && getStack(SLOT_BIND_OUT).isEmpty()) {
				getWorld().setBlockState(getPos(), XinkeMod.B_TELE.getDefaultState().with(Properties.LIT, true));
				removeStack(SLOT_CHARGE);
				setStack(SLOT_BIND_OUT, getBind());
			}
			if (charged && !getStack(SLOT_BIND_IN).isEmpty() && getStack(SLOT_BIND_OUT).isEmpty()) {
				removeStack(SLOT_BIND_IN);
				setStack(SLOT_BIND_OUT, getBind());
			}
			if (charged && !getStack(SLOT_ACTIVATE).isEmpty() && !getStack(SLOT_BIND).isEmpty()) {
				List<LivingEntity> liv = getWorld().getEntitiesByClass(LivingEntity.class, new Box(getPos().up()),
						(e) -> true);
				int max = Math.min(getStack(SLOT_ACTIVATE).getCount(), liv.size());
				NBTManager nbt = new NBTManager(getStack(SLOT_BIND).getTag());
				BlockPos pos = nbt.getBlockPos("pos");
				Identifier dim = new Identifier(nbt.tag.getString("dim"));
				ServerWorld w = null;
				if (!getWorld().getRegistryKey().getValue().equals(dim))
					w = ((ServerWorld) getWorld()).getServer().getWorld(RegistryKey.of(Registry.DIMENSION, dim));
				for (int i = 0; i < max; i++) {
					if (w != null)
						liv.get(i).moveToWorld(w);
					liv.get(i).requestTeleport(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
				}
			}
		}

	}

	public static class TeleState implements IState, ILight {

		public static final TeleState INSTANCE = new TeleState();

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(Properties.LIT);
		}

		@Override
		public int getLightValue(BlockState bs) {
			return bs.get(Properties.LIT) ? 15 : 0;
		}

	}

	public static final int INV_SIZE = 5;
	public static final int SLOT_CHARGE = 0;
	public static final int SLOT_BIND_IN = 1;
	public static final int SLOT_BIND_OUT = 2;
	public static final int SLOT_BIND = 3;
	public static final int SLOT_ACTIVATE = 4;

	public TeleportBlock(BlockProp p, STE ste) {
		super(p, ste, TeleState.INSTANCE);
		setDefaultState(getDefaultState().with(Properties.LIT, false));
	}

}
