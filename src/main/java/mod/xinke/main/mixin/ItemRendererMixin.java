package mod.xinke.main.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import mod.xinke.item.XinkeEnergyItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("TAIL"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void renderGuiItemOverlay(TextRenderer tr, ItemStack stack, int x, int y, String str, CallbackInfo info) {
		ItemRendererAccessor self = (ItemRendererAccessor) this;
		if (XinkeEnergyItem.getMaxEnergy(stack) > 0) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			float f = XinkeEnergyItem.getEnergy(stack);
			float g = XinkeEnergyItem.getMaxEnergy(stack);
			int i = Math.round(f * 13.0F / g);
			self.invokeRenderGuiQuad(bufferBuilder, x + 2, y + 12, 13, 1, 255, 128, 128, 255);
			self.invokeRenderGuiQuad(bufferBuilder, x + 2, y + 12, i, 1, 255, 0, 0, 255);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}
	}

}
