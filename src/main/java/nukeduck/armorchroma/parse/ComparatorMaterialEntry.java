package nukeduck.armorchroma.parse;

import java.util.Comparator;

import nukeduck.armorchroma.parse.IconData.IconGroup.MaterialEntry;

public class ComparatorMaterialEntry implements Comparator<MaterialEntry> {
	public static final ComparatorMaterialEntry INSTANCE = new ComparatorMaterialEntry();

	@Override
	public int compare(MaterialEntry arg0, MaterialEntry arg1) {
		return arg0.name.compareToIgnoreCase(arg1.name);
	}
}
