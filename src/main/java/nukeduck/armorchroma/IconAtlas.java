package nukeduck.armorchroma;

/** Maps icon indices onto their UV coordinates.<br>
 * Negative indices are mapped onto the bottom right of the texture
 * instead of the top left */
public class IconAtlas {
	public final int iconWidth, iconHeight;

	/** The number of icons that can fit in a row of the texture */
	private final int span;

	public static final int TEXTURE_SIZE = 256;

	public IconAtlas(int iconWidth, int iconHeight) {
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		span = TEXTURE_SIZE / iconWidth;
	}

	/** @return The U texture coordinate of the icon at {@code iconIndex} */
	public int getU(int iconIndex) {
		int u = (iconIndex % span) * iconWidth;
		if(u < 0) u += TEXTURE_SIZE;

		return u;
	}

	/** @return The V texture coordinate of the icon at {@code iconIndex} */
	public int getV(int iconIndex) {
		if(iconIndex >= 0) {
			return (iconIndex / span) * iconHeight;
		} else {
			// Adjust for 1-based index
			return (TEXTURE_SIZE) + ((iconIndex + 1) / span - 1) * iconHeight;
		}
	}
}
