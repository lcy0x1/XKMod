package mod.xinke.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class NBTManager {

	public CompoundTag tag;

	public NBTManager(CompoundTag tag) {
		this.tag = tag;
	}

	public BlockPos getBlockPos(String key) {
		if (!hasTag(key))
			return null;
		CompoundTag sub = tag.getCompound(key);
		return new BlockPos(sub.getInt("x"), sub.getInt("y"), sub.getInt("z"));
	}

	public ItemStack getStack(String key) {
		if (!hasTag(key))
			return ItemStack.EMPTY;
		return ItemStack.fromTag(tag.getCompound(key));
	}

	public boolean hasTag(String key) {
		return tag.contains(key);
	}

	public void putBlockPos(String key, BlockPos pos) {
		if (pos == null)
			return;
		CompoundTag sub = new CompoundTag();
		sub.putInt("x", pos.getX());
		sub.putInt("y", pos.getY());
		sub.putInt("z", pos.getZ());
		tag.put(key, sub);
	}

	public void putStack(String key, ItemStack is) {
		tag.put(key, is.toTag(new CompoundTag()));
	}

}
