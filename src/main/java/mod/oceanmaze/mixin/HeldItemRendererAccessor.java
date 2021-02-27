package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Mixin(HeldItemRenderer.class)
public interface HeldItemRendererAccessor {

	@Invoker("renderMapInBothHands")
	public void invokeRenderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
			float pitch, float equipProgress, float swingProgress);

	@Invoker("renderMapInOneHand")
	public void invokeRenderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
			float equipProgress, Arm arm, float swingProgress, ItemStack stack);

	@Accessor("offHand")
	public ItemStack getOffHand();

}
