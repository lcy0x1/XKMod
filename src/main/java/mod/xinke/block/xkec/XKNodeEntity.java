package mod.xinke.block.xkec;

import mod.xinke.block.CTESReg;
import mod.xinke.util.SerialClass;

@SerialClass
public class XKNodeEntity extends AbstractXKECBlockEntity<XKNodeEntity> {

	public XKNodeEntity() {
		super(CTESReg.BET_XK_NODE);
	}

	@Override
	public void activate() {
	}

}
