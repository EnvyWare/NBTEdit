package com.mcf.davidee.nbtedit.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class GuiCharacterButton extends Widget {

	public static final int WIDTH = 14;
	public static final int HEIGHT = 14;

	private int id;
	private boolean enabled = false;

	public GuiCharacterButton(int id, int x, int y, int width, int height) {
		super(x, y, width, height, StringTextComponent.EMPTY);

		this.id = id;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		Minecraft.getInstance().getTextureManager().bind(GuiNBTNode.WIDGET_TEXTURE);

		if (this.inBounds(mouseX, mouseY)) {
			AbstractGui.fill(matrix, x, y, x + WIDTH, y + HEIGHT, 0x80ffffff);
		}

		if (this.active) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
		}

		 AbstractGui.blit(matrix, x, y, this.id * WIDTH, 27, WIDTH, HEIGHT, 256, 256);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean inBounds(double mx, double my) {
		return this.enabled && mx >= x && my >= y && mx < x + WIDTH && my < y + HEIGHT;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
