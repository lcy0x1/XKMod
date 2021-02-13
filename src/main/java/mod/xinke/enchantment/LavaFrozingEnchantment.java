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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LavaFrozingEnchantment extends Enchantment {

	public LavaFrozingEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET });
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	public static void freezeLava(LivingEntity entity, World world, BlockPos blockPos, int level) {
		if (entity.isOnGround()) {
			BlockState magma = Blocks.OBSIDIAN.getDefaultState();
			float f = Math.min(16, 2 + level);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			Iterator<BlockPos> itr = BlockPos.iterate(blockPos.add(-f, -1, -f), blockPos.add(f, -1, f)).iterator();
			while (itr.hasNext()) {
				BlockPos pos = itr.next();
				if (pos.isWithinDistance(entity.getPos(), f)) {
					mutable.set(pos.getX(), pos.getY() + 1, pos.getZ());
					BlockState bsu = world.getBlockState(mutable);
					if (bsu.isAir()) {
						BlockState bs0 = world.getBlockState(pos);
						if (bs0.getMaterial() == Material.LAVA && bs0.get(FluidBlock.LEVEL) == 0
								&& magma.canPlaceAt(world, pos) && world.canPlace(magma, pos, ShapeContext.absent())) {
							world.setBlockState(pos, magma);
						}
					}
				}
			}

		}
	}

}
