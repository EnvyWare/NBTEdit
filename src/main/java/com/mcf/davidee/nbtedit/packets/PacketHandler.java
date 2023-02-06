package com.mcf.davidee.nbtedit.packets;

import com.mcf.davidee.nbtedit.NBTEdit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {

	private static SimpleChannel instance;
	private static int packetId = 0;

	public static void initialize() {
		instance = NetworkRegistry.ChannelBuilder.named(ResourceLocation.tryParse(NBTEdit.MODID + ":main"))
				.serverAcceptedVersions(serverVersion -> {
					if (serverVersion.equalsIgnoreCase(NetworkRegistry.ABSENT) || serverVersion.equalsIgnoreCase(NetworkRegistry.ACCEPTVANILLA)) {
						return true;
					}

					return NBTEdit.VERSION.equals(serverVersion);
				})
				.clientAcceptedVersions(clientVersion -> {
					if (clientVersion.equalsIgnoreCase(NetworkRegistry.ABSENT) || clientVersion.equalsIgnoreCase(NetworkRegistry.ACCEPTVANILLA)) {
						return true;
					}

					return NBTEdit.VERSION.equals(clientVersion);
				})
				.networkProtocolVersion(() -> NBTEdit.VERSION)
				.simpleChannel();

		registerPackets();
	}

	private static void registerPackets() {
		registerPacket(TileRequestPacket.class, TileRequestPacket::new);
		registerPacket(TileNBTPacket.class, TileNBTPacket::new);
		registerPacket(EntityRequestPacket.class, EntityRequestPacket::new);
		registerPacket(EntityNBTPacket.class, EntityNBTPacket::new);
		registerPacket(MouseOverPacket.class, MouseOverPacket::new);
		registerPacket(UpdateTileNBTPacket.class, UpdateTileNBTPacket::new);
		registerPacket(UpdateEntityNBTPacket.class, UpdateEntityNBTPacket::new);
	}

	private static <T extends Packet> void registerPacket(Class<T> clazz, Supplier<T> constructor) {
		instance.registerMessage(packetId++, clazz, Packet::encode, packetBuffer -> decodeUsingSupplier(packetBuffer, constructor),
				(packet, context) -> packet.handle(context.get()));
	}

	private static <T extends Packet> T decodeUsingSupplier(PacketBuffer packetBuffer, Supplier<T> supplier) {
		T t = (T)supplier.get();

		try {
			t.decode(packetBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static void sendPacket(ServerPlayerEntity player, Packet packet) {
		instance.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	public static void sendToServer(Packet packet) {
		instance.sendToServer(packet);
	}

	/**
	 * Sends a TileEntity's nbt data to the player for editing.
	 *
	 * @param player The player to send the TileEntity to.
	 * @param pos    The block containing the TileEntity.
	 */
	public static void sendTile(ServerPlayerEntity player, final BlockPos pos) {
		if (NBTEdit.checkPermission(player)) {
			TileEntity te = player.getLevel().getBlockEntity(pos);

			if (te != null) {
				CompoundNBT tag = new CompoundNBT();
				te.save(tag);
				sendPacket(player, new TileNBTPacket(pos, tag));
			} else {
				player.sendMessage(new StringTextComponent(
						"Error - There is no TileEntity at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
						.withStyle(TextFormatting.RED), Util.NIL_UUID);
			}
		}
	}

	/**
	 * Sends a Entity's nbt data to the player for editing.
	 *
	 * @param player   The player to send the Entity data to.
	 * @param entityId The id of the Entity.
	 */
	public static void sendEntity(ServerPlayerEntity player, final int entityId) {
		if (NBTEdit.checkPermission(player)) {
			Entity entity = player.getLevel().getEntity(entityId);

			if (entity instanceof PlayerEntity && entity != player && !NBTEdit.editOtherPlayers) {
				player.sendMessage(new StringTextComponent("Error - You may not use NBTEdit on other Players")
						.withStyle(TextFormatting.RED), Util.NIL_UUID);
				return;
			}

			if (entity != null) {
				CompoundNBT tag = new CompoundNBT();
				entity.save(tag);
				sendPacket(player, new EntityNBTPacket(entityId, tag));
			}
		}
	}
}
