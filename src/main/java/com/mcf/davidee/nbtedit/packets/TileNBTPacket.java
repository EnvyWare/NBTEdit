package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
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

	@Override
	public void handle(NetworkEvent.Context context) {
		Minecraft.getInstance().setScreen(new GuiEditNBTTree(this.pos, this.tag));
	}
//
//	public static class Handler implements IMessageHandler<TileNBTPacket, IMessage> {
//
//		@Override
//		public IMessage onMessage(final TileNBTPacket packet, MessageContext ctx) {
//			if (ctx.side == Side.SERVER) {
//				final EntityPlayerMP player = ctx.getServerHandler().player;
//				player.getServerWorld().addScheduledTask(new Runnable() {
//					@Override
//					public void run() {
//						TileEntity te = player.getServerWorld().getTileEntity(packet.pos);
//						if (te != null && NBTEdit.proxy.checkPermission(player)) {
//							try {
//								te.readFromNBT(packet.tag);
//								te.markDirty();// Ensures changes gets saved to disk later on.
//								if (te.hasWorld() && te.getWorld() instanceof WorldServer) {
//									((WorldServer) te.getWorld()).getPlayerChunkMap().markBlockForUpdate(packet.pos);// Broadcast changes.
//								}
//								NBTEdit.log(Level.TRACE, player.getName() + " edited a tag -- Tile Entity at " + packet.pos.getX() + ", " + packet.pos.getY() + ", " + packet.pos.getZ());
//								NBTEdit.logTag(packet.tag);
//								NBTEdit.proxy.sendMessage(player, "Your changes have been saved", TextFormatting.WHITE);
//							} catch (Throwable t) {
//								NBTEdit.proxy.sendMessage(player, "Save Failed - Invalid NBT format for Tile Entity", TextFormatting.RED);
//								NBTEdit.log(Level.WARN, player.getName() + " edited a tag and caused an exception");
//								NBTEdit.logTag(packet.tag);
//								NBTEdit.throwing("TileNBTPacket", "Handler.onMessage", t);
//							}
//						} else {
//							NBTEdit.log(Level.WARN, player.getName() + " tried to edit a non-existent TileEntity at " + packet.pos.getX() + ", " + packet.pos.getY() + ", " + packet.pos.getZ());
//							NBTEdit.proxy.sendMessage(player, "cSave Failed - There is no TileEntity at " + packet.pos.getX() + ", " + packet.pos.getY() + ", " + packet.pos.getZ(), TextFormatting.RED);
//						}
//					}
//				});
//			} else {
//				NBTEdit.proxy.openEditGUI(packet.pos, packet.tag);
//			}
//			return null;
//		}
//	}
}
