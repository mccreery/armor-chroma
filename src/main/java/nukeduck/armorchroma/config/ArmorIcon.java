package nukeduck.armorchroma.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ArmorIcon {
    private static final int TEXTURE_SIZE = 256;
    private static final int ICON_SIZE = 9;

    private static final int SPAN = TEXTURE_SIZE / ICON_SIZE;

    private static final String TEXTURE_PATH = "textures/gui/armor_chroma.png";

    public final ResourceLocation texture;
    public final int u, v;
    public final int color;

    public ArmorIcon(int i) {
        this(null, i, 0xffffff);
    }

    public ArmorIcon(int i, int color) {
        this(null, i, color);
    }

    public ArmorIcon(String modid, int i) {
        this(modid, i, 0xffffff);
    }

    public ArmorIcon(String modid, int i, int color) {
        texture = new ResourceLocation(modid, TEXTURE_PATH);

        u = Math.floorMod(i, SPAN) * ICON_SIZE;
        v = Math.floorMod(Math.floorDiv(i, SPAN), SPAN) * ICON_SIZE;
        this.color = color;
    }

    public void draw(Gui gui, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Util.setColor(color);
        gui.drawTexturedModalRect(x, y, u, v, ICON_SIZE, ICON_SIZE);
    }
}
