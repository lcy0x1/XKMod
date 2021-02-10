package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKECSideEntity extends AbstractXKECBlockEntity<XKECSideEntity> {

	@SerialField
	public BlockPos core;

	public XKECSideEntity() {
		super(CTESReg.BET_XKEC_SIDE);
	}

	@Override
	public void activate() {
	}

}
