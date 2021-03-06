package mod.oceanmaze.main;

import mod.oceanmaze.effects.SpongeWetEffect;
import mod.oceanmaze.enchantment.SpongeProtectionEnchantment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OceanMaze implements ModInitializer {

	public static final String MODID = "oceanmaze";

	public static final Enchantment SPONGE_PROT = Registry.register(Registry.ENCHANTMENT,
			new Identifier(MODID, "sponge_protection"), new SpongeProtectionEnchantment());

	public static final SpongeWetEffect SPONGE_WET = new SpongeWetEffect();

	@Override
	public void onInitialize() {
		BIReg.init();
		Registry.register(Registry.STATUS_EFFECT, new Identifier(MODID, "sponge_wet"), SPONGE_WET);
		OceanMazeStructureReg.onInit();
		LootInjector.injector();
	}
}
