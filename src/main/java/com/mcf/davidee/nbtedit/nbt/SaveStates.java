package com.mcf.davidee.nbtedit.nbt;

import com.mcf.davidee.nbtedit.NBTEdit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

// This save format can definitely be improved. Also, this can be extended to provide infinite save slots - just
// need to add some scrollbar (use GuiLib!).
public class SaveStates {

	private File file;
	private SaveState[] tags;

	public SaveStates(File file) {
		this.file = file;
		tags = new SaveState[7];
		for (int i = 0; i < 7; ++i)
			tags[i] = new SaveState("Slot " + (i + 1));
	}

	public void read() throws IOException {
		if (file.exists() && file.canRead()) {
			CompoundNBT root = CompressedStreamTools.read(file);
			for (int i = 0; i < 7; ++i) {
				String name = "slot" + (i + 1);
				if (root.contains(name))
					tags[i].tag = root.getCompound(name);
				if (root.contains(name + "Name"))
					tags[i].name = root.getString(name + "Name");
			}
		}
	}

	public void write() throws IOException {
		CompoundNBT root = new CompoundNBT();
		for (int i = 0; i < 7; ++i) {
			root.put("slot" + (i + 1), tags[i].tag);
			root.putString("slot" + (i + 1) + "Name", tags[i].name);
		}
		CompressedStreamTools.write(root, file);
	}

	public void save() {
		try {
			write();
			NBTEdit.log(Level.TRACE, "NBTEdit saved successfully.");
		} catch (IOException e) {
			NBTEdit.LOGGER.catching(e);
		}
	}

	public void load() {
		try {
			read();
			NBTEdit.log(Level.TRACE, "NBTEdit save loaded successfully.");
		} catch (IOException e) {
			NBTEdit.LOGGER.catching(e);
		}
	}

	public SaveState getSaveState(int index) {
		return tags[index];
	}

	public static final class SaveState {
		public String name;
		public CompoundNBT tag;

		public SaveState(String name) {
			this.name = name;
			this.tag = new CompoundNBT();
		}
	}
}
