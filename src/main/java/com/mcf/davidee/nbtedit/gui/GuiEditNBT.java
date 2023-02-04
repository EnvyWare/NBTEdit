package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import com.mcf.davidee.nbtedit.nbt.ParseHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GuiEditNBT extends Widget {

	public static final ResourceLocation WINDOW_TEXTURE = new ResourceLocation("nbtedit", "textures/gui/window.png");

	public static final int WIDTH = 178, HEIGHT = 93;

	private Node<NamedNBT> node;
	private INBT nbt;
	private boolean canEditText, canEditValue;
	private GuiNBTTree parent;

	private GuiTextField key, value;
	private GuiCharacterButton save, cancel;
	private String kError, vError;

	private GuiCharacterButton newLine, section;


	public GuiEditNBT(int x, int y, GuiNBTTree parent, Node<NamedNBT> node, boolean editText, boolean editValue) {
		super(x, y, 0, 0, StringTextComponent.EMPTY);

		this.parent = parent;
		this.node = node;
		this.nbt = node.getObject().getNBT();
		canEditText = editText;
		canEditValue = editValue;
	}


	public void init() {
		section = new GuiCharacterButton(x + WIDTH - 1, y + 34, 0, 0);
		newLine = new GuiCharacterButton(x + WIDTH - 1, y + 50, 0, 0);
		String sKey = (key == null) ? node.getObject().getName() : key.getText();
		String sValue = (value == null) ? getValue(nbt) : value.getText();
		this.key = new GuiTextField(x + 46, y + 18, 116, 15, false);
		this.value = new GuiTextField(x + 46, y + 44, 116, 15, true);

		key.setText(sKey);
		key.setEnableBackgroundDrawing(false);
		key.func_82265_c(canEditText);
		value.setMaxStringLength(256);
		value.setText(sValue);
		value.setEnableBackgroundDrawing(false);
		value.func_82265_c(canEditValue);
		save = new GuiCharacterButton(x + 9, y + 62, 75, 20);
		if (!key.isFocused() && !value.isFocused()) {
			if (canEditText)
				key.setFocused(true);
			else if (canEditValue)
				value.setFocused(true);
		}
		section.setEnabled(value.isFocused());
		newLine.setEnabled(value.isFocused());
		cancel = new GuiCharacterButton(x + 93, y + 62, 75, 20);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseKey) {
		if (newLine.inBounds(mouseX, mouseY) && value.isFocused()) {
			value.writeText("\n");
			checkValidInput();
		} else if (section.inBounds(mouseX, mouseY) && value.isFocused()) {
			value.writeText("" + NBTStringHelper.SECTION_SIGN);
			checkValidInput();
		} else {
			key.mouseClicked(mouseX, mouseY, 0);
			value.mouseClicked(mouseX, mouseY, 0);
			if (save.mouseClicked(mouseX, mouseY, 0))
				saveAndQuit();
			if (cancel.mouseClicked(mouseX, mouseY, 0))
				parent.closeWindow();
			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
		}

		return true;
	}

	private void saveAndQuit() {
		if (canEditText)
			node.getObject().setName(key.getText());
		setValidValue(node, value.getText());
		parent.nodeEdited(node);
		parent.closeWindow();
	}

	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks) {
		Minecraft.getInstance().getTextureManager().bind(WINDOW_TEXTURE);

		GL11.glColor4f(1, 1, 1, 1);
		AbstractGui.blit(matrix, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
		if (!canEditText)
			AbstractGui.fill(matrix, x + 42, y + 15, x + 169, y + 31, 0x80000000);
		if (!canEditValue)
			AbstractGui.fill(matrix, x + 42, y + 41, x + 169, y + 57, 0x80000000);
		key.drawTextBox(matrix);
		value.drawTextBox(matrix);

		save.render(matrix, mx, my, partialTicks);
		cancel.render(matrix, mx, my, partialTicks);

		if (kError != null)
			AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, kError, x + WIDTH / 2, y + 4, 0xFF0000);
		if (vError != null)
			AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, vError, x + WIDTH / 2, y + 32, 0xFF0000);

		newLine.render(matrix, mx, my, partialTicks);
		section.render(matrix, mx, my, partialTicks);
	}

	public void update() {
		value.updateCursorCounter();
		key.updateCursorCounter();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (i == GLFW.GLFW_KEY_ESCAPE) {
			parent.closeWindow();
			return true;
		} else if (i == GLFW.GLFW_KEY_TAB) {
			if (key.isFocused() && canEditValue) {
				key.setFocused(false);
				value.setFocused(true);
			} else if (value.isFocused() && canEditText) {
				key.setFocused(true);
				value.setFocused(false);
			}
			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
			return true;
		} else if (i == GLFW.GLFW_KEY_ENTER) {
			checkValidInput();
			if (save.isEnabled())
				saveAndQuit();
			return true;
		} else {
			key.textboxKeyTyped(c, i);
			value.textboxKeyTyped(c, i);
			checkValidInput();
			return true;
		}
	}

	private void checkValidInput() {
		boolean valid = true;
		kError = null;
		vError = null;
		if (canEditText && !validName()) {
			valid = false;
			kError = "Duplicate Tag Name";
		}
		try {
			validValue(value.getText(), nbt.getId());
			valid &= true;
		} catch (NumberFormatException e) {
			vError = e.getMessage();
			valid = false;
		}
		save.setEnabled(valid);
	}

	private boolean validName() {
		for (Node<NamedNBT> node : this.node.getParent().getChildren()) {
			INBT base = node.getObject().getNBT();
			if (base != nbt && node.getObject().getName().equals(key.getText()))
				return false;
		}
		return true;
	}

	private static void setValidValue(Node<NamedNBT> node, String value) {
		NamedNBT named = node.getObject();
		INBT base = named.getNBT();

		if (base instanceof ByteNBT)
			named.setNBT(ByteNBT.valueOf(ParseHelper.parseByte(value)));
		if (base instanceof ShortNBT)
			named.setNBT(ShortNBT.valueOf(ParseHelper.parseShort(value)));
		if (base instanceof IntNBT)
			named.setNBT(IntNBT.valueOf(ParseHelper.parseInt(value)));
		if (base instanceof LongNBT)
			named.setNBT(LongNBT.valueOf(ParseHelper.parseLong(value)));
		if (base instanceof FloatNBT)
			named.setNBT(FloatNBT.valueOf(ParseHelper.parseFloat(value)));
		if (base instanceof DoubleNBT)
			named.setNBT(DoubleNBT.valueOf(ParseHelper.parseDouble(value)));
		if (base instanceof ByteArrayNBT)
			named.setNBT(new ByteArrayNBT(ParseHelper.parseByteArray(value)));
		if (base instanceof IntArrayNBT)
			named.setNBT(new IntArrayNBT(ParseHelper.parseIntArray(value)));
		if (base instanceof StringNBT)
			named.setNBT(StringNBT.valueOf(value));
	}

	private static void validValue(String value, byte type) throws NumberFormatException {
		switch (type) {
			case 1:
				ParseHelper.parseByte(value);
				break;
			case 2:
				ParseHelper.parseShort(value);
				break;
			case 3:
				ParseHelper.parseInt(value);
				break;
			case 4:
				ParseHelper.parseLong(value);
				break;
			case 5:
				ParseHelper.parseFloat(value);
				break;
			case 6:
				ParseHelper.parseDouble(value);
				break;
			case 7:
				ParseHelper.parseByteArray(value);
				break;
			case 11:
				ParseHelper.parseIntArray(value);
				break;
		}
	}

	private static String getValue(INBT base) {
		switch (base.getId()) {
			case 7:
				StringBuilder s = new StringBuilder();
				for (byte b : ((ByteArrayNBT)base).getAsByteArray()) {
					s.append(b).append(" ");
				}
				return s.toString();
			case 9:
				return "TagList";
			case 10:
				return "TagCompound";
			case 11:
				StringBuilder i = new StringBuilder();
				for (int a : ((IntArrayNBT)base).getAsIntArray()) {
					i.append(a).append(" ");
				}
				return i.toString();
			default:
				return NBTStringHelper.toString(base);
		}
	}
}
