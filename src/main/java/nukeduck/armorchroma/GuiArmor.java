package nukeduck.armorchroma;

import static nukeduck.armorchroma.ArmorChroma.MINECRAFT;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TRANSFORM_BIT;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ISpecialArmor;

public class GuiArmor extends Gui {
	/** {@link ResourceLocation} for item glints (textures/misc/enchanted_item_glint.png)
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	/** {@link ResourceLocation} for different armor icons (armorbars:textures/gui/armor_icons.png)
	 * @see #renderBar(int, int) */
	private static final ResourceLocation ARMOR_ICONS = new ResourceLocation(ArmorChroma.MODID, "textures/gui/armor_icons.png");

	private static final int ROW_SPACING = 5;
	private static final int GLINT_COLOR = 0x61309b;

	public static final IconAtlas ICON_MAP = new IconAtlas(9, 9);

	/** Renders the full armor bar.
	 * @param width The width of the scaled GUI, in pixels
	 * @param height The height of the scaled GUI, in pixels */
	public void renderBar(int width, int height) {
		MINECRAFT.mcProfiler.startSection("armor");
		int left = width / 2 - 91;
		int top = height - GuiIngameForge.left_height;
		GuiIngameForge.left_height -= ROW_SPACING;

		MINECRAFT.getTextureManager().bindTexture(ARMOR_ICONS);
		glEnable(GL_BLEND);

		// Total points in all rows so far
		int barPoints = 0;

		zLevel = -6;
		for(int i = 0; i < MINECRAFT.thePlayer.inventory.armorInventory.length; i++) {
			ItemStack stack = MINECRAFT.thePlayer.getCurrentArmor(i);
			int stackPoints = getArmorValue(MINECRAFT.thePlayer, stack, i);

			if(stackPoints > 0) {
				if(barPoints == 0) { // Draw background
					drawBackground(left, top, 0);
				}

				renderRows(left, top, barPoints, stackPoints, stack);
				barPoints += stackPoints;

				++zLevel;
			}
		}

		MINECRAFT.getTextureManager().bindTexture(icons);
		MINECRAFT.mcProfiler.endSection();
	}

	private void drawBackground(int x, int y, int level) {
		final int[] colors = {0x3acaff, 0x3be55a, 0xffff00, 0xff9d00, 0xed3200, 0x7130c1};

		drawTexturedModalRect(x, y, ICON_MAP.getU(-11), ICON_MAP.getV(-11), 81, 9);

		if(level > 0) {
			int color = level <= colors.length ? colors[level-1] : colors[colors.length-1];
			drawTexturedColoredModalRect(x-1, y-1, ICON_MAP.getU(-67)-1, ICON_MAP.getV(-67)-1, 83, 11, color);
		}
	}

	private void renderRows(int left, int top, int barPoints, int stackPoints, ItemStack stack) {
		int space;
		top -= (barPoints / 20) * ROW_SPACING;

		while((space = 20 - (barPoints % 20)) <= stackPoints) {
			renderRow(left, top, 20 - space, space, stack);

			top -= ROW_SPACING;
			zLevel -= 6;
			barPoints += space;
			stackPoints -= space;
		}

		if(stackPoints > 0) {
			renderRow(left, top, 20 - space, stackPoints, stack);
		}
	}

	/** Renders an armor piece of the given width, using the current state.
	 * @param left The leftmost X coordinate
	 * @param barPoints The points already in the bar
	 * @return The number of points filled */
	private void renderRow(int left, int top, int barPoints, int stackPoints, ItemStack stack) {
		int iconIndex = ArmorChroma.config.getIcon(stack);
		int color     = stack.getItem().getColorFromItemStack(stack, 0);
		boolean glint = ArmorChroma.config.renderGlint && stack.hasEffect();

		if(glint) zLevel += 2; // Glint rows should appear on top of normal rows

		int u = ICON_MAP.getU(iconIndex), v = ICON_MAP.getV(iconIndex);
		int i = barPoints & 1;
		int x = left + barPoints * 4;

		// Drawing icons starts here

		if(i == 1) { // leading half icon
			drawMaskedRect(x - 4, top, u, v, ICON_MAP.getU(-2), ICON_MAP.getV(-2), 9, 9, color);
			x += 4;
		}
		for(; i < stackPoints - 1; i += 2, x += 8) { // Main body icons
			drawTexturedColoredModalRect(x, top, u, v, 9, 9, color);
		}
		if(i < stackPoints) { // Trailing half icon
			drawMaskedRect(x, top, u, v, ICON_MAP.getU(-1), ICON_MAP.getV(-1), 9, 9, color);
		}

		if(glint) { // Draw one glint quad for the whole row
			this.drawTexturedGlintRect(left + barPoints * 4, top, left, 0, stackPoints*4 + 1, 9);
			zLevel -= 2;
		}
	}

