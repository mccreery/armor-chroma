package nukeduck.armorchroma.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ArmorIcon {
    private static final int TEXTURE_SIZE = 256;
    private static final int ICON_SIZE = 9;

    private static final int SPAN = TEXTURE_SIZE / ICON_SIZE;

    private static final String TEXTURE_PATH = "textures/gui/armor_chroma.png";

    public final Identifier texture;
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
        texture = new Identifier(modid, TEXTURE_PATH);

        if(i >= 0) {
            u = (i % SPAN) * ICON_SIZE;
            v = (i / SPAN) * ICON_SIZE;
        } else {
            u = TEXTURE_SIZE + (i % SPAN) * ICON_SIZE;
            v = TEXTURE_SIZE + ((i+1) / SPAN - 1) * ICON_SIZE;
        }
        this.color = color;
    }

    public void draw(MatrixStack matrices, DrawableHelper gui, int x, int y) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        Util.setColor(color);
        gui.drawTexture(matrices, x, y, u, v, ICON_SIZE, ICON_SIZE);
    }
}
