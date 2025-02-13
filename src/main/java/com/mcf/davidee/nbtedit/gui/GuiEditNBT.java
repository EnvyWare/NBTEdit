package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import com.mcf.davidee.nbtedit.nbt.ParseHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
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

	private TextFieldWidget key, value;
	private Button save, cancel;
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
		section = new GuiCharacterButton(2,x + WIDTH - 1, y + 34, 0, 0);
		newLine = new GuiCharacterButton(1, x + WIDTH - 1, y + 50, 0, 0);
		String sKey = (key == null) ? node.getObject().getName() : key.getValue();
		String sValue = (value == null) ? getValue(nbt) : value.getValue();
		this.key = new TextFieldWidget(Minecraft.getInstance().font, x + 46, y + 18, 116, 15, new StringTextComponent(sKey));
		this.value = new TextFieldWidget(Minecraft.getInstance().font, x + 46, y + 44, 116, 15, new StringTextComponent(sValue));

		key.setValue(sKey);
		key.setEditable(canEditText);
		key.setMaxLength(256);
		key.setBordered(false);
		value.setValue(sValue);
		value.setEditable(canEditValue);
		value.setMaxLength(256);
		value.setBordered(false);

		save = new Button(x + 9, y + 62, 75, 20, new StringTextComponent("Save"), unused -> saveAndQuit());
		if (!key.isFocused() && !value.isFocused()) {
			if (canEditText)
				key.changeFocus(true);
			else if (canEditValue)
				value.changeFocus(true);
		}
		section.setEnabled(value.isFocused());
		newLine.setEnabled(value.isFocused());
		cancel = new Button(x + 93, y + 62, 75, 20, new StringTextComponent("Cancel"), unused -> parent.closeWindow());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseKey) {
		if (newLine.inBounds(mouseX, mouseY) && value.isFocused()) {
			value.setValue(value.getValue() + "\n");
			checkValidInput();
		} else if (section.inBounds(mouseX, mouseY) && value.isFocused()) {
			value.setValue(value.getValue() + NBTStringHelper.SECTION_SIGN);
			checkValidInput();
		} else {
			if (this.save.mouseClicked(mouseX, mouseY, mouseKey)) {
				return true;
			}

			if (this.cancel.mouseClicked(mouseX, mouseY, mouseKey)) {
				return true;
			}

			key.mouseClicked(mouseX, mouseY, 0);
			value.mouseClicked(mouseX, mouseY, 0);
			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
		}

		return true;
	}

	private void saveAndQuit() {
		if (canEditText)
			node.getObject().setName(key.getValue());
		setValidValue(node, value.getValue());
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

		key.render(matrix, mx, my, partialTicks);
		value.render(matrix, mx, my, partialTicks);

		save.render(matrix, mx, my, partialTicks);
		cancel.render(matrix, mx, my, partialTicks);

		if (kError != null)
			AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, kError, x + WIDTH / 2, y + 4, 0xFF0000);
		if (vError != null)
			AbstractGui.drawCenteredString(matrix, Minecraft.getInstance().font, vError, x + WIDTH / 2, y + 32, 0xFF0000);

		newLine.render(matrix, mx, my, partialTicks);
		section.render(matrix, mx, my, partialTicks);
	}

	public void tick() {
		value.tick();
		key.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.parent.closeWindow();
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_TAB) {
			if (key.isFocused() && canEditValue) {
				key.changeFocus(false);
				value.changeFocus(true);
			} else if (value.isFocused() && canEditText) {
				key.changeFocus(true);
				value.changeFocus(false);
			}
			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			checkValidInput();
			if (save.isFocused())
				saveAndQuit();
			return true;
		}

		key.keyPressed(keyCode, scanCode, modifiers);
		value.keyPressed(keyCode, scanCode, modifiers);
		checkValidInput();
		return true;
	}

	@Override
	public boolean charTyped(char p_231042_1_, int p_231042_2_) {
		if (key.charTyped(p_231042_1_, p_231042_2_)) {
			return true;
		}

		if (value.charTyped(p_231042_1_, p_231042_2_)) {
			checkValidInput();
			return true;
		}

		return super.charTyped(p_231042_1_, p_231042_2_);
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
			validValue(value.getValue(), nbt.getId());
		} catch (NumberFormatException e) {
			vError = e.getMessage();
			valid = false;
		}

		save.visible = valid;
	}

	private boolean validName() {
		for (Node<NamedNBT> node : this.node.getParent().getChildren()) {
			INBT base = node.getObject().getNBT();
			if (base != nbt && node.getObject().getName().equals(key.getValue()))
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