	private static int getArmorValue(EntityPlayer player, ItemStack stack, int slot) {
		if(stack != null && stack.getItem() != null) {
			Item item = stack.getItem();

			if(item instanceof ItemArmor) {
				return ((ItemArmor)item).damageReduceAmount;
			} else if(item instanceof ISpecialArmor) {
				return ((ISpecialArmor)item).getArmorDisplay(player, stack, slot);
			}
		}
		return 0;
	}

	public void drawTexturedColoredModalRect(int x, int y, int u, int v, int width, int height, int color) {
		final float texUnit = 1f / 256f;
		drawTexturedColoredModalRect(x, y, x + width, y + height, u * texUnit, v * texUnit, (u + width) * texUnit, (v + height) * texUnit, color);
	}

	public void drawTexturedColoredModalRect(int left, int top, int right, int bottom, float texLeft, float texTop, float texRight, float texBottom, int color) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();

		worldRenderer.startDrawingQuads();
		worldRenderer.setColorOpaque_I(color);

		worldRenderer.addVertexWithUV(left,  bottom, zLevel, texLeft,  texBottom);
		worldRenderer.addVertexWithUV(right, bottom, zLevel, texRight, texBottom);
		worldRenderer.addVertexWithUV(right, top,    zLevel, texRight, texTop);
		worldRenderer.addVertexWithUV(left,  top,    zLevel, texLeft,  texTop);

		tessellator.draw();
	}

	/** Draws a quad with the specified RGBA mask applied.<br>
	 * The same x, y, width and height will be used for both the quad and the mask
	 * @param maskU The U texture coordinate of the mask
	 * @param maskV The V texture coordinate of the mask
	 * @param color The color to draw the quad with */
	public void drawMaskedRect(int x, int y, int u, int v, int maskU, int maskV, int width, int height, int color) {
		// TODO fix OpenGL bug where first quad cannot be reduced in alpha
		drawTexturedModalRect(x, y, maskU, maskV, width, height);

		glDepthFunc(GL_EQUAL);
		glBlendFunc(GL_DST_COLOR, GL_ZERO);
		drawTexturedColoredModalRect(x, y, u, v, width, height, color);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthFunc(GL_LEQUAL);
	}

	/** Bits which are modified while rendering item glints, so must be pushed/popped.
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	public static final int GL_GLINT_BITS =
			GL_ENABLE_BIT | /*GL_TEXTURE_BIT | */GL_TRANSFORM_BIT | // pushing texture bit doesn't save/restore bound texture
			GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT;

	/** Render an 'item glint' over the specified quad with the currently bound texture for clipping.
	 * @param x X coordinate of the top-left
	 * @param y Y coordinate of the top-left
	 * @param u Texture X coordinate, in pixels
	 * @param v Texture Y coordinate, in pixels
	 * @param width The width of the quad to draw, in pixels
	 * @param height The height of the quad to draw, in pixels
	 * @see ItemRenderer#renderItem(net.minecraft.entity.EntityLivingBase, ItemStack, int, net.minecraftforge.client.IItemRenderer.ItemRenderType) */
	public void drawTexturedGlintRect(int x, int y, int u, int v, int width, int height) {
		// Push bits we modify to restore later on
		glPushAttrib(GL_GLINT_BITS);

		glDepthFunc(GL_EQUAL);
		OpenGlHelper.glBlendFunc(GL_SRC_COLOR, GL_ONE, GL_ONE, GL_ZERO);

		MINECRAFT.getTextureManager().bindTexture(RES_ITEM_GLINT);

		glMatrixMode(GL_TEXTURE); // Scale texture instead of coords

		long time = Minecraft.getSystemTime();

		// Rect #1
		glPushMatrix();
		glScalef(0.125F, 0.125F, 0.125F);
		glTranslatef((float) (time % 3000L) / 3000.0F * 8.0F, 0.0F, 0.0F);
		glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		this.drawTexturedColoredModalRect(x, y, u, v, width, height, GLINT_COLOR);
		glPopMatrix();

		// Rect #2
		glPushMatrix();
		glScalef(0.125F, 0.125F, 0.125F);
		glTranslatef((float) (time % 4873L) / 4873.0F * -8.0F, 0.0F, 0.0F);
		glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
		this.drawTexturedColoredModalRect(x, y, u, v, width, height, GLINT_COLOR);
		glPopMatrix();

		glPopAttrib();

		MINECRAFT.getTextureManager().bindTexture(ARMOR_ICONS);
	}
}
