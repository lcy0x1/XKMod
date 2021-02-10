package mod.xinke.main;

import mod.xinke.block.CTESReg;
import mod.xinke.block.xkec.XKECBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class XinkeClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(CTESReg.BET_XKEC_SIDE, XKECBlockEntityRenderer::new);
	}

}
