package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKNodeEntity extends AbstractXKECBlockEntity<XKNodeEntity> {

	@SerialField
	public BlockPos core;

	public XKNodeEntity() {
		super(CTESReg.BET_XK_NODE);
	}

	@Override
	public void activate() {
	}

}
