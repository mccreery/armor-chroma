package nukeduck.armorchroma;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nukeduck.armorchroma.config.ArmorIcon;
import nukeduck.armorchroma.config.Util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.client.render.item.ItemRenderer.ENCHANTED_ITEM_GLINT;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

/** Renders the armor bar in the HUD */
public class GuiArmor extends DrawableHelper {

    private static final Identifier BACKGROUND = new Identifier(ArmorChroma.MODID, "textures/gui/background.png");

    /** Speed of the enchanted glint texture */
    private static final double GLINT_U_SPEED = Math.cos(Math.PI / 3) / 16,
                                GLINT_V_SPEED = Math.sin(Math.PI / 3) / 16;

    /** The colors used for the border of the bar at different levels
     * @see #drawBackground(MatrixStack, int, int, int) */
    private static final int[] BG_COLORS = {0x3acaff, 0x3be55a, 0xffff00, 0xff9d00, 0xed3200, 0x7130c1};

    /** The vertical distance between the top of each row */
    private static final int ROW_SPACING = 5;

    /** Fallback attributes required when getting the player's armor */
    private static final DefaultAttributeContainer FALLBACK_ATTRIBUTES = DefaultAttributeContainer.builder()
    .add(EntityAttributes.GENERIC_ARMOR).build();

    private final MinecraftClient client = MinecraftClient.getInstance();

