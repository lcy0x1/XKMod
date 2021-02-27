package mod.oceanmaze.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.oceanmaze.main.OceanMazeStructureReg;
import mod.oceanmaze.structure.OceanMazeGenerator;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

	@Inject(at = @At("HEAD"), cancellable = true, method = "getEntitySpawnList(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;")
	public void getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos,
			CallbackInfoReturnable<List<SpawnSettings.SpawnEntry>> info) {
		if (group == SpawnGroup.MONSTER) {
			StructureStart<?> st = accessor.getStructureAt(pos, false, OceanMazeStructureReg.SF_OCEANMAZE);
			if (st.hasChildren()) {
				BlockBox box = st.getBoundingBox();
				for (int i = 0; i < OceanMazeGenerator.LAYERS.length; i++) {
					BlockBox sub = new BlockBox(box.minX + i * 5 + 5, box.minY + i * 5 + 5, box.minZ + i * 5 + 5,
							box.maxX - i * 5 - 5, box.minY + i * 5 + 10, box.maxZ - i * 5 - 5);
					if (sub.contains(pos))
						info.setReturnValue(OceanMazeStructureReg.SF_OCEANMAZE.getMonsterSpawns());
				}
			}
		}
	}
}
