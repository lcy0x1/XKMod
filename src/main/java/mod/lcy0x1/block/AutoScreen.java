package mod.lcy0x1.block;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public abstract class AutoScreen<T extends ScreenHandler> extends HandledScreen<T> {

	public AutoScreen(T handler, PlayerInventory inventory, Text title, int h) {
		super(handler, inventory, title);
		this.backgroundHeight = h;
		this.playerInventoryTitleY = h - 94;
	}

	public int getH() {
		return backgroundHeight;
	}

	public int getW() {
		return backgroundWidth;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
