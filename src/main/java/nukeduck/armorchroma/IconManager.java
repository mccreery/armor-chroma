package nukeduck.armorchroma;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

/**
 * Singleton containing a map of armor icons and the corresponding texture.
 */
public class IconManager implements IResourceManagerReloadListener {
    private static final IconManager INSTANCE = new IconManager(
            new ResourceLocation(ArmorChroma.MODID, "textures/atlas/icons.png"),
            new IconTextureMap("textures/gui/armor", 9, ArmorChroma.MODID));

    private final ResourceLocation resourceLocation;
    private final TextureMap textureMap;

    public IconManager(ResourceLocation resourceLocation, TextureMap textureMap) {
        this.resourceLocation = resourceLocation;
        this.textureMap = textureMap;
    }

    public static IconManager getInstance() {
        return INSTANCE;
    }

    /**
     * Getter for the armor icons texture.
     *
     * @return A stitched texture corresponding to the armor icon texture map.
     * @see #getTextureMap()
     */
    public ResourceLocation getTexture() {
        return resourceLocation;
    }

    /**
     * Getter for the armor icons texture map.
     *
     * @return A texture map for looking up armor icons.
     * @see #getTexture()
     */
    public TextureMap getTextureMap() {
        return textureMap;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // TODO placeholder test icons
        List<ResourceLocation> sprites = Arrays.asList(
            new ResourceLocation(ArmorChroma.MODID, "test"),
            new ResourceLocation(ArmorChroma.MODID, "test2"),
            new ResourceLocation(ArmorChroma.MODID, "test3"),
            new ResourceLocation(ArmorChroma.MODID, "test4")
        );

        textureMap.loadSprites(resourceManager, map -> sprites.forEach(map::registerSprite));
        Minecraft.getMinecraft().renderEngine.loadTexture(resourceLocation, textureMap);
    }
}
