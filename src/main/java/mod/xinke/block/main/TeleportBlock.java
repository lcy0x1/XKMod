package mod.xinke.block.main;

import java.util.List;

import mod.lcy0x1.util.SpriteManager;
import mod.lcy0x1.util.SpriteManager.ScreenRenderer;
import mod.lcy0x1.block.AutoScreen;
import mod.lcy0x1.block.BlockProp;
import mod.lcy0x1.block.InvBlockEntity;
import mod.lcy0x1.block.InvContainer;
import mod.lcy0x1.block.BaseBlock.BaseBlockWithEntity;
import mod.lcy0x1.util.NBTManager;
import mod.lcy0x1.util.SerialClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import mod.xinke.block.CTESReg;
import mod.xinke.main.XinkeMod;

public class TeleportBlock extends BaseBlockWithEntity {

	public static class Cont extends InvContainer<Cont> {

		public Cont(int syncId, PlayerInventory plInv) {
			this(syncId, plInv, new SimpleInventory(INV_SIZE));
		}

		public Cont(int syncId, PlayerInventory plInv, Inventory inv) {
			super(CTESReg.SHT_TELE, syncId, plInv, inv, SPRM.getPIH());
			addSlot(SPRM.getSlot("bind_in", (x, y) -> new CondSlot(inv, 0, x, y, (is) -> isItemValid(0, is))));
			addSlot(SPRM.getSlot("bind_out", (x, y) -> new ResultSlot(inv, 1, x, y)));
			addSlot(SPRM.getSlot("bind", (x, y) -> new CondSlot(inv, 2, x, y, (is) -> isItemValid(2, is))));
			addSlot(SPRM.getSlot("activate", (x, y) -> new CondSlot(inv, 3, x, y, (is) -> isItemValid(3, is))));
		}

	}

	@Environment(EnvType.CLIENT)
	public static class Scr extends AutoScreen<Cont> {

		public Scr(Cont handler, PlayerInventory inventory, Text title) {
			super(handler, inventory, title, SPRM.getHeight());
		}

		@Override
		protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
			ScreenRenderer sr = SPRM.getRenderer(this);
			sr.start(matrices);
		}

	}

	@SerialClass
	public static class TE extends InvBlockEntity<TE> implements SidedInventory, Tickable {

		public TE() {
			super(CTESReg.BET_TELE, INV_SIZE);
		}

		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return dir != Direction.UP && (slot == SLOT_BIND || slot == SLOT_BIND_OUT);
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack, Direction dir) {
			return dir != Direction.UP && isValid(slot, stack);
		}

		@Override
		public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
			return new Cont(syncId, inv, this);
		}

		@Override
		public int[] getAvailableSlots(Direction side) {
			return new int[] { 2, 3 };
		}

		public ItemStack getBind() {
			ItemStack ans = new ItemStack(XinkeMod.I_TELE_BIND);
			NBTManager nbt = new NBTManager(ans.getOrCreateTag());
			nbt.putBlockPos("pos", getPos());
			nbt.tag.putString("dim", getWorld().getRegistryKey().getValue().toString());
			return ans;
		}

		@Override
		public Text getDisplayName() {
			return new TranslatableText("xinke:container.teleport");
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			return isItemValid(slot, stack);
		}

		@Override
		public void tick() {
			if (getWorld().isClient())
				return;
			BlockState bs = getWorld().getBlockState(getPos());
			boolean charged = bs.get(Properties.LIT);
			if (charged != !getStack(SLOT_BIND).isEmpty())
				getWorld().setBlockState(getPos(), bs.with(Properties.LIT, charged = !getStack(SLOT_BIND).isEmpty()));
			if (!getStack(SLOT_BIND_IN).isEmpty() && getStack(SLOT_BIND_OUT).isEmpty()) {
				removeStack(SLOT_BIND_IN);
				setStack(SLOT_BIND_OUT, getBind());
			}
			if (!getStack(SLOT_ACTIVATE).isEmpty() && !getStack(SLOT_BIND).isEmpty()) {
				List<LivingEntity> liv = getWorld().getEntitiesByClass(LivingEntity.class, new Box(getPos().up()),
						(e) -> true);
				int max = Math.min(getStack(SLOT_ACTIVATE).getCount(), liv.size());
				NBTManager nbt = new NBTManager(getStack(SLOT_BIND).getTag());
				BlockPos pos = nbt.getBlockPos("pos");
				Identifier dim = new Identifier(nbt.tag.getString("dim"));
				if (getWorld().getRegistryKey().getValue().equals(dim))
					for (int i = 0; i < max; i++) {
						double x = pos.getX() + 0.5;
						double y = pos.getY() + 1;
						double z = pos.getZ() + 0.5;
						liv.get(i).requestTeleport(x, y, z);
					}
				removeStack(SLOT_ACTIVATE, max);
			}
		}

	}

	public static class TeleState implements IState, ILight {

		public static final TeleState INSTANCE = new TeleState();

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(Properties.LIT);
		}

		@Override
		public int getLightValue(BlockState bs) {
			return bs.get(Properties.LIT) ? 15 : 0;
		}

		@Override
		public BlockState setDefaultState(BlockState bs) {
			return bs.with(Properties.LIT, false);
		}

	}

	public static final int INV_SIZE = 4;
	public static final int SLOT_BIND_IN = 0;
	public static final int SLOT_BIND_OUT = 1;
	public static final int SLOT_BIND = 2;
	public static final int SLOT_ACTIVATE = 3;

	public static final SpriteManager SPRM = new SpriteManager(XinkeMod.MODID, "teleport");

	public static boolean isItemValid(int slot, ItemStack stack) {
		if (slot == SLOT_BIND_IN)
			return stack.getItem() == XinkeMod.I_TELE_CHARGE;
		if (slot == SLOT_ACTIVATE)
			return stack.getItem() == Items.ENDER_PEARL;
		return stack.getItem() == XinkeMod.I_TELE_BIND;
	}

	public TeleportBlock(BlockProp p, STE ste) {
		super(p, ste, TeleState.INSTANCE);
	}

}
