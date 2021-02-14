package mod.xinke.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class InvContainer<T extends InvContainer<T>> extends ScreenHandler {

	protected final Inventory inv;

	protected InvContainer(ScreenHandlerType<T> type, int syncId, PlayerInventory plInv, Inventory inv) {
		super(type, syncId);
		this.inv = inv;

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
