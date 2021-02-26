package mod.oceanmaze.structure;

import com.mojang.serialization.Codec;

import mod.lcy0x1.util.maze.MazeConfig;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
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

	public OceanMazeFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return Start::new;
	}

}
