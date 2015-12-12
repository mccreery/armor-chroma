package nukeduck.armorchroma.parse;

import java.util.Comparator;

import nukeduck.armorchroma.parse.IconData.IconGroup.ItemEntry;

public class ComparatorItemEntry implements Comparator<ItemEntry> {
	public static final ComparatorItemEntry INSTANCE = new ComparatorItemEntry();

	@Override
	public int compare(ItemEntry arg0, ItemEntry arg1) {
		return arg0.id.compareToIgnoreCase(arg1.id);
	}
}
