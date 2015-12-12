package nukeduck.armorchroma.parse;

import java.util.List;

import nukeduck.armorchroma.parse.IconData.IconGroup.MaterialEntry;

public class IconData {
	public List<IconGroup> groups;
	public IconGroup[] groupsA;
	public MaterialEntry[] materialsA;

	public static class IconGroup {
		public String modid;

		public List<MaterialEntry> materials;
		public List<ItemEntry> items;

		public ItemEntry[] itemsA;

		public static class MaterialEntry {
			public String name;
			public int index;
		}

		public static class ItemEntry {
			public String id;
			public int index;
		}
	}
}
