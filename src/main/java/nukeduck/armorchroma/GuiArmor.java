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
import static org.lwjgl.opengl.GL11.GL_QUADS;
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor;

/** Renders the armor bar in the HUD */
public class GuiArmor extends Gui {
	/** {@link ResourceLocation} for different armor icons
	 * @see #draw(int, int) */
	private static final ResourceLocation ARMOR_ICONS = new ResourceLocation(ArmorChroma.MODID, "textures/gui/armor_icons.png");

	private static final ResourceLocation BACKGROUND = new ResourceLocation(ArmorChroma.MODID, "textures/gui/background.png");

	/** {@link ResourceLocation} for item glints
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	/** A light purple tint used to draw item glints */
	private static final int GLINT_COLOR = 0x61309b;

	/** The colors used for the border of the bar at different levels
	 * @see #drawBackground(int, int, int) */
	private static final int[] BG_COLORS = {0x3acaff, 0x3be55a, 0xffff00, 0xff9d00, 0xed3200, 0x7130c1};

	/** The vertical distance between the top of each row */
	private static final int ROW_SPACING = 5;

	/** Maps icon indices onto their UV coordinates for armor icons */
	private static final IconAtlas ATLAS = new IconAtlas(9, 9);

	/** Render the bar as a full replacement for {@link GuiIngameForge#renderArmor(int, int)} */
	public void draw(int width, int height) {
		MINECRAFT.mcProfiler.startSection("armor");
		int left = width / 2 - 91;
		int top = height - GuiIngameForge.left_height;

		MINECRAFT.getTextureManager().bindTexture(ARMOR_ICONS);
		glEnable(GL_BLEND);

		// Total points in all rows so far
		int barPoints = 0;
		int[] armorPoints = getBarPoints(MINECRAFT.player);

		zLevel = -7;
		for(int i = 0; i < MINECRAFT.player.inventory.armorInventory.size(); i++) {
			if(armorPoints[i] > 0) {
				if(barPoints == 0) { // Draw background
					drawBackground(left, top, armorPoints[4]); // levels
					++zLevel;
				}

				drawPiece(left, top, barPoints, armorPoints[i], MINECRAFT.player.inventory.armorItemInSlot(i));
				barPoints += armorPoints[i];

				++zLevel; // Make sure blending works with GL_EQUAL
			}
		}

		// Let other bars draw in the correct position
		GuiIngameForge.left_height += (barPoints-1) / 20 * ROW_SPACING + 10;

		MINECRAFT.getTextureManager().bindTexture(ICONS);
		MINECRAFT.mcProfiler.endSection();
	}

