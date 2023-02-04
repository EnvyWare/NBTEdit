package com.mcf.davidee.nbtedit.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.network.NetworkEvent;

public class MouseOverPacket implements Packet {

	public MouseOverPacket() {
	}

	@Override
	public void decode(PacketBuffer buf) {
	}

	@Override
	public void encode(PacketBuffer buf) {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		RayTraceResult pos = Minecraft.getInstance().hitResult;

		if (pos == null || pos.getType() == RayTraceResult.Type.MISS) {
			return;
		}

		if (pos.getType() == RayTraceResult.Type.ENTITY) {
			PacketHandler.sendToServer(new EntityRequestPacket(((EntityRayTraceResult) pos).getEntity().getId()));
		} else if (pos.getType() == RayTraceResult.Type.BLOCK) {
			PacketHandler.sendToServer(new TileRequestPacket(((BlockRayTraceResult) pos).getBlockPos()));
		}
	}
}
