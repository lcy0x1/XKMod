package dev.lcy0x1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import mod.lcy0x1.block.AutoScreen;
import mod.xinke.main.XinkeMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.JsonHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class SpriteManager {

	public static class Rect {

		public static final Rect ZERO = new Rect(0, 0, 0, 0);

		public final int x, y, w, h;

		private Rect(int x0, int y0, int w0, int h0) {
			x = x0;
			y = y0;
			w = w0;
			h = h0;
		}

	}

	@Environment(EnvType.CLIENT)
	public class ScreenRenderer {

		private final int x, y, w, h;
		private final AutoScreen<?> scr;
		private MatrixStack mt;

		private ScreenRenderer(AutoScreen<?> scrIn) {
			x = scrIn.getX();
			y = scrIn.getY();
			w = scrIn.getW();
			h = scrIn.getH();
			scr = scrIn;
		}

		public void draw(String c, String s) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			scr.drawTexture(mt, x + cr.x, y + cr.y, sr.x, sr.y, sr.w, sr.h);
		}

		public void drawBottomUp(String c, String s, double per) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			int dh = (int) Math.round(sr.h * per);
			scr.drawTexture(mt, x + cr.x, y + cr.y + sr.h - dh, sr.x, sr.y + sr.h - dh, sr.w, dh);
		}

		public void drawLeftRight(String c, String s, double per) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			int dw = (int) Math.round(sr.w * per);
			scr.drawTexture(mt, x + cr.x, y + cr.y, sr.x, sr.y, dw, sr.h);
		}

		public void start(MatrixStack ms) {
			mt = ms;
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
			scr.drawTexture(mt, x, y, 0, 0, w, h);
		}

	}

	public static interface SlotFactory<T extends Slot> {

		public T getSlot(int x, int y);

	}

	private final String name;
	private final Map<String, Rect> side = new HashMap<>();
	private final Map<String, Rect> comp = new HashMap<>();
	private final Identifier coords, texture;

	private int height = 0;
	private boolean loaded = false;

	public SpriteManager(String mod, String str) {
		name = mod + ":" + str;
		coords = new Identifier(mod, "/textures/gui/coords/" + str + ".json");
		texture = new Identifier(mod, "/textures/gui/container/" + str + ".png");
		check();
	}

	public Rect getComp(String key) {
		check();
		return comp.containsKey(key) ? comp.get(key) : Rect.ZERO;
	}

	public int getHeight() {
		check();
		return height;
	}

	public int getPIH() {
		check();
		return height - 82;
	}

	@Environment(EnvType.CLIENT)
	public ScreenRenderer getRenderer(AutoScreen<?> blit) {
		check();
		return new ScreenRenderer(blit);
	}

	public Rect getSide(String key) {
		check();
		return side.containsKey(key) ? side.get(key) : Rect.ZERO;
	}

	public <T extends Slot> T getSlot(String key, SlotFactory<T> fac) {
		check();
		Rect c = getComp(key);
		return fac.getSlot(c.x, c.y);
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean within(String key, double x, double y) {
		check();
		Rect c = getComp(key);
		return x > c.x && x < c.x + c.w && y > c.y && y < c.y + c.h;
	}

	private void check() {
		if (!loaded && XinkeMod.isPhysicalClient)
			load();
	}

	private void load() {
		try {
			Resource r = MinecraftClient.getInstance().getResourceManager().getResource(coords);
			JsonObject jo = JsonHelper.deserialize(new InputStreamReader(r.getInputStream()));
			height = JsonHelper.getInt(jo, "height");
			JsonHelper.getObject(jo, "side").entrySet().forEach(ent -> {
				JsonObject co = ent.getValue().getAsJsonObject();
				int x = JsonHelper.getInt(co, "x");
				int y = JsonHelper.getInt(co, "y");
				int w = JsonHelper.getInt(co, "w");
				int h = JsonHelper.getInt(co, "h");
				side.put(ent.getKey(), new Rect(x, y, w, h));
			});
			JsonHelper.getObject(jo, "comp").entrySet().forEach(ent -> {
				JsonObject co = ent.getValue().getAsJsonObject();
				int x = JsonHelper.getInt(co, "x");
				int y = JsonHelper.getInt(co, "y");
				int w = JsonHelper.getInt(co, "w");
				int h = JsonHelper.getInt(co, "h");
				comp.put(ent.getKey(), new Rect(x, y, w, h));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		loaded = true;
	}

}
