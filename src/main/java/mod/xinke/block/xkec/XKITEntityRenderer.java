package mod.xinke.block.xkec;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class XKITEntityRenderer extends XKECBlockEntityRenderer<XKITEntity> {

	public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

	private static final int HUE_PLAY = 120;

	public XKITEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(XKITEntity xkit, float dt, MatrixStack mat, VertexConsumerProvider vc, int light, int overlay) {
		super.render(xkit, dt, mat, vc, light, overlay);
		float time = Math.floorMod(xkit.getWorld().getTime(), 80L) + dt;
		mat.push();
		mat.translate(0.5D, 0.5D, 0.5D);
		BeamRenderer br = new BeamRenderer();
		float t0 = (float) xkit.temperature;
		br.setColor(t0, t0, t0, 1);
		if (xkit.conn != null)
			for (BlockPos i : xkit.conn) {
				BlockPos p = i.subtract(xkit.getPos());
				BlockEntity be = xkit.getWorld().getBlockEntity(i);
				float t1 = 0.5f;
				if (be instanceof XKITEntity)
					t1 = (float) ((XKITEntity) be).temperature;
				int x = p.getX();
				int y = p.getY();
				int z = p.getZ();
				br.setEndcolor(t1, t1, t1, 1);
				renderLightBeam(mat, vc, BEAM_TEXTURE, time, br, x, y, z);
			}
		if (xkit.playerID != null) {
			PlayerEntity pl = xkit.getWorld().getPlayerByUuid(xkit.playerID);
			if (pl != null && pl.getEntityWorld() == xkit.getWorld()
					&& xkit.getPos().isWithinDistance(pl.getPos(), 8)) {
				br.setColor(-HUE_PLAY / 360f);
				renderLightBeam(mat, vc, BEAM_TEXTURE, time, br, (float) pl.getX(), (float) pl.getY(),
						(float) pl.getZ());
			}
		}
		mat.pop();
	}

}