package mod.xinke.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class XKSteelPickaxe extends PickaxeItem implements XinkeEnergyItem {

	private static boolean chain(World world, BlockPos pos, ServerPlayerEntity player) {
		BlockState blockState = world.getBlockState(pos);
		if (!player.getMainHandStack().getItem().canMine(blockState, world, pos, player)) {
			return false;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock)
					&& !player.isCreativeLevelTwoOp()) {
				world.updateListeners(pos, blockState, blockState, 3);
				return false;
			} else if (player.isBlockBreakingRestricted(world, pos,
					player.abilities.creativeMode ? GameMode.CREATIVE : GameMode.SURVIVAL)) {
				return false;
			} else {
				block.onBreak(world, pos, blockState, player);
				boolean bl = world.removeBlock(pos, false);
				if (bl) {
					block.onBroken(world, pos, blockState);
				}

				if (player.abilities.creativeMode) {
					return true;
				} else {
					ItemStack itemStack = player.getMainHandStack();
					ItemStack itemStack2 = itemStack.copy();
					boolean bl2 = player.isUsingEffectiveTool(blockState);
					if (bl && bl2) {
						block.afterBreak(world, player, pos, blockState, blockEntity, itemStack2);
					}
					return true;
				}
			}
		}
	}

	private static void chainAll(ItemStack stack, BlockState state, BlockPos pos, World world, ServerPlayerEntity miner,
			int radius) {
		int max = XinkeEnergyItem.getEnergy(stack);
		BlockPos.Mutable m = new BlockPos.Mutable();
		for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++)
				for (int k = -radius; k <= radius; k++) {
					if (max == 0)
						return;
					if (i == 0 && j == 0 && k == 0)
						continue;
					m.set(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					if (world.getBlockState(m).getBlock() != state.getBlock())
						continue;
					if (chain(world, m.toImmutable(), miner))
						max--;
				}
		XinkeEnergyItem.setEnergy(stack, max);
	}

	public XKSteelPickaxe(Settings settings) {
		super(XKSteelToolMaterial.INSTANCE, 2, -2.4f, settings);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		super.postMine(stack, world, state, pos, miner);
		if (miner instanceof ServerPlayerEntity)
			chainAll(stack, state, pos, world, (ServerPlayerEntity) miner, 1);
		return true;
	}

}
