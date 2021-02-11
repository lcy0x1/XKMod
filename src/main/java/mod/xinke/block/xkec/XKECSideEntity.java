package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKECSideEntity extends AbstractXKECBlockEntity<XKECSideEntity> {

	@SerialField
	public BlockPos core;

	public XKECSideEntity() {
		super(CTESReg.BET_XKEC_SIDE);
	}

	@Override
	public void activate(PlayerEntity pl) {
	}

	public void disConnect() {
		core = null;
		markDirty();
	}

	@Override
	public void onDestroy() {
		System.out.println("on destroy: " + core);// TODO
		if (getWorld().isClient() || core == null)
			return;
		BlockEntity be = getWorld().getBlockEntity(core);
		if (be instanceof XKECCoreEntity) {
			XKECCoreEntity ce = (XKECCoreEntity) be;
			ce.disConnect(getPos());
		}
	}

	public void setConnected(BlockPos pos) {
		if (pos.equals(core))
			return;
		if (core != null) {
			BlockEntity be = this.getWorld().getBlockEntity(core);
			if (be instanceof XKECCoreEntity) {
				XKECCoreEntity ce = (XKECCoreEntity) be;
				ce.disConnect(getPos());
			}
		}
		core = pos;
		markDirty();
	}

}