	/** Draws the armor bar background with a border if {@code level > 0} */
	private void drawBackground(int x, int y, int level) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);

		// Plain background
		drawTexturedModalRect(x, y, 0, 0, 81, 9);

		// Colored border
		if(level > 0) {
			int color = level <= BG_COLORS.length ? BG_COLORS[level-1] : BG_COLORS[BG_COLORS.length-1];
			drawTexturedColoredModalRect(x-1, y-1, 81, 0, 83, 11, color);
		}
	}

	/** Draws all the rows needed for a single piece of armor
	 * @param barPoints The number of points in the bar before this piece */
	private void drawPiece(int left, int top, int barPoints, int stackPoints, ItemStack stack) {
		int space;
		top -= (barPoints / 20) * ROW_SPACING; // Offset to account for full bars

		// Repeatedly fill rows when possible
		while((space = 20 - (barPoints % 20)) <= stackPoints) {
			drawPartialRow(left, top, 20 - space, space, stack);

			// Move up a row
			top -= ROW_SPACING;
			zLevel -= 6;
			barPoints += space;
			stackPoints -= space;
		}

		// Whatever's left over (doesn't fill the whole row)
		if(stackPoints > 0) {
			drawPartialRow(left, top, 20 - space, stackPoints, stack);
		}
	}

	private int getColor(ItemStack stack) {
		if(stack != null) {
			Item item = stack.getItem();

			if(item instanceof ItemArmor) {
				return ((ItemArmor)item).getColor(stack);
			}
		}
		return 0xffffff;
	}

	/** Renders a partial row of icons, {@code stackPoints} wide
	 * @param barPoints The points already in the bar */
	private void drawPartialRow(int left, int top, int barPoints, int stackPoints, ItemStack stack) {
		int iconIndex = ArmorChroma.iconData.getIcon(stack);
		int color = getColor(stack);
		boolean glint = ArmorChroma.CONFIG.renderGlint && stack.hasEffect();

		if(glint) zLevel += 2; // Glint rows should appear on top of normal rows

		int u = ATLAS.getU(iconIndex), v = ATLAS.getV(iconIndex);
		int i = barPoints & 1;
		int x = left + barPoints * 4;

		// Drawing icons starts here

		Minecraft.getMinecraft().getTextureManager().bindTexture(ARMOR_ICONS);

		if(i == 1) { // leading half icon
			int rmaskIcon = ArmorChroma.iconData.getSpecial("rmask");
			drawTexturedMaskedModalRect(x - 4, top, u, v, ATLAS.getU(rmaskIcon), ATLAS.getV(rmaskIcon), 9, 9, color);
			x += 4;
		}
		for(; i < stackPoints - 1; i += 2, x += 8) { // Main body icons
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(stack.getItem().getRegistryName().getResourceDomain(), "textures/gui/armor_chroma.png"));
			drawTexturedColoredModalRect(x, top, u, v, 9, 9, color);
		}
		if(i < stackPoints) { // Trailing half icon
			int lmaskIcon = ArmorChroma.iconData.getSpecial("lmask");
			drawTexturedMaskedModalRect(x, top, u, v, ATLAS.getU(lmaskIcon), ATLAS.getV(lmaskIcon), 9, 9, color);
		}

		if(glint) { // Draw one glint quad for the whole row
			this.drawTexturedGlintRect(left + barPoints * 4, top, left, 0, stackPoints*4 + 1, 9);
			MINECRAFT.getTextureManager().bindTexture(ARMOR_ICONS);
			zLevel -= 2;
		}
	}

	/** Searches the player's armor inventory to work out how many points need
	 * to be shown. Will skip full bars if compression is turned on
	 * @return The player's armor points to display in the bar, along with
	 * the number of full bars skipped at index {@code 4} */
	private int[] getBarPoints(EntityPlayer player) {
		int[] points = new int[5]; // 5th slot stores bar levels
		int i = 0;
		points[4] = 0;

		// Skip full bars if compressing
		if(ArmorChroma.CONFIG.compressBar) {
			int ignore = ForgeHooks.getTotalArmorValue(player) - 1;
			ignore /= 20; // Number of full bars to ignore

			if(ignore > 0) {
				points[4] = ignore;
				ignore *= 20;

				for(; ignore > 0; i++) {
					ignore -= getArmorPoints(player, i);
				}
				// Put the overflow back in the bar
				points[i-1] = -ignore;
			}
		}

		// Fill in remaining slots
		for(; i < 4; i++) {
			points[i] = getArmorPoints(player, i);
		}
		return points;
	}

	/** @return The points gained from a single item in the player's armor
	 * @see ForgeHooks#getTotalArmorValue() */
	private static int getArmorPoints(EntityPlayer player, int slot) {
		ItemStack stack = player.inventory.armorItemInSlot(slot);

		if(stack != null) {
			Item item = stack.getItem();

			// Allow for custom calculations
			if(item instanceof ISpecialArmor) {
				return ((ISpecialArmor)item).getArmorDisplay(player, stack, slot);
			} else if(item instanceof ItemArmor) {
				return ((ItemArmor)item).damageReduceAmount;
			}
		}
		return 0;
	}

	/** As {@link #drawTexturedColoredModalRect(int, int, int, int, float, float, float, float, int)},
	 * but generates the interpolated texture coordinates and high vertex coordinates */
	public void drawTexturedColoredModalRect(int x, int y, int u, int v, int width, int height, int color) {
		final float texUnit = 1f / 256f;
		drawTexturedColoredModalRect(x, y, x + width, y + height, u * texUnit, v * texUnit, (u + width) * texUnit, (v + height) * texUnit, color);
	}

	/** Draws a textured and colored quad. Vertex coordinates are pixel-based, texture coordinates are interpolated.<br>
	 * Use {@link #drawTexturedColoredModalRect(int, int, int, int, int, int, int)} for a more useful interface */
	public void drawTexturedColoredModalRect(int left, int top, int right, int bottom, float texLeft, float texTop, float texRight, float texBottom, int color) {
		int r = (color >> 16) & 0xff;
		int g = (color >> 8)  & 0xff;
		int b =  color        & 0xff;
		GlStateManager.color(r / 255f, g / 255f, b / 255f, 1f);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(left, bottom, zLevel).tex(texLeft, texBottom).endVertex();
		buffer.pos(right, bottom, zLevel).tex(texRight, texBottom).endVertex();
		buffer.pos(right, top, zLevel).tex(texRight, texTop).endVertex();
		buffer.pos(left, top, zLevel).tex(texLeft, texTop).endVertex();

		tessellator.draw();
		GlStateManager.color(1f, 1f, 1f, 1f);
	}

	/** Draws a quad with the specified RGBA mask applied.<br>
	 * The same x, y, width and height will be used for both the quad and the mask
	 * @param maskU The U texture coordinate of the mask
	 * @param maskV The V texture coordinate of the mask
	 * @param color The color to draw the quad with */
	public void drawTexturedMaskedModalRect(int x, int y, int u, int v, int maskU, int maskV, int width, int height, int color) {
		drawTexturedModalRect(x, y, maskU, maskV, width, height);

		glDepthFunc(GL_EQUAL);
		glBlendFunc(GL_DST_COLOR, GL_ZERO);
		drawTexturedColoredModalRect(x, y, u, v, width, height, color);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthFunc(GL_LEQUAL);
	}

	/** Bits which are modified while rendering item glints, so must be pushed/popped.
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	private static final int GL_GLINT_BITS = GL_ENABLE_BIT | GL_TRANSFORM_BIT
		| GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT;

	/** Render an item glint over the specified quad, blending with equal depth */
	public void drawTexturedGlintRect(int x, int y, int u, int v, int width, int height) {
		// Push bits we modify to restore later on
		glPushAttrib(GL_GLINT_BITS);

		glDepthFunc(GL_EQUAL);
		OpenGlHelper.glBlendFunc(GL_SRC_COLOR, GL_ONE, GL_ONE, GL_ZERO);
		MINECRAFT.getTextureManager().bindTexture(GLINT);
		glMatrixMode(GL_TEXTURE); // Scale texture instead of vertex
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
	}
}
