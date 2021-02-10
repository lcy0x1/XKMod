package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKITEntity extends AbstractXKECBlockEntity<XKITEntity> {

	@SerialField
	public BlockPos[] conn = new BlockPos[0];
	
	public XKITEntity() {
		super(CTESReg.BET_XKIT);
	}

	@Override
	public void activate() {
	}

}
