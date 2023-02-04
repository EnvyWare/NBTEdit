package com.mcf.davidee.nbtedit.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class EntityRequestPacket implements Packet {

	private int entityID;

	public EntityRequestPacket() {
	}

	public EntityRequestPacket(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void decode(PacketBuffer buf) {
		this.entityID = buf.readInt();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(this.entityID);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayerEntity sender = context.getSender();
		context.enqueueWork(() -> PacketHandler.sendEntity(sender, this.entityID));
	}
}
