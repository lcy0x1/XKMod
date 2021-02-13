package mod.xinke.main.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

	@Invoker("renderGuiQuad")
	public void invokeRenderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green,
			int blue, int alpha);

}
