package nukeduck.armorchroma.parse;

import java.util.Comparator;

import nukeduck.armorchroma.parse.IconData.IconGroup;

public class ComparatorIconGroup implements Comparator<IconGroup> {
	public static final ComparatorIconGroup INSTANCE = new ComparatorIconGroup();

	@Override
	public int compare(IconGroup arg0, IconGroup arg1) {
		return arg0.modid.compareToIgnoreCase(arg1.modid);
	}
}
