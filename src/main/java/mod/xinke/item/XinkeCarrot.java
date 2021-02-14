package mod.xinke.item;

import mod.xinke.main.XinkeMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class XinkeCarrot extends Item {

	public XinkeCarrot(Settings settings) {
		super(settings.food(new FoodComponent.Builder().alwaysEdible()
				.statusEffect(new StatusEffectInstance(XinkeMod.XINKE_BLESS, 1200), 1).build()));
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) user;
		player.inventory.armor.forEach(XinkeCarrot::setEnergy);
	}

	public static void setEnergy(ItemStack is) {
		if (XinkeEnergyItem.isXinkeEnergyItem(is)) {
			XinkeEnergyItem.raiseMaxEnergy(is, 100);
			XinkeEnergyItem.raiseEnergy(is, 100);
		}
	}

}
