package mod.oceanmaze.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mod.lcy0x1.util.maze.MazeConfig;
import mod.lcy0x1.util.maze.MazeGen;
import mod.lcy0x1.util.maze.MazeGen.Debugger;
import mod.oceanmaze.main.OceanMazeStructureReg;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.ChunkRandom;

public class OceanMazeGenerator {

	public static enum CellType {
		END, STRAIGHT, CORNER, T_WAY, CROSS;
	}

	public static class Piece extends SimpleStructurePiece {

		private final BlockRotation rotation;
		private final Identifier template;
		public final boolean inner;

		public Piece(StructureManager structureManager, BlockPos pos, Identifier template, BlockRotation rotation,
				boolean inner) {
			super(OceanMazeStructureReg.SPT_OCEANMAZE, 0);
			this.pos = pos;
			this.rotation = rotation;
			this.template = template;
			this.inner = inner;
			this.initializeStructureData(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(OceanMazeStructureReg.SPT_OCEANMAZE, compoundTag);
			this.template = new Identifier(compoundTag.getString("Template"));
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.inner = compoundTag.getBoolean("inner");
			this.initializeStructureData(structureManager);
		}

		@Override
		protected void handleMetadata(String str, BlockPos pos, ServerWorldAccess world, Random random,
				BlockBox boundingBox) {
			Identifier id = new Identifier(str);
			if (str.startsWith("oceanmaze:chests/")) {
				BlockEntity be = world.getBlockEntity(pos.offset(Direction.DOWN));
				if (be instanceof LootableContainerBlockEntity)
					((LootableContainerBlockEntity) be).setLootTable(id, random.nextLong());
			} else if (str.startsWith("oceanmaze:blocks/")) {
				String type = str.substring(17);
				BlockState bs = null;
				if (type.equals("top")) {
					int r = random.nextInt(100);
					if (r < 5)
						bs = Blocks.NETHERITE_BLOCK.getDefaultState();
					else if (r < 50)
						bs = Blocks.GOLD_BLOCK.getDefaultState();
					else
						bs = Blocks.CRYING_OBSIDIAN.getDefaultState();
				} else if (type.equals("bottom")) {
					int r = random.nextInt(100);
					if (r < 10)
						bs = Blocks.NETHERITE_BLOCK.getDefaultState();
					else if (r < 70)
						bs = Blocks.GOLD_BLOCK.getDefaultState();
					else
						bs = Blocks.CRYING_OBSIDIAN.getDefaultState();
				} else
					bs = Blocks.NETHERRACK.getDefaultState();
				world.setBlockState(pos, bs, 2);
			}
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putString("Rot", this.rotation.name());
			tag.putBoolean("inner", inner);
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData placementData = (new StructurePlacementData()).setRotation(this.rotation)
					.setMirror(BlockMirror.NONE).setPosition(new BlockPos(2, 0, 2))
					.addProcessor(inner ? UnderWaterStructureProcessor.INSTANCE : OnMazeStructureProcessor.INSTANCE);
			this.setStructureData(structure, this.pos, placementData);
		}

	}

	public static final Identifier STRAIGHT = new Identifier("oceanmaze:oceanmaze/straight");
	public static final Identifier CORNER = new Identifier("oceanmaze:oceanmaze/corner");
	public static final Identifier T_WAY = new Identifier("oceanmaze:oceanmaze/t_way");
	public static final Identifier CROSS = new Identifier("oceanmaze:oceanmaze/cross");
	public static final Identifier END_LV0 = new Identifier("oceanmaze:oceanmaze/end_0");
	public static final Identifier END_LV1 = new Identifier("oceanmaze:oceanmaze/end_1");
	public static final Identifier END_LV2 = new Identifier("oceanmaze:oceanmaze/end_2");
	public static final Identifier END_LV3 = new Identifier("oceanmaze:oceanmaze/end_3");
	public static final Identifier CORE = new Identifier("oceanmaze:oceanmaze/core");
	public static final Identifier DEGRADE = new Identifier("oceanmaze:oceanmaze/degrade");

	public static final Identifier SIDE_EDGE = new Identifier("oceanmaze:oceanmaze/side_edge");
	public static final Identifier SIDE_CORNER = new Identifier("oceanmaze:oceanmaze/side_corner");
	public static final Identifier LAST_SIDE_EDGE = new Identifier("oceanmaze:oceanmaze/last_side_edge");
	public static final Identifier LAST_SIDE_CORNER = new Identifier("oceanmaze:oceanmaze/last_side_corner");

	public static final Identifier TOP_EDGE = new Identifier("oceanmaze:oceanmaze/top_edge");
	public static final Identifier TOP_CORNER = new Identifier("oceanmaze:oceanmaze/top_corner");
	public static final Identifier BOTTOM_EDGE = new Identifier("oceanmaze:oceanmaze/bottom_edge");
	public static final Identifier BOTTOM_CORNER = new Identifier("oceanmaze:oceanmaze/bottom_corner");

