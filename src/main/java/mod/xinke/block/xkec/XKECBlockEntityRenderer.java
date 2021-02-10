package mod.xinke.block.xkec;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3i;

public class XKECBlockEntityRenderer<T extends AbstractXKECBlockEntity<T>> extends BlockEntityRenderer<T> {

	private static class BeamRenderer {

		private float red, blue, green, alpha;
		private float u0, u1, v0, v1, y0, y1;

		private Matrix3f m3;
		private Matrix4f m4;
		private VertexConsumer vc;

		public BeamRenderer() {

		}

		public BeamRenderer setColor(float r, float b, float g, float a) {
			red = r;
			blue = b;
			green = g;
			alpha = a;
			return this;
		}

		public BeamRenderer setUV(float u0, float u1, float v0, float v1) {
			this.u0 = u0;
			this.u1 = u1;
			this.v0 = v0;
			this.v1 = v1;
			return this;
		}

		public void drawCube(MatrixStack mat, VertexConsumer vc, float y0, float y1, float h) {
			MatrixStack.Entry entry = mat.peek();
			m4 = entry.getModel();
			m3 = entry.getNormal();
			this.vc = vc;
			this.y0 = y0;
			this.y1 = y1;
			drawRect(0, h, h, 0);
			drawRect(h, 0, 0, -h);
			drawRect(0, -h, -h, 0);
			drawRect(-h, 0, 0, h);
		}

		private void drawRect(float x0, float z0, float x1, float z1) {
			drawVertex(y1, x0, z0, u1, v0);
			drawVertex(y0, x0, z0, u1, v1);
			drawVertex(y0, x1, z1, u0, v1);
			drawVertex(y1, x1, z1, u0, v0);
		}

		private void drawVertex(float y, float x, float z, float u, float v) {
			vc.vertex(m4, x, y, z);
			vc.color(red, green, blue, alpha);
			vc.texture(u, v);
			vc.overlay(OverlayTexture.DEFAULT_UV);
			vc.light(15728880);
			vc.normal(m3, 0, 1, 0);
			vc.next();
		}

	}

	public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

	public XKECBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(T be, float dt, MatrixStack mat, VertexConsumerProvider vc, int light, int overlay) {
		float time = Math.floorMod(be.getWorld().getTime(), 40L) + dt;
		ItemStack is = be.inv;
		if (!is.isEmpty()) {
			mat.push();
			double offset = Math.sin(time / 8.0) / 4.0;
			mat.translate(0.5, 0.5 + offset, 0.5);
			mat.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(time * 4));
			MinecraftClient.getInstance().getItemRenderer().renderItem(is, ModelTransformation.Mode.GROUND, light,
					overlay, mat, vc);
			mat.pop();
		}
		if (be instanceof XKECCoreEntity) {
			XKECCoreEntity core = (XKECCoreEntity) be;
			mat.push();
			mat.translate(0.5D, 0.5D, 0.5D);
			BeamRenderer br = new BeamRenderer();
			for (BlockPos i : core.conn) {
				renderLightBeam(mat, vc, BEAM_TEXTURE, time, br, i.subtract(core.getPos()));
			}
			mat.pop();
		}
	}

	public static void renderLightBeam(MatrixStack mat, VertexConsumerProvider vcp, Identifier identifier, float t1,
			BeamRenderer br, Vec3i pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		float xz = (float) Math.sqrt(x * x + z * z);
		float len = (float) Math.sqrt(pos.getSquaredDistance(Vec3i.ZERO));
		mat.push();
		mat.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float) Math.atan2(z, x)));
		mat.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion((float) Math.atan2(y, xz)));
		mat.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(t1 * 2.25F - 45.0F));
		br.setUV(0, 1, 0, len);
		br.setColor(1, 0, 0, 1);
		br.drawCube(mat, vcp.getBuffer(RenderLayer.getBeaconBeam(identifier, false)), 0, len, 0.2f);
		mat.pop();
	}
}