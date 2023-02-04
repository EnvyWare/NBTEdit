package com.mcf.davidee.nbtedit;

import com.mcf.davidee.nbtedit.nbt.NBTNodeSorter;
import com.mcf.davidee.nbtedit.nbt.NBTTree;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.SaveStates;
import com.mcf.davidee.nbtedit.packets.PacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.OpEntry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(NBTEdit.MODID)
@Mod.EventBusSubscriber(modid = NBTEdit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NBTEdit {

	public static final String MODID = "nbtedit";
	public static final String NAME = "In-game NBTEdit";
	public static final String VERSION = "1.11.2-2.0.2";
	public static final NBTNodeSorter SORTER = new NBTNodeSorter();
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static NamedNBT clipboard = null;
	public static boolean opOnly = true;
	public static boolean editOtherPlayers = false;
	private static SaveStates saves;

	@SubscribeEvent
	public static void preInit(FMLCommonSetupEvent event) {
		LOGGER.trace("NBTEdit Initalized");
		saves = new SaveStates(new File(new File(".", "saves"), "NBTEdit.dat"));
		PacketHandler.initialize();
	}

//	@SubscribeEvent
//	public static void serverStarting(FMLServerStartingEvent event) {
////		MinecraftServer server = event.getServer();
////		ServerCommandManager serverCommandManager = (ServerCommandManager) server.getCommandManager();
////		serverCommandManager.registerCommand(new CommandNBTEdit());
////		logger.trace("Server Starting -- Added \"/nbtedit\" command");
//	}

	public static void log(Level l, String s) {
		LOGGER.log(l, s);
	}

	public static void logTag(CompoundNBT tag) {
		NBTTree tree = new NBTTree(tag);
		StringBuilder sb = new StringBuilder();
		for (String s : tree.toStrings()) {
			sb.append(System.lineSeparator()).append("\t\t\t").append(s);
		}
		NBTEdit.log(Level.TRACE, sb.toString());
	}

	public static SaveStates getSaveStates() {
		return saves;
	}

	public static boolean checkPermission(ServerPlayerEntity player) {
		return true;
		/*return isOP(player) || PermissionAPI.hasPermission(player, NBTEdit.MODID);*/
	}

	public static boolean isOP(ServerPlayerEntity player) {
		OpEntry entry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile());
		return entry != null;
	}
}
