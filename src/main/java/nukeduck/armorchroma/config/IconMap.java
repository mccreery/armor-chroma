package nukeduck.armorchroma.config;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class IconMap implements Map<Object, Integer> {
	private final List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();

	protected abstract String getName(ItemStack stack);

	private String normalizeKey(Object key) {
		if(key instanceof ItemStack) {
			return getName((ItemStack)key);
		} else if(key instanceof String) {
			return (String)key;
		} else {
			return null;
		}
	}

	@Override
	public Integer get(Object key) {
		String name = normalizeKey(key);

		for(Entry<String, Integer> entry : entries) {
			if(matches(entry.getKey(), name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Integer put(Object key, Integer value) {
		String name = normalizeKey(key);

		Integer existing = get(name);
		entries.add(new SimpleEntry<String, Integer>(name, value));
		return existing;
	}

	@Override
	public Integer remove(Object key) {
		String name = normalizeKey(key);

		for(Iterator<Entry<String, Integer>> it = entries.iterator(); it.hasNext(); ) {
			Entry<String, Integer> entry = it.next();
			if(matches(entry.getKey(), name)) {
				it.remove();
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public void clear() {
		entries.clear();
	}

	@Override
	public Set<Object> keySet() {throw new UnsupportedOperationException();}
	@Override
	public Set<java.util.Map.Entry<Object, Integer>> entrySet() {throw new UnsupportedOperationException();}
	@Override
	public Collection<Integer> values() {throw new UnsupportedOperationException();}
	@Override
	public boolean containsKey(Object key) {throw new UnsupportedOperationException();}
	@Override
	public boolean containsValue(Object value) {throw new UnsupportedOperationException();}
	@Override
	public void putAll(Map<? extends Object, ? extends Integer> m) {throw new UnsupportedOperationException();}

	/** @return {@code true} if the name matches the template with wildcards */
	private static boolean matches(String template, String name) {
		int tOffset, nOffset = 0, cardLength;
		int wild = template.indexOf('*');

		// Test leading card (if wild == -1 then the region always matches)
		if(!template.regionMatches(0, name, 0, wild)) {
			return false;
		}
		cardLength = wild;

		// Test *card until no more wildcards are left
		while(true) {
			// Card starts after nOffset, template wildcard is at tOffset
			tOffset = wild+1;
			nOffset += cardLength;

			wild = template.indexOf('*', tOffset);

			// Loop break condition - the next card is at the end of the string
			if(wild == -1) {
				cardLength = template.length() - tOffset; // Length of the card
				return template.regionMatches(tOffset, name, name.length() - cardLength, cardLength);
			}

			cardLength = wild - tOffset;
			nOffset = indexOfRegion(name, nOffset, template, tOffset, cardLength);

			// Loop break condition - the current card is not present
			if(nOffset == -1) {
				return false;
			}
		}
	}

	/** @return The lowest {@code i >= fromIndex} such that
	 * the first {@code length} characters of {@code haystack} starting at {@code i}
	 * match those of {@code needle} starting at {@code nOffset}
	 * @see String#startsWith(String, int) */
	private static int indexOfRegion(String haystack, int fromIndex, String needle, int nOffset, int length) {
		for(int i = fromIndex; i <= haystack.length() - length; i++) {
			if(haystack.regionMatches(i, needle, nOffset, length)) {
				return i;
			}
		}
		return -1;
	}

	public static class ItemIconMap extends IconMap {
		@Override
		protected String getName(ItemStack stack) {
			ResourceLocation id = (ResourceLocation)Item.itemRegistry.getNameForObject(stack.getItem());
			return id.getResourcePath();
		}
	}

	public static class MaterialIconMap extends IconMap {
		@Override
		protected String getName(ItemStack stack) {
			Item item = stack.getItem();

			if(item instanceof ItemArmor) {
				return ((ItemArmor)item).getArmorMaterial().getName();
			} else {
				return null;
			}
		}
	}
}
