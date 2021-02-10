package mod.xinke.block.xkec;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

public class XKECBlockEntityRenderer<T extends AbstractXKECBlockEntity<T>> extends BlockEntityRenderer<T> {

	public XKECBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(T be, float dt, MatrixStack mat, VertexConsumerProvider vc, int light, int overlay) {
		ItemStack is = be.inv;
		if (is.isEmpty())
			return;
		mat.push();
		float time = be.getWorld().getTime() + dt;
		double offset = Math.sin(time / 8.0) / 4.0;
		mat.translate(0.5, 0.5 + offset, 0.5);
		mat.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(time * 4));
		MinecraftClient.getInstance().getItemRenderer().renderItem(is, ModelTransformation.Mode.GROUND, light, overlay,
				mat, vc);
		mat.pop();
	}
}