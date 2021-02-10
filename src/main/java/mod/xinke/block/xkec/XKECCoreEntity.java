package mod.xinke.block.xkec;

import java.util.ArrayList;
import java.util.List;

import mod.xinke.block.CTESReg;
import mod.xinke.main.XinkeMod;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKECCoreEntity extends AbstractXKECBlockEntity<XKECCoreEntity> {

	@SerialField
	public BlockPos[] conn = new BlockPos[0];

	public XKECCoreEntity() {
		super(CTESReg.BET_XKEC_CORE);
	}

	@Override
	public void activate() {
		if (this.getWorld().isClient())
			return;
		BlockPos self = this.getPos();
		Block b = this.getWorld().getBlockState(self).getBlock();
		int range = b == XinkeMod.B_XKEC_CORE_2 ? 16 : b == XinkeMod.B_XKEC_CORE_1 ? 8 : 4;
		BlockPos.Mutable bs = new BlockPos.Mutable();
		List<BlockPos> list = new ArrayList<>();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					if (i * i + j * j + k * k > range * range)
						continue;
					bs.set(self.getX() + i, self.getY() + j, self.getZ() + k);
					BlockEntity be = this.getWorld().getBlockEntity(bs);
					if (be instanceof XKECSideEntity) {
						XKECSideEntity se = (XKECSideEntity) be;
						se.setConnected(self);
						list.add(bs.toImmutable());
					}
				}
			}
		}
		conn = list.toArray(new BlockPos[0]);
		markDirty();
		sync();
	}

	public void disConnect(BlockPos pos) {
		List<BlockPos> list = new ArrayList<>();
		for (BlockPos p : conn)
			list.add(p);
		conn = list.toArray(new BlockPos[0]);
		markDirty();
		sync();
	}

}
