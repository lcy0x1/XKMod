package mod.oceanmaze.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class OceanMazeMap extends FilledMapItem {

	public OceanMazeMap(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (itemStack.getSubTag("map") == null) {
			int i = world.getNextMapId();
			MapState mapState = new MapState(getMapName(i));
			int x = MathHelper.floor(user.getX());
			int z = MathHelper.floor(user.getZ());
			mapState.init(x, z, 0, true, false, world.getRegistryKey());
			world.putMapState(mapState);
			itemStack.getOrCreateTag().putInt("map", i);
			return TypedActionResult.success(itemStack, world.isClient());
		} else
			return TypedActionResult.pass(itemStack);
	}

	@Override
	public void updateColors(World world, Entity entity, MapState state) {
		if (world.getRegistryKey() == state.dimension && entity instanceof PlayerEntity) {
			int scale = 1 << state.scale;
			int cx = state.xCenter;
			int cz = state.zCenter;
			int rx = MathHelper.floor(entity.getX() - cx) / scale + 64;
			int rz = MathHelper.floor(entity.getZ() - cz) / scale + 64;
			int radius = 128 / scale;
			MapState.PlayerUpdateTracker playerUpdateTracker = state.getPlayerSyncData((PlayerEntity) entity);
			++playerUpdateTracker.field_131;
			boolean flag = false;
			for (int bx = rx - radius + 1; bx < rx + radius; ++bx) {
				if ((bx & 15) == (playerUpdateTracker.field_131 & 15) || flag) {
					flag = false;
					for (int bz = rz - radius - 1; bz < rz + radius; ++bz) {
						if (bx >= 0 && bz >= -1 && bx < 128 && bz < 128) {
							int mx = bx - rx;
							int mz = bz - rz;
							boolean out_of_range = mx * mx + mz * mz > (radius - 2) * (radius - 2);
							int px = (cx / scale + bx - 64) * scale;
							int pz = (cz / scale + bz - 64) * scale;
							WorldChunk worldChunk = world.getWorldChunk(new BlockPos(px, 0, pz));
							if (!worldChunk.isEmpty()) {
								ChunkPos chunkPos = worldChunk.getPos();
								int cpx = px & 15;
								int cpz = pz & 15;
								BlockPos.Mutable mutable = new BlockPos.Mutable();
								int y = entity.getBlockPos().getY() + 1;
								mutable.set(chunkPos.getStartX() + cpx, y, chunkPos.getStartZ() + cpz);
								BlockState blockState = worldChunk.getBlockState(mutable);
								double f = ((bx + bz & 1) - 0.5D) * 0.4D;
								int ac = f > 0.6 ? 2 : f < -0.6 ? 0 : 1;
								MaterialColor materialColor = blockState.getMaterial().getColor();
								if (materialColor == MaterialColor.WATER) {
									f = (bx + bz & 1) * 0.2D;
									ac = f < 0.5 ? 2 : f > 0.9 ? 0 : 1;
								}
								if (bz >= 0 && mx * mx + mz * mz < radius * radius
										&& (!out_of_range || (bx + bz & 1) != 0)) {
									byte b = state.colors[bx + bz * 128];
									byte c = (byte) (materialColor.id * 4 + ac);
									if (b != c) {
										state.colors[bx + bz * 128] = c;
										state.markDirty(bx, bz);
										flag = true;
									}
								}

							}
						}
					}
				}
			}
		}

	}

}
