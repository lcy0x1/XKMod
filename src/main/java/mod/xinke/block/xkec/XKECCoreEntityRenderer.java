package mod.xinke.block.xkec;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class XKECCoreEntityRenderer extends XKECBlockEntityRenderer<XKECCoreEntity> {

	public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

	private static final int[] HUE = { 0, 30, 60, 120, 180, 240, 300 };

	public XKECCoreEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(XKECCoreEntity core, float dt, MatrixStack mat, VertexConsumerProvider vc, int light,
			int overlay) {
		super.render(core, dt, mat, vc, light, overlay);
		float time = Math.floorMod(core.getWorld().getTime(), 80L) + dt;
		mat.push();
		mat.translate(0.5D, 0.5D, 0.5D);
		BeamRenderer br = new BeamRenderer();
		int color = -1;
		int prev = -1;
		if (core.conn != null)
			for (BlockPos i : core.conn) {
				BlockPos p = i.subtract(core.getPos());
				int x = p.getX();
				int y = p.getY();
				int z = p.getZ();
				int sqdis = x * x + z * z + y * y;
				if (sqdis != prev) {
					prev = sqdis;
					color++;
				}
				br.setColor(-HUE[color] / 360f);
				renderLightBeam(mat, vc, BEAM_TEXTURE, time, br, x, y, z);
			}
		mat.pop();
	}

}