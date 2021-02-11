package mod.xinke.block;

import mod.xinke.util.Automator;
import mod.xinke.util.ExceptionHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public class BaseBlockEntity<T extends BaseBlockEntity<T>> extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<T> type) {
		super(type);
	}

	public void fromClientTag(CompoundTag tag) {
		ExceptionHandler.get(() -> Automator.fromTag(tag, getClass(), this, f -> f != null && f.toClient()));
	}

	@Override
	public final void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		ExceptionHandler.get(() -> Automator.fromTag(tag, getClass(), this, f -> f != null));
	}

	public CompoundTag toClientTag(CompoundTag tag) {
		return ExceptionHandler.get(() -> Automator.toTag(tag, getClass(), this, f -> f != null && f.toClient()));
	}

	@Override
	public final CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		ExceptionHandler.get(() -> Automator.toTag(tag, getClass(), this, f -> f != null));
		return tag;
	}

}
