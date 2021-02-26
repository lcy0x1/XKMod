package mod.oceanmaze.structure;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import mod.lcy0x1.util.maze.MazeConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.SpawnSettings.SpawnEntry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.Heightmap;

public class OceanMazeFeature extends StructureFeature<DefaultFeatureConfig> {

	public static class Start extends StructureStart<DefaultFeatureConfig> {

		public Start(StructureFeature<DefaultFeatureConfig> feature, int chunkX, int chunkZ, BlockBox box,
				int references, long seed) {
			super(feature, chunkX, chunkZ, box, references, seed);
		}

		@Override
		public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator,
				StructureManager manager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig config) {
			int x = chunkX * 16;
			int z = chunkZ * 16;
			int y = chunkGenerator.getHeight(x, z, Heightmap.Type.OCEAN_FLOOR);
			y = Math.max(y - 5, 30);
			MazeConfig conf = new MazeConfig();
			BlockPos pos = new BlockPos(x, y, z);
			OceanMazeGenerator.addPieces(manager, pos, this.children, this.random, conf);
			this.setBoundingBoxFromChildren();
		}

	}

	public static final List<SpawnSettings.SpawnEntry> MOB = new ArrayList<>();

	static {

		MOB.add(new SpawnEntry(EntityType.ELDER_GUARDIAN, 100, 1, 1));
		MOB.add(new SpawnEntry(EntityType.GUARDIAN, 200, 1, 4));
		MOB.add(new SpawnEntry(EntityType.DROWNED, 300, 1, 4));

	}

	public OceanMazeFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return Start::new;
	}

	@Override
	public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
		return MOB;
	}

}
