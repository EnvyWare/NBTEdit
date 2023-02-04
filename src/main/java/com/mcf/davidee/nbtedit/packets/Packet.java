package com.mcf.davidee.nbtedit.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface Packet {

    void encode(PacketBuffer buffer);

    void decode(PacketBuffer buffer);

    void handle(NetworkEvent.Context context);

}
