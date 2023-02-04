package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.nbt.NBTTree;
import com.mcf.davidee.nbtedit.packets.EntityNBTPacket;
import com.mcf.davidee.nbtedit.packets.PacketHandler;
import com.mcf.davidee.nbtedit.packets.TileNBTPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class GuiEditNBTTree extends Screen {

	public final int entityId;
	public final int y;
	public final int z;
	private boolean entity;
	protected String screenTitle;
	private GuiNBTTree guiTree;
	private CompoundNBT tag;

	public GuiEditNBTTree(int entity, CompoundNBT tag) {
		super(StringTextComponent.EMPTY);
		this.entity = true;
		entityId = entity;
		y = 0;
		z = 0;
		screenTitle = "NBTEdit -- EntityId #" + entityId;
		this.tag = tag;
	}

	public GuiEditNBTTree(BlockPos pos, CompoundNBT tag) {
		super(StringTextComponent.EMPTY);

		this.entity = false;
		this.entityId = -1;
		this.y = pos.getY();
		this.z = pos.getZ();
		screenTitle = "NBTEdit -- TileEntity at " + pos.getX() + "," + pos.getY() + "," + pos.getZ();
		this.tag = tag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.buttons.clear();
		this.addButton(new Button(width / 4 - 100, this.height - 27, 40, 20, new StringTextComponent("Save"), context -> this.quitWithSave()));
		this.addButton(new Button(width * 3 / 4 - 100, this.height - 27, 40, 20, new StringTextComponent("Quit"), context -> this.quitWithoutSaving()));
		guiTree = new GuiNBTTree(width, height, height - 35, new NBTTree(this.tag));
		this.setFocused(guiTree);
	}

	@Override
	public boolean charTyped(char character, int keyCode) {
		GuiEditNBT window = guiTree.getWindow();
		if (window != null)
			return window.charTyped(character, keyCode);
		else {
			if (keyCode == 1) {
				if (guiTree.isEditingSlot())
					guiTree.stopEditingSlot();
				else
					quitWithoutSaving();
			} else if (keyCode == GLFW.GLFW_KEY_DELETE)
				guiTree.deleteSelected();
			else if (keyCode == GLFW.GLFW_KEY_ENTER)
				guiTree.editSelected();
			else if (keyCode == GLFW.GLFW_KEY_UP)
				guiTree.arrowKeyPressed(true);
			else if (keyCode == GLFW.GLFW_KEY_DOWN)
				guiTree.arrowKeyPressed(false);
			else
				return guiTree.charTyped(character, keyCode);

			return true;
		}
	}

	@Override
	public void tick() {
		this.guiTree.updateScreen();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (guiTree.getWindow() == null)
			super.mouseClicked(mouseX, mouseY, button);
		if (button == 0) {
			this.setFocused(this.guiTree);
			return guiTree.mouseClicked(mouseX, mouseY, button);
		}
		if (button == 1) {
			this.setFocused(this.guiTree);
			return guiTree.rightClick(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta != 0) {
			this.guiTree.shift(delta >= 1 ? 6 : -6);
		}

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	private void quitWithSave() {
		if (entity)
			PacketHandler.sendToServer(new EntityNBTPacket(entityId, guiTree.getNBTTree().toNBTTagCompound()));
		else
			PacketHandler.sendToServer(new TileNBTPacket(new BlockPos(entityId, y, z), guiTree.getNBTTree().toNBTTagCompound()));

		Minecraft.getInstance().setScreen(null);
	}

	private void quitWithoutSaving() {
		Minecraft.getInstance().setScreen(null);
	}

	@Override
	public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
		matrixStack.pushPose();
		guiTree.render(matrixStack, x, y, partialTicks);

		AbstractGui.drawCenteredString(matrixStack, Minecraft.getInstance().font, this.screenTitle, this.width / 2, 5, 16777215);

		if (guiTree.getWindow() == null)
			super.render(matrixStack, x, y, partialTicks);
		else
			super.render(matrixStack, -1, -1, partialTicks);

		matrixStack.popPose();
	}

	public Entity getEntity() {
		return entity ? Minecraft.getInstance().level.getEntity(entityId) : null;
	}

	public boolean isTileEntity() {
		return !entity;
	}

	public int getBlockX() {
		return entity ? 0 : entityId;
	}
}
