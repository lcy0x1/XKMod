package mod.oceanmaze.main;

import mod.oceanmaze.structure.OceanMazeFeature;
import mod.oceanmaze.structure.OceanMazeGenerator;
import mod.oceanmaze.structure.OnMazeStructureProcessor;
import mod.oceanmaze.structure.UnderWaterStructureProcessor;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

@SuppressWarnings("deprecation")
public class OceanMazeStructureReg {

	public static final StructurePieceType SPT_OCEANMAZE = OceanMazeGenerator.Piece::new;

	public static final StructureFeature<DefaultFeatureConfig> SF_OCEANMAZE = new OceanMazeFeature(
			DefaultFeatureConfig.CODEC);

	public static final ConfiguredStructureFeature<?, ?> CSF_OCEANMAZE = SF_OCEANMAZE
			.configure(DefaultFeatureConfig.DEFAULT);

	public static final StructureProcessorType<UnderWaterStructureProcessor> SPCT_UW = StructureProcessorType
			.register("oceanmaze:under_water", UnderWaterStructureProcessor.CODEC);
	
	public static final StructureProcessorType<OnMazeStructureProcessor> SPCT_OM = StructureProcessorType
			.register("oceanmaze:on_maze", OnMazeStructureProcessor.CODEC);

	public static void onInit() {
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(OceanMaze.MODID, "oceanmaze"), SPT_OCEANMAZE);
		FabricStructureBuilder.create(new Identifier(OceanMaze.MODID, "oceanmaze"), SF_OCEANMAZE)
				.step(GenerationStep.Feature.SURFACE_STRUCTURES).defaultConfig(64, 8, 26243534).register();

		RegistryKey<ConfiguredStructureFeature<?, ?>> myConfigured = RegistryKey
				.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, new Identifier(OceanMaze.MODID, "oceanmaze"));
		BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, myConfigured.getValue(), CSF_OCEANMAZE);

		BiomeModifications.addStructure((biome) -> biome.getBiome().getCategory() == Biome.Category.OCEAN
				&& biome.getBiome().getDepth() < -1.5f, myConfigured);
	}

}
