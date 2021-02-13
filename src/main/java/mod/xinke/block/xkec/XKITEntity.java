package mod.xinke.block.xkec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import mod.xinke.block.CTESReg;
import mod.xinke.main.XinkeMod;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SerialClass
public class XKITEntity extends AbstractXKECBlockEntity<XKITEntity> implements Tickable {

	public static class ItemToken {

		public XKITEntity source;
		public Inventory inv;
		public int slot;
		public ItemStack stack;
		public boolean fullfilled;

		public ItemToken(XKITEntity source, Inventory inv, int slot, ItemStack stack) {
			this.source = source;
			this.inv = inv;
			this.slot = slot;
			this.stack = stack;
			fullfilled = false;
		}

		public ItemStack fullfill(int i) {
			if (source.source != this) {
				fullfilled = true;
				return null;
			}
			source.source = null;
			if (!ItemStack.areItemsEqual(stack, inv.getStack(slot))
					|| !ItemStack.areTagsEqual(stack, inv.getStack(slot))) {
				fullfilled = true;
				source.source = null;
				return null;
			}
			ItemStack ans = inv.removeStack(slot, Math.min(i, inv.getStack(slot).getCount()));
			if (inv.getStack(slot).isEmpty()) {
				fullfilled = true;
				source.source = null;
			}
			return ans;
		}

		public void kill() {
			source.source = null;
		}

		@Override
		public String toString() {
			return "ItemToken from " + inv.getClass() + " at slot " + slot + " with item {" + stack + "} fullfilled = "
					+ fullfilled;
		}

	}

