package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class TileNBTPacket implements Packet {

	protected BlockPos pos;
	protected CompoundNBT tag;

	public TileNBTPacket() {
	}

	public TileNBTPacket(BlockPos pos, CompoundNBT tag) {
		this.pos = pos;
		this.tag = tag;
	}

	@Override
	public void decode(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.tag = buf.readNbt();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);
		buf.writeNbt(this.tag);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handle(NetworkEvent.Context context) {
		Minecraft.getInstance().setScreen(new GuiEditNBTTree(this.pos, this.tag));
	}
}
