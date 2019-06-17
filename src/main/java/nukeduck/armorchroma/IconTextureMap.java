package nukeduck.armorchroma;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * A texture map which takes the given missing texture size.
 * Includes a domain so that block icons from other mods are blocked.
 */
public class IconTextureMap extends TextureMap {
    private final int iconSize;
    private final String domain;

    public IconTextureMap(String basePathIn, int iconSize, String domain) {
        super(basePathIn);
        this.iconSize = iconSize;
        this.domain = domain;
    }

    @Override
    public void loadTextureAtlas(IResourceManager resourceManager) {
        // Missing sprite must be resized before stitching happens
        generateMissingSprite();
        super.loadTextureAtlas(resourceManager);
    }

    @Override
    public boolean setTextureEntry(TextureAtlasSprite entry) {
        // Ignore sprites from other domains
        if(new ResourceLocation(entry.getIconName()).getResourceDomain().equals(domain)) {
            return super.setTextureEntry(entry);
        } else {
            return false;
        }
    }

    @Override
    public TextureAtlasSprite registerSprite(ResourceLocation location) {
        // Ignore sprites from other domains
        if(location.getResourceDomain().equals(domain)) {
            return super.registerSprite(location);
        } else {
            return null;
        }
    }

    /**
     * Replaces the default missing sprite with a dynamic one.
     */
    private void generateMissingSprite() {
        TextureAtlasSprite missingImage = getMissingSprite();
        missingImage.setIconWidth(iconSize);
        missingImage.setIconHeight(iconSize);

        // One mipmap, wrapped in a list of one frame
        int[][] mipData = new int[1][];
        mipData[0] = getMissingTextureData();
        missingImage.setFramesTextureData(Arrays.<int[][]>asList(mipData));
    }

    /**
     * Generates a black-magenta checkerboard texture with the current icon size.
     * @return The ARGB texture data for the checkerboard texture.
     */
    private int[] getMissingTextureData() {
        int[] data = new int[iconSize * iconSize];
        int center = (iconSize + 1) / 2;

        for(int y = 0; y < iconSize; y++) {
            for(int x = 0; x < iconSize; x++) {
                // xor generates checkerboard pattern
                boolean magenta = x >= center ^ y >= center;
                data[y * iconSize + x] = magenta ? 0xfff800f8 : 0xff000000;
            }
        }
        return data;
    }
}
