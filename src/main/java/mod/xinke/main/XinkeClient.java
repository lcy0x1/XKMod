package mod.xinke.main;

import mod.xinke.block.CTESReg;
import mod.xinke.block.main.TeleportBlock;
import mod.xinke.block.xkec.XKECBlockEntityRenderer;
import mod.xinke.block.xkec.XKECCoreEntityRenderer;
import mod.xinke.block.xkec.XKITEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;

public class XinkeClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		XinkeMod.isPhysicalClient = true;
		
		BlockEntityRendererRegistry.INSTANCE.register(CTESReg.BET_XKEC_SIDE, XKECBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(CTESReg.BET_XK_NODE, XKECBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(CTESReg.BET_XKEC_CORE, XKECCoreEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(CTESReg.BET_XKIT, XKITEntityRenderer::new);

		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_BLADE_CROP, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XK_NODE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKEC_CORE_0, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKEC_CORE_1, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKEC_CORE_2, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKEC_SIDE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKIT_SOURCE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKIT_MIDDLE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(XinkeMod.B_XKIT_TARGET, RenderLayer.getCutout());
		

        ScreenRegistry.register(CTESReg.SHT_TELE, TeleportBlock.Scr::new);
	}

}