	public static final Identifier TOP_FACE_CORE = new Identifier("oceanmaze:oceanmaze/top_core");
	public static final Identifier TOP_FACE = new Identifier("oceanmaze:oceanmaze/top_face");
	public static final Identifier TOP_FACE_0 = new Identifier("oceanmaze:oceanmaze/top_face_0");
	public static final Identifier TOP_FACE_1 = new Identifier("oceanmaze:oceanmaze/top_face_1");
	public static final Identifier TOP_FACE_2 = new Identifier("oceanmaze:oceanmaze/top_face_2");
	public static final Identifier TOP_FACE_B = new Identifier("oceanmaze:oceanmaze/top_face_staff");

	public static final Identifier BOTTOM_FACE = new Identifier("oceanmaze:oceanmaze/bottom_face");
	public static final Identifier BOTTOM_FACE_0 = new Identifier("oceanmaze:oceanmaze/bottom_face_0");
	public static final Identifier BOTTOM_FACE_1 = new Identifier("oceanmaze:oceanmaze/bottom_face_1");
	public static final Identifier BOTTOM_FACE_2 = new Identifier("oceanmaze:oceanmaze/bottom_face_2");
	public static final Identifier BOTTOM_FACE_B = new Identifier("oceanmaze:oceanmaze/bottom_face_staff");

	public static final int[] LAYERS = { 8, 9, 10, 11, 12 };

	public static void addPieces(StructureManager manager, BlockPos pos, List<StructurePiece> children, ChunkRandom r,
			MazeConfig conf) {
		MazeGen[] mazes = new MazeGen[LAYERS.length];
		for (int i = 0; i < LAYERS.length; i++) {
			mazes[i] = new MazeGen(LAYERS[i], r, conf, new Debugger());
			mazes[i].gen();
			int d = mazes[i].r;
			int[][] map = mazes[i].ans;
			List<int[]> list = new ArrayList<>();
			for (int x = 0; x < mazes[i].w; x++)
				for (int z = 0; z < mazes[i].w; z++) {
					int dire = map[x][z];
					if (parse_ct(dire) == CellType.END)
						list.add(new int[] { x, z });
				}
			Object[] arr00 = list.stream().filter(a -> a[0] < d && a[1] < d).toArray();
			Object[] arr01 = list.stream().filter(a -> a[0] < d && a[1] > d).toArray();
			Object[] arr10 = list.stream().filter(a -> a[0] > d && a[1] < d).toArray();
			Object[] arr11 = list.stream().filter(a -> a[0] > d && a[1] > d).toArray();
			if (arr00.length == 0)
				arr00 = new int[][] { { -1, -1 } };
			if (arr01.length == 0)
				arr01 = new int[][] { { -1, -1 } };
			if (arr10.length == 0)
				arr10 = new int[][] { { -1, -1 } };
			if (arr11.length == 0)
				arr11 = new int[][] { { -1, -1 } };
			int[] a00 = (int[]) arr00[r.nextInt(arr00.length)];
			int[] a01 = (int[]) arr01[r.nextInt(arr01.length)];
			int[] a10 = (int[]) arr10[r.nextInt(arr10.length)];
			int[] a11 = (int[]) arr11[r.nextInt(arr11.length)];

			for (int x = 0; x < mazes[i].w; x++)
				for (int z = 0; z < mazes[i].w; z++) {
					int dire = map[x][z];
					CellType ct = parse_ct(dire);

					Identifier id;
					if (x == a00[0] && z == a00[1] || x == a01[0] && z == a01[1] || x == a10[0] && z == a10[1]
							|| x == a11[0] && z == a11[1])
						id = i == mazes.length - 1 ? DEGRADE : CORE;
					else
						id = parse_id(ct, r, Math.abs(x - mazes[i].r), Math.abs(z - mazes[i].r));
					children.add(new Piece(manager, pos.add((x - mazes[i].r) * 5, -i * 5, (z - mazes[i].r) * 5), id,
							parse_rot(dire), true));
				}
		}
		for (int i = 0; i < LAYERS.length - 1; i++) {
			for (int j = 0; j < mazes[i].w; j++) {
				addEdge(manager, children, pos, i - 1, -1, mazes[i].w, mazes[i].r, j, TOP_EDGE);
				addEdge(manager, children, pos, i, -1, mazes[i].w, mazes[i].r, j, SIDE_EDGE);
			}
			addEdge(manager, children, pos, i - 1, -1, mazes[i].w, mazes[i].r, mazes[i].w, TOP_CORNER);
			addEdge(manager, children, pos, i, -1, mazes[i].w, mazes[i].r, mazes[i].w, SIDE_CORNER);
		}
		for (int x = 0; x < mazes[0].w; x++)
			for (int z = 0; z < mazes[0].w; z++) {
				Identifier id;
				if (x == mazes[0].r && z == x)
					id = TOP_FACE_CORE;
				else {
					int rand = r.nextInt(200) + 100 * (x + z & 1);
					if (rand < 5)
						id = TOP_FACE_B;
					else if (rand < 10)
						id = TOP_FACE_0;
					else if (rand < 15)
						id = TOP_FACE_1;
					else if (rand < 20)
						id = TOP_FACE_2;
					else
						id = TOP_FACE;
				}
				children.add(new Piece(manager, pos.add((x - mazes[0].r) * 5, 5, (z - mazes[0].r) * 5), id,
						BlockRotation.NONE, false));
			}
		int ln = LAYERS.length - 1;
		for (int x = 0; x < mazes[ln].w; x++)
			for (int z = 0; z < mazes[ln].w; z++) {
				Identifier id;
				int rand = r.nextInt(200) + 100 * (x + z & 1);
				if (rand < 5)
					id = BOTTOM_FACE_B;
				else if (rand < 10)
					id = BOTTOM_FACE_0;
				else if (rand < 15)
					id = BOTTOM_FACE_1;
				else if (rand < 20)
					id = BOTTOM_FACE_2;
				else
					id = BOTTOM_FACE;
				children.add(new Piece(manager, pos.add((x - mazes[ln].r) * 5, -(ln + 1) * 5, (z - mazes[ln].r) * 5),
						id, BlockRotation.NONE, false));
			}
		for (int j = 0; j < mazes[ln].w; j++) {
		}
		addEdge(manager, children, pos, ln - 1, -1, mazes[ln].w, mazes[ln].r, mazes[ln].w, TOP_CORNER);
		addEdge(manager, children, pos, ln, -1, mazes[ln].w, mazes[ln].r, mazes[ln].w, LAST_SIDE_CORNER);
		for (int j = 0; j < mazes[ln].w; j++) {
			addEdge(manager, children, pos, ln - 1, -1, mazes[ln].w, mazes[ln].r, j, TOP_EDGE);
			addEdge(manager, children, pos, ln, -1, mazes[ln].w, mazes[ln].r, j, LAST_SIDE_EDGE);
			addEdge(manager, children, pos, ln + 1, -1, mazes[ln].w, mazes[ln].r, j, BOTTOM_EDGE);
		}
	}

