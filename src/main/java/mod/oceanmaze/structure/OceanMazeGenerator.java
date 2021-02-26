package mod.oceanmaze.structure;

import java.util.List;
import java.util.Random;

import mod.lcy0x1.util.maze.MazeConfig;
import mod.lcy0x1.util.maze.MazeGen;
import mod.lcy0x1.util.maze.MazeGen.Debugger;
import mod.oceanmaze.main.OceanMazeStructureReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.ChunkRandom;

public class OceanMazeGenerator {

	public static enum CellType {
		END, STRAIGHT, CORNER, T_WAY, CROSS;
	}

	public static class Piece extends SimpleStructurePiece {

		private final BlockRotation rotation;
		private final Identifier template;

		public Piece(StructureManager structureManager, BlockPos pos, Identifier template, BlockRotation rotation) {
			super(OceanMazeStructureReg.SPT_OCEANMAZE, 0);
			this.pos = pos;
			this.rotation = rotation;
			this.template = template;
			this.initializeStructureData(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(OceanMazeStructureReg.SPT_OCEANMAZE, compoundTag);
			this.template = new Identifier(compoundTag.getString("Template"));
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.initializeStructureData(structureManager);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random,
				BlockBox boundingBox) {
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putString("Rot", this.rotation.name());
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData placementData = (new StructurePlacementData()).setRotation(this.rotation)
					.setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, placementData);
		}

	}
	
	public static final Identifier CORE = new Identifier("oceanmaze:oceanmaze/core");
	public static final Identifier STRAIGHT = new Identifier("oceanmaze:oceanmaze/straight");
	public static final Identifier CORNER = new Identifier("oceanmaze:oceanmaze/corner");
	public static final Identifier T_WAY = new Identifier("oceanmaze:oceanmaze/t_way");
	public static final Identifier CROSS = new Identifier("oceanmaze:oceanmaze/cross");
	public static final Identifier WALL_FACE = new Identifier("oceanmaze:oceanmaze/wall_face");
	public static final Identifier WALL_CORNER = new Identifier("oceanmaze:oceanmaze/wall_corner");
	public static final Identifier TOP_FACE = new Identifier("oceanmaze:oceanmaze/top_face");
	public static final Identifier TOP_EDGE = new Identifier("oceanmaze:oceanmaze/top_edge");
	public static final Identifier TOP_CORNER = new Identifier("oceanmaze:oceanmaze/top_corner");
	public static final Identifier BOTTOM_FACE = new Identifier("oceanmaze:oceanmaze/bottom_face");
	public static final Identifier BOTTOM_EDGE = new Identifier("oceanmaze:oceanmaze/bottom_edge");
	public static final Identifier BOTTOM_CORNER = new Identifier("oceanmaze:oceanmaze/bottom_corner");
	public static final Identifier END_LV0 = new Identifier("oceanmaze:oceanmaze/end_0");
	public static final Identifier END_LV1 = new Identifier("oceanmaze:oceanmaze/end_1");
	public static final Identifier END_LV2 = new Identifier("oceanmaze:oceanmaze/end_2");
	public static final Identifier END_LV3 = new Identifier("oceanmaze:oceanmaze/end_3");
	
	public static final int[] LAYERS = { 8, 9, 10, 11, 12 };
	public static final int[] WEIGHTS = { 5, 10, 15 };

	public static void addPieces(StructureManager manager, BlockPos pos, List<StructurePiece> children, ChunkRandom r,
			MazeConfig conf) {
		MazeGen[] mazes = new MazeGen[LAYERS.length];
		for (int i = 0; i < LAYERS.length; i++) {
			mazes[i] = new MazeGen(LAYERS[i], r, conf, new Debugger());
			mazes[i].gen();
			int[][] map = mazes[i].ans;
			for (int x = 0; x < mazes[i].w; x++)
				for (int z = 0; z < mazes[i].w; z++) {
					int dire = map[x][z];
					CellType ct = parse_ct(dire);
					Identifier id = parse_id(ct, r, Math.abs(x - mazes[i].r), Math.abs(z - mazes[i].r));
					children.add(new Piece(manager, pos, id, parse_rot(dire)));
				}
		}
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
		// TODO error
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
			if (x == 0 && z == 0)
				return CORE;
			int rand = r.nextInt(x + z);
			if (rand < WEIGHTS[0])
				return END_LV0;
			if (rand < WEIGHTS[1])
				return END_LV1;
			if (rand < WEIGHTS[2])
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
		if (cell == 4)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 8)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 3)
			return BlockRotation.NONE;
		if (cell == 12)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 9)
			return BlockRotation.NONE;
		if (cell == 6)
			return BlockRotation.CLOCKWISE_180;
		if (cell == 5)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 10)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 14)
			return BlockRotation.NONE;
		if (cell == 13)
			return BlockRotation.CLOCKWISE_180;
		if (cell == 7)
			return BlockRotation.COUNTERCLOCKWISE_90;
		if (cell == 11)
			return BlockRotation.CLOCKWISE_90;

		if (cell == 15)
			return BlockRotation.NONE;
		// TODO error
		return null;
	}

}
