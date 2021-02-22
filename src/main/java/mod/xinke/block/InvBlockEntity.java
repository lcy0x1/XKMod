package mod.xinke.block;

import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;

@SerialClass
public abstract class InvBlockEntity<T extends InvBlockEntity<T>> extends BaseBlockEntity<T>
		implements Inventory, NamedScreenHandlerFactory {

	@SerialField
	public ItemStack[] inv;

	public InvBlockEntity(BlockEntityType<T> type, int size) {
		super(type);
		inv = new ItemStack[size];
		for (int i = 0; i < size; i++)
			inv[i] = ItemStack.EMPTY;
		LootableContainerBlockEntity.class.getCanonicalName();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world.getBlockEntity(this.pos) != this)
			return false;
		return player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
				this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inv.length; i++)
			inv[i] = ItemStack.EMPTY;
		markDirty();
	}
	
	@Override
	public ItemStack getStack(int slot) {
		return inv[slot];
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.length; i++)
			if (!inv[i].isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack ins = inv[slot];
		inv[slot] = ItemStack.EMPTY;
		markDirty();
		return ins;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack is = inv[slot];
		int a = Math.min(is.getCount(), amount);
		if (a == is.getCount())
			return removeStack(slot);
		is = is.split(a);
		markDirty();
		return is;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv[slot] = stack;
		markDirty();
	}

	@Override
	public int size() {
		return inv.length;
	}

}
