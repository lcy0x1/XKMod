package mod.oceanmaze.main;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.util.Identifier;

public class LootInjector {

	public static void injector() {
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (new Identifier("minecraft:chests/underwater_ruin_big").equals(id)) {
				FabricLootPoolBuilder pool = FabricLootPoolBuilder.builder();
				pool.rolls(ConstantLootTableRange.create(1));
				pool.withEntry(ItemEntry.builder(Items.MAP)
						.apply(ExplorationMapLootFunction.create().withDestination(OceanMazeStructureReg.SF_OCEANMAZE))
						.build());
				supplier.pool(pool);
			}
		});
	}

}
