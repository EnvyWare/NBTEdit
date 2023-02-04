package com.mcf.davidee.nbtedit.forge;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.nbt.SaveStates;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = NBTEdit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

	public static KeyBinding NBTEditKey;

	@SubscribeEvent
	public static void onModConstruct(FMLClientSetupEvent event) {
		SaveStates save = NBTEdit.getSaveStates();
		save.load();
		save.save();

		NBTEditKey = new KeyBinding("NBTEdit Shortcut", GLFW.GLFW_KEY_M, "key.categories.misc");
		ClientRegistry.registerKeyBinding(NBTEditKey);
	}
}
