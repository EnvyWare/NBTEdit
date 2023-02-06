package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.NBTEdit;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateTileNBTPacket implements Packet {

    protected BlockPos pos;
    protected CompoundNBT tag;

    public UpdateTileNBTPacket(BlockPos pos, CompoundNBT tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public UpdateTileNBTPacket() {
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeNbt(this.tag);
    }

    @Override
    public void decode(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.tag = buffer.readNbt();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            TileEntity te = sender.getLevel().getBlockEntity(this.pos);

            if (te == null || !NBTEdit.checkPermission(sender)) {
                return;
            }

            te.load(sender.getLevel().getBlockState(this.pos), this.tag);
            te.setChanged();

            if (te.hasLevel() && te.getLevel() instanceof ServerWorld) {
                te.getLevel().blockUpdated(this.pos, sender.getLevel().getBlockState(this.pos).getBlock());
            }

            NBTEdit.LOGGER.trace("{} edited a tag -- Tile Entity at {}, {}, {}", sender.getName(), this.pos.getX(), this.pos.getY(), this.pos.getZ());
            NBTEdit.logTag(this.tag);
            sender.sendMessage(new StringTextComponent("Your changes have been saved").withStyle(TextFormatting.WHITE), Util.NIL_UUID);
        });
    }
}
