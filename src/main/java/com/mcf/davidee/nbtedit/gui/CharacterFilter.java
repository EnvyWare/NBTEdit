package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;

public class CharacterFilter {
	public static String filerAllowedCharacters(String str, boolean section) {
		StringBuilder sb = new StringBuilder();
		char[] arr = str.toCharArray();
		for (char c : arr) {
			if ((section && (c == NBTStringHelper.SECTION_SIGN || c == '\n')))
				sb.append(c);
		}

		return sb.toString();
	}
}
