package mod.xinke.enchantment;

import java.util.Iterator;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class LavaFrozingEnchantment extends Enchantment {

	public static void freezeLava(LivingEntity entity, World world, BlockPos blockPos, int level) {
		if (!(entity instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) entity;
		if (entity.isOnGround()) {
			BlockState obs = Blocks.OBSIDIAN.getDefaultState();
			BlockState magma = Blocks.MAGMA_BLOCK.getDefaultState();
			BlockState source = world.getDimension().isUltrawarm() ? magma : obs;
			BlockState basalt = Blocks.BASALT.getDefaultState();
			float f = Math.min(16, 2 + level);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			Iterator<BlockPos> itr = BlockPos.iterate(blockPos.add(-f, -2, -f), blockPos.add(f, 0, f)).iterator();
			while (itr.hasNext()) {
				BlockPos pos = itr.next();
				if (player.isBlockBreakingRestricted(world, pos,
						player.abilities.creativeMode ? GameMode.CREATIVE : GameMode.SURVIVAL))
					continue;
				if (!pos.isWithinDistance(entity.getPos(), f))
					continue;
				mutable.set(pos.getX(), pos.getY() + 1, pos.getZ());
				BlockState bs = world.getBlockState(mutable);
				if (bs.getMaterial() != Material.LAVA)
					continue;
				BlockState toPlace = bs.get(FluidBlock.LEVEL) == 0 ? source : basalt;
				if (toPlace.canPlaceAt(world, pos) && world.canPlace(toPlace, pos, ShapeContext.absent())) {
					world.setBlockState(pos, toPlace);
				}

			}
		}
	}

	public LavaFrozingEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET });
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return false;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

}
