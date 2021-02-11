package mod.xinke.block.xkec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import mod.xinke.block.CTESReg;
import mod.xinke.main.XinkeMod;
import mod.xinke.recipe.XKECRecipe;
import mod.xinke.recipe.XKECRecipe.InvLayer;
import mod.xinke.util.SerialClass;
import mod.xinke.util.SerialClass.SerialField;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

@SerialClass
public class XKECCoreEntity extends AbstractXKECBlockEntity<XKECCoreEntity> implements XKECRecipe.Inv {

	@SerialField(toClient = true)
	public BlockPos[] conn = new BlockPos[0];

	public XKECCoreEntity() {
		super(CTESReg.BET_XKEC_CORE);
	}

	@Override
	public void activate(PlayerEntity pl) {
		if (this.getWorld().isClient())
			return;
		BlockPos self = this.getPos();
		Block b = this.getWorld().getBlockState(self).getBlock();
		int range = b == XinkeMod.B_XKEC_CORE_2 ? 16 : b == XinkeMod.B_XKEC_CORE_1 ? 8 : 4;
		int max = b == XinkeMod.B_XKEC_CORE_2 ? 7 : b == XinkeMod.B_XKEC_CORE_1 ? 5 : 3;
		BlockPos.Mutable bs = new BlockPos.Mutable();
		Map<Integer, List<BlockPos>> map = new TreeMap<>();
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					int sqdis = i * i + j * j + k * k;
					if (sqdis > range * range)
						continue;
					bs.set(self.getX() + i, self.getY() + j, self.getZ() + k);
					BlockEntity be = this.getWorld().getBlockEntity(bs);
					if (be instanceof XKECSideEntity) {
						List<BlockPos> list;
						if (map.containsKey(sqdis))
							list = map.get(sqdis);
						else
							map.put(sqdis, list = new ArrayList<>());
						list.add(bs.toImmutable());
					}
				}
			}
		}
		List<BlockPos> list = new ArrayList<>();
		int count = 0;
		for (Entry<Integer, List<BlockPos>> ent : map.entrySet()) {
			list.addAll(ent.getValue());
			count++;
			if (count >= max)
				break;
		}
		conn = list.toArray(new BlockPos[0]);
		for (BlockPos pos : conn) {
			BlockEntity be = this.getWorld().getBlockEntity(pos);
			if (be instanceof XKECSideEntity)
				((XKECSideEntity) be).setConnected(self);
		}
		markDirty();
		sync();
	}

	@Override
	public void clearAll() {
		for (BlockPos p : conn) {
			BlockEntity be = getWorld().getBlockEntity(p);
			if (be instanceof XKECSideEntity) {
				XKECSideEntity se = (XKECSideEntity) be;
				se.clear();
			}
		}
	}

	public void disConnect(BlockPos pos) {
		List<BlockPos> list = new ArrayList<>();
		for (BlockPos p : conn)
			list.add(p);
		list.remove(pos);
		conn = list.toArray(new BlockPos[0]);
		markDirty();
	}

	@Override
	public InvLayer[] getLayers() {
		List<InvLayer> list = new ArrayList<>();
		List<ItemStack> cur = null;
		int prev = -1;
		for (BlockPos i : conn) {
			BlockPos p = i.subtract(getPos());
			int x = p.getX();
			int y = p.getY();
			int z = p.getZ();
			int sqdis = x * x + z * z + y * y;
			if (sqdis != prev) {
				if (cur != null)
					list.add(new InvLayer(cur));
				cur = new ArrayList<>();
				prev = sqdis;
			}
			BlockEntity be = getWorld().getBlockEntity(p);
			if (be instanceof XKECSideEntity) {
				XKECSideEntity se = (XKECSideEntity) be;
				cur.add(se.inv);
			}
		}
		if (cur != null)
			list.add(new InvLayer(cur));
		return list.toArray(new InvLayer[0]);
	}

	@Override
	public void onDestroy() {
		if (getWorld().isClient())
			return;
		for (BlockPos p : conn) {
			BlockEntity be = getWorld().getBlockEntity(p);
			if (be instanceof XKECSideEntity) {
				XKECSideEntity se = (XKECSideEntity) be;
				se.disConnect();
			}
		}
	}

}
