package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.nbt.SaveStates;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GuiSaveSlotButton extends Widget {

	public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");
	private static final int X_SIZE = 14, HEIGHT = 20, MAX_WIDTH = 150, MIN_WIDTH = 82, GAP = 3;


	public final SaveStates.SaveState save;
	private final int rightX;

	private String text;
	private boolean xVisible;

	private int tickCount;

	public GuiSaveSlotButton(SaveStates.SaveState save, int rightX, int y) {
		super(0, y, 0, 0, StringTextComponent.EMPTY);

		this.save = save;
		this.rightX = rightX;
		this.y = y;

		xVisible = !save.tag.isEmpty();
		text = (save.tag.isEmpty() ? "Save " : "Load ") + save.name;
		tickCount = -1;
		updatePosition();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int textColor = inBounds(mouseX, mouseY) ? 16777120 : 0xffffff;

		renderVanillaButton(matrixStack, x, y, 0, 66, width, HEIGHT);
		AbstractGui.drawCenteredString(matrixStack, Minecraft.getInstance().font, text, x + width / 2, y + 6, textColor);
		if (tickCount != -1 && tickCount / 6 % 2 == 0) {
			Minecraft.getInstance().font.drawShadow(matrixStack,"_", x + (width + Minecraft.getInstance().font.width(text)) / 2 + 1, y + 6, 0xffffff);
		}

		if (xVisible) {
			textColor = inBoundsOfX(mouseX, mouseY) ? 16777120 : 0xffffff;
			renderVanillaButton(matrixStack, leftBoundOfX(), topBoundOfX(), 0, 66, X_SIZE, X_SIZE);
			AbstractGui.drawCenteredString(matrixStack, Minecraft.getInstance().font, "x", x - GAP - X_SIZE / 2, y + 6, textColor);
		}
	}

	private void renderVanillaButton(MatrixStack matrix, int x, int y, int u, int v, int width, int height) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		//Top Left
		AbstractGui.blit(matrix, x, y, u, v, width / 2, height / 2, 256, 256);
		//Top Right 
		AbstractGui.blit(matrix, x + width / 2, y, u + 200 - width / 2, v, width / 2, height / 2, 256, 256);
		//Bottom Left
		AbstractGui.blit(matrix, x, y + height / 2, u, v + 20 - height / 2, width / 2, height / 2, 256, 256);
		//Bottom Right
		AbstractGui.blit(matrix, x + width / 2, y + height / 2, u + 200 - width / 2, v + 20 - height / 2, width / 2, height / 2, 256, 256);
	}

	private int leftBoundOfX() {
		return x - X_SIZE - GAP;
	}

	private int topBoundOfX() {
		return y + (HEIGHT - X_SIZE) / 2;
	}

	public boolean inBoundsOfX(double mx, double my) {
		int buttonX = leftBoundOfX();
		int buttonY = topBoundOfX();
		return xVisible && mx >= buttonX && my >= buttonY && mx < buttonX + X_SIZE && my < buttonY + X_SIZE;
	}

	public boolean inBounds(double mx, double my) {
		return mx >= x && my >= y && mx < x + width && my < y + HEIGHT;
	}

	private void updatePosition() {
		width = Minecraft.getInstance().font.width(text) + 24;
		if (width % 2 == 1)
			++width;
		width = MathHelper.clamp(width, MIN_WIDTH, MAX_WIDTH);
		x = rightX - width;
	}

	public void reset() {
		xVisible = false;
		save.tag = new CompoundNBT();
		text = "Save " + save.name;
		updatePosition();
	}

	public void saved() {
		xVisible = true;
		text = "Load " + save.name;
		updatePosition();
	}

	@Override
	public boolean charTyped(char c, int key) {
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			backSpace();
			return true;
		}
		if (Character.isDigit(c) || Character.isLetter(c)) {
			save.name += c;
			text = (save.tag.isEmpty() ? "Save " : "Load ") + save.name;
			updatePosition();
			return true;
		}

		return false;
	}

	public void backSpace() {
		if (save.name.length() > 0) {
			save.name = save.name.substring(0, save.name.length() - 1);
			text = (save.tag.isEmpty() ? "Save " : "Load ") + save.name;
			updatePosition();
		}
	}

	public void startEditing() {
		tickCount = 0;
	}

	public void stopEditing() {
		tickCount = -1;
	}


	public void update() {
		++tickCount;
	}

}