    /** Render the bar as a full replacement for vanilla */
    public void draw(MatrixStack matrices, int width, int top) {
        Map<EquipmentSlot, Integer> pointsMap = new LinkedHashMap<>();
        int totalPoints = getArmorPoints(client.player, pointsMap);
        if (totalPoints <= 0) return;

        int left = width / 2 - 91;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        // Total points in all rows so far
        int barPoints = 0;

        int compressedRows = ArmorChroma.config.compressBar() ? compressRows(pointsMap, totalPoints) : 0;

        // Accounts for the +2 glint rect offset
        setZOffset(-2);

        for(Entry<EquipmentSlot, Integer> entry : pointsMap.entrySet()) {
            //noinspection ConstantConditions (nullable stuff)
            drawPiece(matrices, left, top, barPoints, entry.getValue(), client.player.getEquippedStack(entry.getKey()));
            barPoints += entry.getValue();
        }
        // Most negative zOffset here
        drawBackground(matrices, left, top, compressedRows);

        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /** Draws the armor bar background with a border if {@code level > 0} */
    private void drawBackground(MatrixStack matrices, int x, int y, int level) {
        RenderSystem.setShaderTexture(0, BACKGROUND);

        // Plain background
        if (ArmorChroma.config.renderBackground()) {
            drawTexture(matrices, x, y, 0, 0, 81, 9);
        }

        // Colored border
        if(level > 0) {
            int color = level <= BG_COLORS.length ? BG_COLORS[level-1] : BG_COLORS[BG_COLORS.length-1];
            Util.setColor(color);
            drawTexture(matrices, x-1, y-1, 81, 0, 83, 11);
        }
    }

    /** Draws all the rows needed for a single piece of armor
     * @param barPoints The number of points in the bar before this piece */
    private void drawPiece(MatrixStack matrices, int left, int top, int barPoints, int stackPoints, ItemStack stack) {
        int space;
        top -= (barPoints / 20) * ROW_SPACING; // Offset to account for full bars

        // Repeatedly fill rows when possible
        while((space = 20 - (barPoints % 20)) <= stackPoints) {
            drawPartialRow(matrices, left, top, 20 - space, space, stack);
            moveZOffset(-3); // Move out of range of glint offset

            // Move up a row
            top -= ROW_SPACING;
            barPoints += space;
            stackPoints -= space;
        }

        // Whatever's left over (doesn't fill the whole row)
        if(stackPoints > 0) {
            drawPartialRow(matrices, left, top, 20 - space, stackPoints, stack);
            moveZOffset(-1);
        }
    }

    /** Renders a partial row of icons, {@code stackPoints} wide
     * @param barPoints The points already in the bar */
    private void drawPartialRow(MatrixStack matrices, int left, int top, int barPoints, int stackPoints, ItemStack stack) {
        ArmorIcon icon = ArmorChroma.ICON_DATA.getIcon(stack);
        RenderSystem.setShaderTexture(0, icon.texture);

        boolean glint = ArmorChroma.config.renderGlint() && stack.hasGlint();

        if(glint) moveZOffset(2); // Glint rows should appear on top of normal rows

        int i = barPoints & 1;
        int x = left + barPoints * 4;

        // Drawing icons starts here

        if(i == 1) { // leading half icon
            drawMaskedIcon(matrices, x - 4, top, icon, ArmorChroma.ICON_DATA.getSpecial(Util.getModid(stack), "leadingMask"));
            x += 4;
        }
        for(; i < stackPoints - 1; i += 2, x += 8) { // Main body icons
            icon.draw(matrices, this, x, top);
        }
        if(i < stackPoints) { // Trailing half icon
            drawMaskedIcon(matrices, x, top, icon, ArmorChroma.ICON_DATA.getSpecial(Util.getModid(stack), "trailingMask"));
        }

        if(glint) { // Draw one glint quad for the whole row
            this.drawTexturedGlintRect(matrices, left + barPoints * 4, top, left, 0, stackPoints*4 + 1, 9);
            moveZOffset(-2);
        }
    }

    /** Finds all items in the player's equipment slots that provide armor
     *
     * @param player The player holding the items
     * @param pointsMap The map of each slot's points
     * @return The total number of armor points the player has */
    private int getArmorPoints(ClientPlayerEntity player, Map<EquipmentSlot, Integer> pointsMap) {
        AttributeContainer attributes = new AttributeContainer(FALLBACK_ATTRIBUTES);
        EntityAttributeInstance armor = attributes.getCustomInstance(EntityAttributes.GENERIC_ARMOR);
        if (armor == null) return 0;

        int attrLast = (int) ((EntityAttributeInstanceAccess) armor).getUnclampedValue();

        for(EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            attributes.addTemporaryModifiers(stack.getAttributeModifiers(slot));

            int attrNext = (int) ((EntityAttributeInstanceAccess) armor).getUnclampedValue();
            int points = attrNext - attrLast;
            attrLast = attrNext;

            if(points > 0) pointsMap.put(slot, points);
        }
        return attrLast;
    }

    /** Removes leading full rows from the points map
     * @param pointsMap The map of slots to points, traversed in entrySet order
     * @return The number of compressed rows */
    private int compressRows(Map<EquipmentSlot, Integer> pointsMap, int totalPoints) {
        int compressedRows = (totalPoints - 1) / 20;
        int compressedPoints = compressedRows * 20;
        Iterator<Entry<EquipmentSlot, Integer>> it = pointsMap.entrySet().iterator();

        for(int i = 0; i < compressedPoints;) {
            Entry<EquipmentSlot, Integer> entry = it.next();
            int d = Math.min(compressedPoints - i, entry.getValue());

            if(d == entry.getValue()) {
                it.remove();
            } else {
                entry.setValue(entry.getValue() - d);
            }
            i += d;
        }
        return compressedRows;
    }

    public void drawMaskedIcon(MatrixStack matrices, int x, int y, ArmorIcon icon, ArmorIcon mask) {
        mask.draw(matrices, this, x, y);
        RenderSystem.depthFunc(GL_EQUAL);
        RenderSystem.blendFunc(GL_DST_COLOR, GL_ZERO);
        icon.draw(matrices, this, x, y);
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthFunc(GL_LEQUAL);
    }

    /** Render an item glint over the specified quad, blending with equal depth */
    public void drawTexturedGlintRect(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        RenderSystem.depthFunc(GL_EQUAL);
        RenderSystem.blendFuncSeparate(GL_SRC_COLOR, GL_ONE, GL_ONE, GL_ZERO);
        RenderSystem.setShaderTexture(0, ENCHANTED_ITEM_GLINT);
        RenderSystem.setShaderColor(.75f, .75f, .75f, 1);

        long time = System.currentTimeMillis();
        // Casting to long then int because casting directly from double to
        // int doesn't cause an overflow and returns Integer.MAX_VALUE instead
        u = (u - (int) (long) (time * GLINT_U_SPEED)) % 256;
        v = (v + (int) (long) (time * GLINT_V_SPEED)) % 256;
        drawTexture(matrices, x, y, u, v, width, height);

        RenderSystem.depthFunc(GL_LEQUAL);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void moveZOffset(int z) {
        setZOffset(getZOffset() + z);
    }

}
