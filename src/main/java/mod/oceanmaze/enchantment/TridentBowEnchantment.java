package mod.oceanmaze.enchantment;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.oceanmaze.main.BIReg;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TridentBowEnchantment extends Enchantment {

	public static final Set<Item> SET = ImmutableSet.of(BIReg.I_TRIDENT_BOW);

	public TridentBowEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.values());
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 10;
	}

	@Override
	public int getMinPower(int level) {
		return 25 + (level - 1) * 10;
	}

	@Override
	public boolean isAcceptableItem(ItemStack is) {
		return SET.contains(is.getItem());
	}

	public boolean isAvailableForEnchantedBookOffer() {
		return false;
	}

	public boolean isAvailableForRandomSelection() {
		return false;
	}

	public boolean isTreasure() {
		return true;
	}

}
