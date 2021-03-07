package mod.oceanmaze.enchantment;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TridentSharpEnchantment extends Enchantment {

	public static final Set<Item> SET = ImmutableSet.of(Items.TRIDENT);

	public TridentSharpEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values());
	}

	public float getAttackDamage(int level, EntityGroup group) {
		return level * 1.5f;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + 10;
	}

	@Override
	public int getMinPower(int level) {
		return 25 + (level - 1) * 10;
	}
	
	public boolean canAccept(Enchantment other) {
		return !(other instanceof ImpalingEnchantment);
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
