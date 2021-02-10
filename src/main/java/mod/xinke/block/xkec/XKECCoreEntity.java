package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKECCoreEntity extends AbstractXKECBlockEntity<XKECCoreEntity> {

	public XKECCoreEntity() {
		super(CTESReg.BET_XKEC_CORE);
	}

	@Override
	public void activate() {
	}

}
