package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class GuiNBTButton extends Widget {

	public static final int WIDTH = 9, HEIGHT = 9;

	private byte id;
	private boolean enabled;

	private long hoverTime;

	public GuiNBTButton(byte id, int x, int y) {
		super(x, y, 0, 0, StringTextComponent.EMPTY);

		this.id = id;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		Minecraft.getInstance().getTextureManager().bind(GuiNBTNode.WIDGET_TEXTURE);

		if (inBounds(mouseX, mouseY)) {//checks if the mouse is over the button
			AbstractGui.fill(matrix, x, y, x + WIDTH, y + HEIGHT, 0x80ffffff);//draw a grayish background
			if (hoverTime == -1)
				hoverTime = System.currentTimeMillis();
		} else
			hoverTime = -1;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (enabled)
			AbstractGui.blit(matrix, x, y, (id - 1) * 9, 18, WIDTH, HEIGHT, 256, 256);//Draw the texture

		if (hoverTime != -1 && System.currentTimeMillis() - hoverTime > 300) {
			drawToolTip(matrix, mouseX, mouseY);
		}

	}

	private void drawToolTip(MatrixStack matrix, int mx, int my) {
		String s = NBTStringHelper.getButtonName(id);
		int width = Minecraft.getInstance().font.width(s);
		AbstractGui.fill(matrix, mx + 4, my + 7, mx + 5 + width, my + 17, 0xff000000);
		Minecraft.getInstance().font.draw(matrix, s, mx + 5, my + 8, 0xffffff);
	}

	public void setEnabled(boolean aFlag) {
		enabled = aFlag;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean inBounds(int mx, int my) {
		return enabled && mx >= x && my >= y && mx < x + WIDTH && my < y + HEIGHT;
	}

	public byte getId() {
		return id;
	}
}
