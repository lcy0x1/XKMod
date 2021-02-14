package mod.xinke.block;

import java.util.function.Supplier;

import mod.xinke.block.main.TeleportBlock;
import mod.xinke.block.xkec.XKECCoreEntity;
import mod.xinke.block.xkec.XKECSideEntity;
import mod.xinke.block.xkec.XKITEntity;
import mod.xinke.block.xkec.XKNodeEntity;
import mod.xinke.main.XinkeMod;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CTESReg {

	public static BlockEntityType<XKNodeEntity> BET_XK_NODE;
	public static BlockEntityType<XKECSideEntity> BET_XKEC_SIDE;
	public static BlockEntityType<XKECCoreEntity> BET_XKEC_CORE;
	public static BlockEntityType<XKITEntity> BET_XKIT;

	public static BlockEntityType<TeleportBlock.TE> BET_TELE;
	public static ScreenHandlerType<TeleportBlock.Cont> SHT_TELE;

	public static void register() {
		BET_XK_NODE = reg("xkec_node", XKNodeEntity::new, XinkeMod.B_XK_NODE);
		BET_XKEC_SIDE = reg("xkec_side", XKECSideEntity::new, XinkeMod.B_XKEC_SIDE);
		BET_XKEC_CORE = reg("xkec_core", XKECCoreEntity::new, XinkeMod.B_XKEC_CORE_0, XinkeMod.B_XKEC_CORE_1,
				XinkeMod.B_XKEC_CORE_2);
		BET_XKIT = reg("xkit", XKITEntity::new, XinkeMod.B_XKIT_SOURCE, XinkeMod.B_XKIT_MIDDLE, XinkeMod.B_XKIT_TARGET);
		BET_TELE = reg("teleport", TeleportBlock.TE::new, XinkeMod.B_TELE);

		ScreenHandlerRegistry.registerSimple(new Identifier(XinkeMod.MODID, "teleport"), TeleportBlock.Cont::new);
	}

	private static <T extends BlockEntity> BlockEntityType<T> reg(String id, Supplier<T> sup, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, XinkeMod.MODID + ":" + id,
				BlockEntityType.Builder.create(sup, blocks).build(null));
	}

}
