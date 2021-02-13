package mod.xinke.item;

import net.minecraft.item.ItemStack;

public interface XinkeEnergyItem {

	public static int getEnergy(ItemStack is) {
		return is.getOrCreateSubTag("xinke_energy").getInt("energy");
	}

	public static int getMaxEnergy(ItemStack is) {
		return is.getOrCreateSubTag("xinke_energy").getInt("max_energy");
	}

	public static boolean isXinkeEnergyItem(ItemStack is) {
		return is.getItem() instanceof XinkeEnergyItem;
	}

	public static void setEnergy(ItemStack is, int max) {
		is.getOrCreateSubTag("xinke_energy").putInt("energy", max);
	}

	public static void setMaxEnergy(ItemStack is, int max) {
		is.getOrCreateSubTag("xinke_energy").putInt("max_energy", max);
	}

}
