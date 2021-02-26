package mod.lcy0x1.block;

import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class InvContainer<T extends InvContainer<T>> extends ScreenHandler {

	public static class CondSlot extends Slot {

		public final Predicate<ItemStack> pred;

		public CondSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> pred) {
			super(inventory, index, x, y);
			this.pred = pred;
		}

		@Override
		public boolean canInsert(ItemStack is) {
			return pred.test(is);
		}

	}

	public static class ResultSlot extends Slot {

		public ResultSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack is) {
			return false;
		}

	}

	protected final Inventory inv;

	protected InvContainer(ScreenHandlerType<T> type, int syncId, PlayerInventory plInv, Inventory inv, int h) {
		super(type, syncId);
		this.inv = inv;
		for (int r = 0; r < 3; ++r)
			for (int c = 0; c < 9; ++c)
				addSlot(new Slot(plInv, c + r * 9 + 9, 8 + c * 18, h + r * 18));
		for (int c = 0; c < 9; ++c)
			addSlot(new Slot(plInv, c, 8 + c * 18, h + 58));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return inv.canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();
			if (invSlot < inv.size()) {
				if (!insertItem(originalStack, inv.size(), slots.size(), true))
					return ItemStack.EMPTY;
			} else if (!insertItem(originalStack, 0, inv.size(), false))
				return ItemStack.EMPTY;
			if (originalStack.isEmpty())
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
		}
		return newStack;
	}

}
