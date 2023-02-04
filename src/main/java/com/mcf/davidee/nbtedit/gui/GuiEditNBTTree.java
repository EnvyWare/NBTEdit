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

import java.io.IOException;

public class GuiEditNBTTree extends Screen {

	public final int entityOrX, y, z;
	private boolean entity;
	protected String screenTitle;
	private GuiNBTTree guiTree;

	public GuiEditNBTTree(int entity, CompoundNBT tag) {
		super(StringTextComponent.EMPTY);
		this.entity = true;
		entityOrX = entity;
		y = 0;
		z = 0;
		screenTitle = "NBTEdit -- EntityId #" + entityOrX;
		guiTree = new GuiNBTTree(0, 0, 0, new NBTTree(tag));
	}

	public GuiEditNBTTree(BlockPos pos, CompoundNBT tag) {
		super(StringTextComponent.EMPTY);
		this.entity = false;
		entityOrX = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		screenTitle = "NBTEdit -- TileEntity at " + pos.getX() + "," + pos.getY() + "," + pos.getZ();
		guiTree = new GuiNBTTree(0, 0, 0, new NBTTree(tag));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		this.buttons.clear();
		this.addButton(new Button(0, 0, width / 4 - 100, this.height - 27, new StringTextComponent("Save"), context -> this.quitWithSave()));
		this.addButton(new Button(0, 0, width * 3 / 4 - 100, this.height - 27, new StringTextComponent("Quit"), p_onPress_1_ -> this.quitWithoutSaving()));
	}

//	protected void keyTyped(char par1, int key) {
//		GuiEditNBT window = guiTree.getWindow();
//		if (window != null)
//			window.keyTyped(par1, key);
//		else {
//			if (key == 1) {
//				if (guiTree.isEditingSlot())
//					guiTree.stopEditingSlot();
//				else
//					quitWithoutSaving();
//			} else if (key == Keyboard.KEY_DELETE)
//				guiTree.deleteSelected();
//			else if (key == Keyboard.KEY_RETURN)
//				guiTree.editSelected();
//			else if (key == Keyboard.KEY_UP)
//				guiTree.arrowKeyPressed(true);
//			else if (key == Keyboard.KEY_DOWN)
//				guiTree.arrowKeyPressed(false);
//			else
//				guiTree.keyTyped(par1, key);
//		}
//	}

	protected void mouseClicked(int x, int y, int t) throws IOException {
		if (guiTree.getWindow() == null)
			super.mouseClicked(x, y, t);
		if (t == 0)
			guiTree.mouseClicked(x, y);
		if (t == 1)
			guiTree.rightClick(x, y);
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
			PacketHandler.sendToServer(new EntityNBTPacket(entityOrX, guiTree.getNBTTree().toNBTTagCompound()));
		else
			PacketHandler.sendToServer(new TileNBTPacket(new BlockPos(entityOrX, y, z), guiTree.getNBTTree().toNBTTagCompound()));

		Minecraft.getInstance().setScreen(null);
	}

	private void quitWithoutSaving() {
		Minecraft.getInstance().setScreen(null);
	}

	@Override
	public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
		super.render(matrixStack, x, y, partialTicks);

		guiTree.render(matrixStack, x, y, partialTicks);
		AbstractGui.drawCenteredString(matrixStack, Minecraft.getInstance().font, this.screenTitle, this.width / 2, 5, 16777215);
		if (guiTree.getWindow() == null)
			super.render(matrixStack, x, y, partialTicks);
		else
			super.render(matrixStack, -1, -1, partialTicks);
	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	public Entity getEntity() {
		return entity ? Minecraft.getInstance().level.getEntity(entityOrX) : null;
	}

	public boolean isTileEntity() {
		return !entity;
	}

	public int getBlockX() {
		return entity ? 0 : entityOrX;
	}

}
