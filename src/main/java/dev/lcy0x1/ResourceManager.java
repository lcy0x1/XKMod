package dev.lcy0x1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ResourceManager {

	private final AssetGen assetgen;

	private class AssetGen {

		private final String path, BS_, IM_, BM_, IM_B, LT_;

		private AssetGen() {
			path = "./resources/" + MODID + "/";
			BS_ = readFile(path + "BS/-templates/-.json");
			BM_ = readFile(path + "BM/-templates/-.json");
			IM_ = readFile(path + "IM/-templates/-.json");
			IM_B = readFile(path + "IM/-templates/-block.json");
			LT_ = readFile(path + "BL/-templates/-.json");

		}

		private void addBlockAssets(String block) throws IOException {
			write(BS + block + ".json", BS_.replaceAll("\\^", block));
			write(BM + block + ".json", BM_.replaceAll("\\^", block));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private void addBlockItemAssets(String block) throws IOException {
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private void addItemAssets(String item) throws IOException {
			write(IM + item + ".json", IM_.replaceAll("\\^", item));
		}

		private void addItemAssets(String item, String res) throws IOException {
			write(IM + item + ".json", IM_.replaceAll("\\^", res));
		}

		private void addLootTable(String block) throws IOException {
			write(BL + block + ".json", LT_.replaceAll("\\^", block));
		}

		private String readFile(String path) {
			List<String> list = null;
			try {
				list = Files.readLines(new File(path), Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			String str = "";
			for (String s : list)
				str += s + "\n";
			return str.replaceAll("\\^modid", MODID);
		}

		private void write(String name, String cont) throws IOException {
			File f = new File(name);
			check(f);
			PrintStream ps = new PrintStream(f);
			ps.println(cont);
			ps.close();
		}

	}

	private class AssetMove {

		private class BSConfig {

			public final String name;
			public final String bs, im;
			public final String[] bm;

			public BSConfig(String str, Map<String, BSConfig> map) {
				JsonObject obj = new JsonParser().parse(str).getAsJsonObject();
				name = obj.get("-name").getAsString();
				bs = assetgen.readFile(assetgen.path + "BS/-templates/-" + obj.get("-bs").getAsString() + ".json");
				JsonArray arr = obj.get("-bm").getAsJsonArray();
				bm = new String[arr.size()];
				for (int i = 0; i < bm.length; i++) {
					bm[i] = assetgen.readFile(assetgen.path + "BM/-templates/-" + arr.get(i).getAsString() + ".json");
				}
				im = obj.has("-im")
						? assetgen.readFile(assetgen.path + "IM/-templates/-" + obj.get("-im").getAsString() + ".json")
						: null;
				map.put(name, this);
			}

			public void run(String block) throws IOException {
				assetgen.write(BS + block + ".json", bs.replaceAll("\\^", block));
				if (bm.length == 1)
					assetgen.write(BM + block + ".json", bm[0].replaceAll("\\^", block));
				else
					for (int i = 0; i < bm.length; i++)
						assetgen.write(BM + block + "_" + i + ".json", bm[i].replaceAll("\\^", block + "_" + i));
				if (im != null)
					assetgen.write(IM + block + ".json", im.replaceAll("\\^", block));
			}

		}

		private final Map<String, String> PATHMAP = new HashMap<>();

		private AssetMove() {
			PATHMAP.put("ASSETS", PATH_ASSET);
			PATHMAP.put("DATA", PATH_DATA);
			PATHMAP.put("BS", BS);
			PATHMAP.put("BM", BM);
			PATHMAP.put("BT", BT);
			PATHMAP.put("BL", BL);
			PATHMAP.put("IM", IM);
			PATHMAP.put("IT", IT);
			PATHMAP.put("R", R);

		}

		public void organize() throws IOException {
			delete(new File(PATH_ASSET));
			delete(new File(PATH_DATA));
			new GUIGen().gen();
			orgImpl("ASSETS");
			orgImpl("DATA");
			orgBlocks();
			orgItems();
			new RecipeGen().gen();
			orgImpl("R");
		}

		private void copyTo(File file, String path) throws IOException {
			File f = new File(path);
			check(f);
			Files.copy(file, f);
		}

		private void orgBlocks() throws IOException {
			orgImpl("BT");
			Map<String, List<String>> map;
			map = readJson(assetgen.path + "BL/-info.json");
			List<String> ignore = map.get("-ignore");
			for (String key : map.keySet()) {
				if (key.startsWith("-"))
					continue;
				String template = assetgen.readFile(assetgen.path + "BL/-templates/-" + key + ".json");
				for (String str : map.get(key)) {
					JsonObject obj = new JsonParser().parse(str).getAsJsonObject();
					String ans = template;
					for (Entry<String, JsonElement> ent : obj.entrySet()) {
						if (ent.getKey().startsWith("-"))
							continue;
						ans = ans.replaceAll("\\^" + ent.getKey(), ent.getValue().getAsString());
					}
					assetgen.write(BL + obj.get("-name").getAsString() + ".json", ans);
					ignore.add(obj.get("-name").getAsString());
				}
			}
			map = readJson(assetgen.path + "BS/-info.json");
			List<String> blocks = orgImpl("BS");
			for (String block : blocks)
				assetgen.addBlockItemAssets(block);
			for (String block : map.get(""))
				assetgen.addBlockAssets(block);
			Map<String, BSConfig> config = new HashMap<>();
			map.get("-setup").forEach(str -> new BSConfig(str, config));
			for (String key : map.keySet()) {
				if (key.startsWith("-") || key.length() == 0)
					continue;
				for (String str : map.get(key))
					config.get(key).run(str);
			}
			map.forEach((k, v) -> {
				if (!k.startsWith("-"))
					blocks.addAll(v);
			});
			for (String block : blocks)
				if (!ignore.contains(block))
					assetgen.addLootTable(block);
			orgImpl("BM");
			orgImpl("BL");
		}

		private void orgImpl(File file, List<String> list, String path, String str) throws IOException {
			String name = file.getName();
			char ch = name.charAt(0);
			if (ch == '.' || ch == '-')
				return;
			else if (ch >= 'A' && ch <= 'Z') {
				path = PATHMAP.get(name);
				str = "";
			} else if (ch == '@') {
				path += name.substring(1) + "/";
				str = "";
			} else if (ch == '_') {
				str += name;
			} else if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z') {
				String[] ss = name.split("\\.");
				if (ss[0].endsWith("_"))
					str = ss[0] + str + (ss.length > 1 ? "." + ss[1] : "");
				else
					str = name;
			} else {
				System.out.println("invalid filename: " + path + ", " + str);
			}
			if (file.isDirectory()) {
				for (File fi : file.listFiles())
					orgImpl(fi, list, path, str);
			} else {
				if (list != null)
					list.add(str.split("\\.")[0]);
				copyTo(file, path + str);
			}
		}

		private List<String> orgImpl(String path) throws IOException {
			List<String> list = new ArrayList<>();
			orgImpl(new File(assetgen.path + path + "/"), list, PATHMAP.get(path), "");
			return list;
		}

		private void orgItems() throws IOException {
			Map<String, List<String>> map = readJson(assetgen.path + "IT/-info.json");
			List<String> list = orgImpl("IT");
			for (String item : list) {
				if (!map.get("ignore").contains(item))
					assetgen.addItemAssets(item);
			}
			map = readJson(assetgen.path + "IM/-info.json");
			for (Entry<String, List<String>> ent : map.entrySet()) {
				String key = ent.getKey();
				int lo = key.charAt(0) - '0';
				int hi = key.charAt(2) - '0';
				for (String str : ent.getValue())
					for (int i = lo; i <= hi; i++)
						assetgen.addItemAssets(str.replaceAll("\\^", "_" + i), str.replaceAll("\\^", ""));
			}
			orgImpl("IM");
		}

	}

	private class GUIGen {

		private class Comp {

			private final String name;
			private final Item it;
			private final int x, y, rx, ry;

			private Comp(String str, JsonObject e) {
				name = str;
				it = ITEM_MAP.get(e.get("sprite").getAsString());
				x = e.get("x").getAsInt();
				y = e.get("y").getAsInt();
				rx = getInt(e, "rx", 1);
				ry = getInt(e, "ry", 1);
			}

			@Override
			public String toString() {
				return name;
			}

			private void draw(Graphics g, int cx, int cy) throws IOException {
				for (int i = 0; i < rx; i++)
					for (int j = 0; j < ry; j++)
						g.drawImage(it.getImg(), cx + i * it.w, cy + j * it.h, null);
			}

			private int gety0() {
				return y - it.h / 2;
			}

			private int gety1() {
				return gety0() + ry * it.h;
			}

		}

		private class Item {

			private final String name, app;
			private final int w, h, dx, dy;

			private BufferedImage bimg;

			private Item(String str, String appe, JsonObject e) {
				ITEM_MAP.put(appe == null ? str : str + appe, this);
				name = str;
				app = appe;
				w = e.get("w").getAsInt();
				h = e.get("h").getAsInt();
				dx = getInt(e, "dx", 0);
				dy = getInt(e, "dy", 0);
			}

			@Override
			public String toString() {
				return app == null ? name : name + app;
			}

			private BufferedImage getImg() throws IOException {
				if (bimg != null)
					return bimg;
				String path = GUI + "-templates/sprites/" + name;
				if (app != null)
					path += "/" + app;
				path += ".png";
				return bimg = ImageIO.read(new File(path));
			}

		}

		private final String GUI = assetgen.path + "ASSETS/@textures/@gui/";

		private final Map<String, Item> ITEM_MAP = new HashMap<>();

		private void gen() throws IOException {
			readSprites();
			File f = new File(GUI + "-templates/container/");
			Item top = ITEM_MAP.get("top");
			Item middle = ITEM_MAP.get("middle");
			Item bottom = ITEM_MAP.get("bottom");
			for (File fi : f.listFiles()) {
				JsonObject e = readJsonFile(fi.getPath()).getAsJsonObject();
				JsonObject out = new JsonObject();
				List<Item> side = new ArrayList<>();
				List<Comp> comp = new ArrayList<>();
				e.get("side").getAsJsonArray().forEach(s -> side.add(ITEM_MAP.get(s.getAsString())));
				for (Entry<String, JsonElement> ent : e.get("comp").getAsJsonObject().entrySet())
					comp.add(new Comp(ent.getKey(), ent.getValue().getAsJsonObject()));
				int y0 = 0, y1 = 0;
				for (Comp c : comp) {
					y0 = Math.min(y0, c.gety0());
					y1 = Math.max(y1, c.gety1());
				}
				out.addProperty("height", top.h + y1 - y0 + bottom.h);
				BufferedImage bimg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bimg.getGraphics();
				g.drawImage(top.getImg(), 0, 0, null);
				for (int i = 0; i < y1 - y0; i++)
					g.drawImage(middle.getImg(), 0, top.h + i, null);
				g.drawImage(bottom.getImg(), 0, top.h + y1 - y0, null);
				JsonObject jarr = new JsonObject();
				for (Comp c : comp) {
					int cx = c.x - c.it.w / 2;
					int cy = c.y - c.it.h / 2 - y0 + top.h;
					c.draw(g, cx, cy);
					JsonObject co = new JsonObject();
					co.addProperty("x", cx + c.it.dx);
					co.addProperty("y", cy + c.it.dy);
					co.addProperty("w", c.it.w);
					co.addProperty("h", c.it.h);
					jarr.add(c.name, co);
				}
				out.add("comp", jarr);
				int dx = 0;
				JsonObject jside = new JsonObject();
				for (Item s : side) {
					JsonObject so = new JsonObject();
					so.addProperty("x", top.w);
					so.addProperty("y", dx);
					so.addProperty("w", s.w);
					so.addProperty("h", s.h);
					jside.add(s.toString(), so);
					g.drawImage(s.getImg(), top.w, dx, null);
					dx += s.h;
				}
				out.add("side", jside);
				g.dispose();
				File fx = new File(GUI + "@container/generated/" + fi.getName().split("\\.")[0] + ".png");
				check(fx);
				ImageIO.write(bimg, "PNG", fx);
				write(GUI + "@coords/" + fi.getName(), out);

			}

		}

		private int getInt(JsonObject e, String key, int def) {
			return e.has(key) ? e.get(key).getAsInt() : def;
		}

		private void readSprites() throws IOException {
			JsonElement e = readJsonFile(GUI + "-templates/info.json");
			e.getAsJsonObject().entrySet().forEach(ent -> {
				String name = ent.getKey();
				JsonObject o = ent.getValue().getAsJsonObject();
				if (o.has("ids"))
					o.get("ids").getAsJsonArray().forEach(ele -> new Item(name, ele.getAsString(), o));
				else
					new Item(name, null, o);
			});
		}

		private void write(String path, JsonObject obj) throws IOException {
			File fy = new File(path);
			check(fy);
			JsonWriter jw = new JsonWriter(Files.newWriter(fy, Charset.defaultCharset()));
			jw.setLenient(true);
			jw.setIndent("\t");
			Streams.write(obj, jw);
			jw.close();
		}

	}

	private class RecipeGen {

		private void gen() throws IOException {
			Map<String, List<String>> map = readJson(assetgen.path + "R/-info.json");
			for (String template : map.keySet()) {
				String tmpl = assetgen.readFile(assetgen.path + "R/-templates/-" + template + ".json");
				for (String str : map.get(template)) {
					JsonObject obj = new JsonParser().parse(str).getAsJsonObject();
					String rec = tmpl;
					for (Entry<String, JsonElement> ent : obj.entrySet()) {
						String key = ent.getKey();
						if (key.startsWith("-"))
							continue;
						String val = ent.getValue().getAsString();
						rec = rec.replaceAll("\\^" + key, val);
					}
					assetgen.write(R + obj.get("-name").getAsString() + ".json", rec);
				}
			}
		}

	}

	private String MODID = "";
	private final String PATH_PRE, PATH_ASSET, PATH_DATA;
	private final String BS, BM, BT, BL, IM, IT, R;

	public static void main(String[] strs) throws IOException {
		new ResourceManager("xinke");
		new ResourceManager("oceanmaze");
	}

	private ResourceManager(String modid) throws IOException {
		MODID = modid;

		PATH_PRE = "./src/main/resources/";
		PATH_ASSET = PATH_PRE + "assets/" + MODID + "/";
		PATH_DATA = PATH_PRE + "data/" + MODID + "/";
		BS = PATH_ASSET + "blockstates/";
		BM = PATH_ASSET + "models/block/";
		BT = PATH_ASSET + "textures/block/";
		BL = PATH_DATA + "loot_tables/blocks/";
		IM = PATH_ASSET + "models/item/";
		IT = PATH_ASSET + "textures/item/";
		R = PATH_DATA + "recipes/";

		assetgen = new AssetGen();
		new AssetMove().organize();
	}

	public static void check(File f) throws IOException {
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			f.createNewFile();
	}

	private static void delete(File f) {
		if (!f.exists())
			return;
		if (f.isDirectory())
			for (File fi : f.listFiles())
				delete(fi);
		f.delete();
	}

	private static Map<String, List<String>> readJson(String path) throws IOException {
		JsonElement e = readJsonFile(path);
		Map<String, List<String>> ans = new HashMap<>();
		e.getAsJsonObject().entrySet().forEach(ent0 -> ent0.getValue().getAsJsonObject().entrySet().forEach(ent1 -> {
			String key = ent1.getKey();
			List<String> list;
			if (ans.containsKey(key))
				list = ans.get(key);
			else
				ans.put(key, list = new ArrayList<>());
			ent1.getValue().getAsJsonObject().entrySet().forEach(ent2 -> {
				String group = ent2.getKey();
				ent2.getValue().getAsJsonArray().forEach(ent3 -> {
					String name = ent3.isJsonObject() ? ent3.toString() : ent3.getAsString();
					if (name.startsWith("_") || name.startsWith("^"))
						list.add(group + name);
					else if (name.endsWith("_"))
						list.add(name + group);
					else
						list.add(name);
				});
			});
		}));
		return ans;
	}

	private static JsonElement readJsonFile(String path) throws IOException {
		File f = new File(path);
		JsonReader r = new JsonReader(Files.newReader(f, Charset.defaultCharset()));
		JsonElement e = new JsonParser().parse(r);
		r.close();
		return e;
	}

}
