package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class EntityNBTPacket implements Packet {

	protected int entityID;
	protected CompoundNBT tag;

	public EntityNBTPacket() {
	}

	public EntityNBTPacket(int entityID, CompoundNBT tag) {
		this.entityID = entityID;
		this.tag = tag;
	}

	@Override
	public void decode(PacketBuffer buf) {
		this.entityID = buf.readInt();
		this.tag = buf.readNbt();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(this.entityID);
		buf.writeNbt(this.tag);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handle(NetworkEvent.Context context) {
		Minecraft.getInstance().setScreen(new GuiEditNBTTree(this.entityID, this.tag));
	}
}
