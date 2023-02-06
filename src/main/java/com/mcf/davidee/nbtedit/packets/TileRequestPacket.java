package com.mcf.davidee.nbtedit.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class TileRequestPacket implements Packet {

	private BlockPos pos;

	public TileRequestPacket() {
	}

	public TileRequestPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void decode(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayerEntity sender = context.getSender();
		context.enqueueWork(() -> PacketHandler.sendTile(sender, this.pos));
	}
}