	private static void addEdge(StructureManager manager, List<StructurePiece> list, BlockPos pos, int h, int o, int w,
			int d, int j, Identifier id) {
		list.add(new Piece(manager, pos.add((o - d) * 5, -h * 5, (j - d) * 5), id, BlockRotation.NONE, false));
		list.add(new Piece(manager, pos.add((w - d) * 5, -h * 5, (w - j - 1 - d) * 5), id, BlockRotation.CLOCKWISE_180,
				false));
		list.add(new Piece(manager, pos.add((w - j - 1 - d) * 5, -h * 5, (o - d) * 5), id, BlockRotation.CLOCKWISE_90,
				false));
		list.add(new Piece(manager, pos.add((j - d) * 5, -h * 5, (w - d) * 5), id, BlockRotation.COUNTERCLOCKWISE_90,
				false));
	}

	private static CellType parse_ct(int cell) {
		if (cell == 1 || cell == 2 || cell == 4 || cell == 8)
			return CellType.END;
		if (cell == 3 || cell == 12)
			return CellType.STRAIGHT;
		if (cell == 5 || cell == 9 || cell == 6 || cell == 10)
			return CellType.CORNER;
		if (cell == 7 || cell == 11 || cell == 13 || cell == 14)
			return CellType.T_WAY;
		if (cell == 15)
			return CellType.CROSS;
		System.out.println("error: invalid cell " + cell);
		return null;
	}

	private static Identifier parse_id(CellType ct, ChunkRandom r, int x, int z) {
		if (ct == CellType.STRAIGHT)
			return STRAIGHT;
		if (ct == CellType.CORNER)
			return CORNER;
		if (ct == CellType.T_WAY)
			return T_WAY;
		if (ct == CellType.CROSS)
			return CROSS;
		if (ct == CellType.END) {
			int rand = r.nextInt((x + z) + 1);
			if (rand < 5)
				return END_LV0;
			if (rand < 10)
				return END_LV1;
			if (rand < 15)
				return END_LV2;
			return END_LV3;
		}
		return null;
	}

	private static BlockRotation parse_rot(int cell) {
		if (cell == 1)
			return BlockRotation.NONE;
		if (cell == 2)
			return BlockRotation.CLOCKWISE_180;
		if (cell == 8)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 4)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 3)
			return BlockRotation.NONE;
		if (cell == 12)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 9)
			return BlockRotation.NONE;
		if (cell == 6)
			return BlockRotation.CLOCKWISE_180;
		if (cell == 10)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 5)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 13)
			return BlockRotation.NONE;
		if (cell == 14)
			return BlockRotation.CLOCKWISE_180;
		if (cell == 11)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 7)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 15)
			return BlockRotation.NONE;
		System.out.println("error: invalid cell " + cell);
		return null;
	}

}