	public static final double FACTOR = 0.02;
	public static final double THRESHOD = 1e-3;
	public static final int COOLDOWN = 4;

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory) inv).canExtract(slot, stack, facing);
	}

	private static boolean canInsert(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return inv.isValid(slot, stack) && !(inv instanceof SidedInventory)
				|| ((SidedInventory) inv).canInsert(slot, stack, facing);
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
			return false;
		} else if (first.getCount() > first.getMaxCount()) {
			return false;
		} else {
			return ItemStack.areTagsEqual(first, second);
		}
	}

	private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
		return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory) inventory).getAvailableSlots(side))
				: IntStream.range(0, inventory.size());
	}

	private static void transfer(Inventory to, ItemToken token, int slot, Direction direction) {
		ItemStack itemStack = to.getStack(slot);
		if (canInsert(to, token.stack, slot, direction)) {
			int max = Math.min(token.stack.getMaxCount(), to.getMaxCountPerStack());
			if (itemStack.isEmpty()) {
				int min = Math.min(max, token.stack.getCount());
				ItemStack tok = token.fullfill(min);
				if (tok != null) {
					to.setStack(slot, tok);
					to.markDirty();
				}
			} else if (canMergeItems(itemStack, token.stack)) {
				int i = max - itemStack.getCount();
				if (i <= 0)
					return;
				int j = Math.min(i, token.stack.getCount());
				ItemStack tok = token.fullfill(j);
				if (tok != null) {
					itemStack.increment(j);
					to.markDirty();
				}

			}
		}
	}

	@SerialField(toClient = true)
	public BlockPos[] conn = new BlockPos[0];

	@SerialField(toClient = true)
	public UUID playerID = null;

	public ItemToken source = null, token = null;

	@SerialField
	public int cooldown = 0;

	/**
	 * don't send to client, as it updates simultaneously on client, and only plays
	 * visual effects there
	 */
	@SerialField
	public double temperature = 0.5;

	public XKITEntity() {
		super(CTESReg.BET_XKIT);
	}

	@Override
	public void activate(PlayerEntity pl) {
		if (pl == null)
			return;
		if (playerID == null) {
			BlockPos near = getNearby(pl.getUuid());
			if (near == null) {
				playerID = pl.getUuid();
			} else {
				BlockEntity be = getWorld().getBlockEntity(near);
				if (be instanceof XKITEntity) {
					XKITEntity xkit = (XKITEntity) be;
					xkit.finishConn();
					finishConn();
					List<BlockPos> list = new ArrayList<>();
					for (BlockPos p : conn)
						list.add(p);
					if (list.contains(near)) {
						disConnect(near);
						xkit.disConnect(getPos());
					} else {
						setConnect(near);
						xkit.setConnect(getPos());
					}
				}
			}
			markDirty();
		} else if (playerID.equals(pl.getUuid())) {
			playerID = null;
			markDirty();
		}
		sync();
	}

	public void disConnect(BlockPos pos) {
		List<BlockPos> list = new ArrayList<>();
		for (BlockPos p : conn)
			list.add(p);
		list.remove(pos);
		conn = list.toArray(new BlockPos[0]);
		markDirty();
		sync();
	}

	public void finishConn() {
		playerID = null;
		markDirty();
		sync();
	}

	@Override
	public void onDestroy() {
		if (getWorld().isClient())
			return;
		for (BlockPos p : conn) {
			BlockEntity be = getWorld().getBlockEntity(p);
			if (be instanceof XKITEntity) {
				XKITEntity se = (XKITEntity) be;
				se.disConnect(getPos());
			}
		}
		if (token != null)
			token.kill();
	}

	public void setConnect(BlockPos pos) {
		List<BlockPos> list = new ArrayList<>();
		for (BlockPos p : conn)
			list.add(p);
		list.add(pos);
		conn = list.toArray(new BlockPos[0]);
		markDirty();
		sync();
	}

	@Override
	public void tick() {
		validatePlayer();
		if (conn == null)
			return;
		updateTemperature();
		updateItemMove();
	}

	private void extract() {
		if (cooldown > 0)
			cooldown--;
		if (cooldown > 0)
			return;
		for (Direction d : Direction.values()) {
			BlockPos target = getPos().offset(d);
			BlockEntity be = getWorld().getBlockEntity(target);
			if (be instanceof Inventory && !(be instanceof XKITEntity)) {
				Inventory i = (Inventory) be;
				if (getAvailableSlots(i, d.getOpposite()).anyMatch((slot) -> {
					ItemStack itemStack = i.getStack(slot);
					if (itemStack.isEmpty())
						return false;
					if (!isItemValid(itemStack))
						return false;
					if (canExtract(i, itemStack, slot, d.getOpposite())) {
						source = token = new ItemToken(this, i, slot, itemStack);
						cooldown = COOLDOWN;
						return true;
					}
					return false;
				}))
					break;
			}
		}
	}

	private BlockPos getNearby(UUID id) {
		BlockPos.Mutable bs = new BlockPos.Mutable();
		int range = 16;
		BlockPos self = getPos();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					int sqdis = i * i + j * j + k * k;
					if (sqdis > range * range)
						continue;
					bs.set(self.getX() + i, self.getY() + j, self.getZ() + k);
					BlockEntity be = this.getWorld().getBlockEntity(bs);
					if (be instanceof XKITEntity) {
						XKITEntity xkit = (XKITEntity) be;
						if (xkit.playerID != null && xkit.playerID.equals(id)) {
							return bs.toImmutable();
						}
					}
				}
			}
		}
		return null;
	}

	private void insert() {
		if (cooldown > 0)
			cooldown--;
		if (cooldown > 0)
			return;
		for (Direction d : Direction.values()) {
			BlockPos target = getPos().offset(d);
			BlockEntity be = getWorld().getBlockEntity(target);
			if (be instanceof Inventory && !(be instanceof XKITEntity)) {
				Inventory i = (Inventory) be;
				if (getAvailableSlots(i, d.getOpposite()).anyMatch((slot) -> {
					if (canInsert(i, token.stack, slot, d.getOpposite())) {
						transfer(i, token, slot, d.getOpposite());
						if (token.fullfilled) {
							cooldown = COOLDOWN;
							token = null;
							return true;
						}
					}
					return false;
				}))
					break;
			}
		}
		if (token != null) {
			token.kill();
			token = null;
		}
	}

	private boolean isItemValid(ItemStack item) {
		ItemStack inv = getStack(0);
		if ((inv == null || inv.isEmpty()) && !item.isEmpty())
			return true;
		if (inv.getItem() == Items.SHULKER_BOX && inv.getSubTag("BlockEntityTag") != null) {
			DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
			Inventories.fromTag(inv.getSubTag("BlockEntityTag"), list);
			for (ItemStack is : list)
				if (!is.isEmpty() && is.getItem() == item.getItem())
					return true;
			return false;
		}
		return item.getItem() == inv.getItem();
	}

	private void updateItemMove() {
		if (getWorld().isClient())
			return;
		Block b = getWorld().getBlockState(getPos()).getBlock();
		if (source == null && token == null && b == XinkeMod.B_XKIT_SOURCE)
			extract();
		if (token != null && b == XinkeMod.B_XKIT_TARGET)
			insert();
		if (token != null) {
			int count = 0;
			for (BlockPos pos : conn) {
				BlockEntity be = getWorld().getBlockEntity(pos);
				if (be instanceof XKITEntity) {
					XKITEntity xkit = (XKITEntity) be;
					if (temperature - xkit.temperature < THRESHOD || xkit.token != null)
						continue;
					if (!xkit.isItemValid(token.stack))
						continue;
					count++;
				}
			}
			int ran = (int) (Math.random() * count);
			for (BlockPos pos : conn) {
				BlockEntity be = getWorld().getBlockEntity(pos);
				if (be instanceof XKITEntity) {
					XKITEntity xkit = (XKITEntity) be;
					if (temperature - xkit.temperature < THRESHOD || xkit.token != null)
						continue;
					if (!xkit.isItemValid(token.stack))
						continue;
					if (ran == 0) {
						xkit.token = token;
						token = null;
						break;
					}
					ran--;
				}
			}
		}

	}

	private void updateTemperature() {
		if (conn == null)
			return;
		double temp = temperature;
		for (BlockPos pos : conn) {
			BlockEntity be = getWorld().getBlockEntity(pos);
			if (be instanceof XKITEntity) {
				XKITEntity xkit = (XKITEntity) be;
				double diff = (temp - xkit.temperature) * FACTOR;
				temperature -= diff;
				xkit.temperature += diff;
			}
		}
		Block b = getWorld().getBlockState(getPos()).getBlock();
		if (b == XinkeMod.B_XKIT_SOURCE)
			temperature = 1;
		if (b == XinkeMod.B_XKIT_TARGET)
			temperature = 0;
	}

	private void validatePlayer() {
		if (getWorld().isClient() || playerID == null)
			return;
		PlayerEntity pl = getWorld().getPlayerByUuid(playerID);
		if (pl == null || pl.getEntityWorld() != getWorld() || !getPos().isWithinDistance(pl.getTrackedPosition(), 8)) {
			playerID = null;
			markDirty();
			sync();
		}

	}

}
