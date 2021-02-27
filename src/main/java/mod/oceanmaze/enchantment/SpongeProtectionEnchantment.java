package mod.oceanmaze.enchantment;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.oceanmaze.main.OceanMaze;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SpongeProtectionEnchantment extends Enchantment {

	public static final Set<Item> SET = ImmutableSet.of(OceanMaze.I_OM_CHESTPLATE,
			OceanMaze.I_DOM_HELMET, OceanMaze.I_DOM_CHESTPLATE,
			OceanMaze.I_DOM_LEGGINGS, OceanMaze.I_DOM_BOOTS);

	public SpongeProtectionEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.ARMOR, EquipmentSlot.values());
	}

	public boolean isAcceptableItem(ItemStack is) {
		return SET.contains(is.getItem());
	}

	public int getMinPower(int level) {
		return 25 + (level - 1) * 10;
	}

	public int getMaxPower(int level) {
		return this.getMinPower(level) + 10;
	}

	public int getMaxLevel() {
		return 2;
	}

	public int getProtectionAmount(int level, DamageSource source) {
		if (source.isFire() || source == DamageSource.FLY_INTO_WALL || source == DamageSource.FALL)
			return level * 3;
		return 0;
	}

}
