package mod.oceanmaze.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OceanMetalArmor extends ArmorItem {

	public OceanMetalArmor(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world.isClient())
			return;
		if (!(entity instanceof LivingEntity))
			return;
	}

}
