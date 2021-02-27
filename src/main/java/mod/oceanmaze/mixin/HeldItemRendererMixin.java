package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.oceanmaze.main.OceanMaze;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	@Inject(at = @At("HEAD"), method = 
			"renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;"
			+ "FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;"
			+ "FLnet/minecraft/client/util/math/MatrixStack;"
			+ "Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
			float sp, ItemStack item, float ep, MatrixStack mat, VertexConsumerProvider vc, int light,
			CallbackInfo info) {
		HeldItemRendererAccessor self = (HeldItemRendererAccessor) this;
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
		System.out.println(item.getTag());
		if (item.getItem() == OceanMaze.I_MAZE_MAP && item.hasTag()) {
			mat.push();
			if (bl && self.getOffHand().isEmpty())
				self.invokeRenderMapInBothHands(mat, vc, light, pitch, ep, sp);
			else
				self.invokeRenderMapInOneHand(mat, vc, light, ep, arm, sp, item);
			mat.pop();
		}
	}

}
