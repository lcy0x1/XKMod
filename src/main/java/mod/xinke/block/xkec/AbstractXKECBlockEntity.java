package mod.xinke.block.xkec;

import mod.xinke.block.BaseBlockEntity;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;

@SerialClass
public abstract class AbstractXKECBlockEntity<T extends AbstractXKECBlockEntity<T>> extends BaseBlockEntity<T>
		implements Inventory, BlockEntityClientSerializable {

	@SerialField(toClient = true)
	public ItemStack inv = ItemStack.EMPTY;

	protected AbstractXKECBlockEntity(BlockEntityType<T> blockEntityType) {
		super(blockEntityType);
	}

	public abstract void activate(PlayerEntity pl);

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world.getBlockEntity(this.pos) != this)
			return false;
		return player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
				this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear() {
		inv = ItemStack.EMPTY;
		markDirty();
		updateState();
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public ItemStack getStack(int slot) {
		return inv;
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	public void onDestroy() {
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack is = inv;
		inv = ItemStack.EMPTY;
		markDirty();
		updateState();
		return is;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (amount == 0)
			return ItemStack.EMPTY;
		ItemStack is = inv;
		inv = ItemStack.EMPTY;
		markDirty();
		updateState();
		return is;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv = stack.split(1);
		markDirty();
		updateState();
	}

	@Override
	public int size() {
		return 1;
	}

	public void updateState() {
		BlockState bs = getWorld().getBlockState(getPos()).with(Properties.LIT, inv == null || inv.isEmpty());
		getWorld().setBlockState(getPos(), bs);
	}

}
