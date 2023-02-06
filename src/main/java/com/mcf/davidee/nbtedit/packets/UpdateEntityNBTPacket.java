package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.NBTEdit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateEntityNBTPacket implements Packet {

    protected int entityId;
    protected CompoundNBT tag;

    public UpdateEntityNBTPacket(int entityId, CompoundNBT tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    public UpdateEntityNBTPacket() {
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeNbt(this.tag);
    }

    @Override
    public void decode(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.tag = buffer.readNbt();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            Entity entity = sender.getLevel().getEntity(this.entityId);

            if (entity == null || !NBTEdit.checkPermission(sender)) {
                return;
            }

            entity.load(this.tag);

            NBTEdit.LOGGER.trace("{} edited a tag -- Entity ID #{}", sender.getName(), this.entityId);
            NBTEdit.logTag(this.tag);
            sender.sendMessage(new StringTextComponent("Your changes have been saved").withStyle(TextFormatting.WHITE), Util.NIL_UUID);
        });
    }
}
