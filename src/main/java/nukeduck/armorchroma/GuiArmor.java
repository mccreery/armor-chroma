package nukeduck.armorchroma;

import static nukeduck.armorchroma.ArmorChroma.mc;
import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ISpecialArmor;

public class GuiArmor extends Gui {
	/** {@link ResourceLocation} for item glints (textures/misc/enchanted_item_glint.png)
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	/** {@link ResourceLocation} for different armor icons (armorbars:textures/gui/armor_icons.png)
	 * @see #renderArmorBar(int, int) */
	private static final ResourceLocation ARMOR_ICONS = new ResourceLocation(ArmorChroma.MODID, "textures/gui/armor_icons.png");

	/** Resets the state of the bar. */
	private void resetState() {
		this.zLevel = 0;
		this.last = this.next = 0;
		this.materialIndex = 0;
		this.glint = false;
		this.color = 0xFFFFFF;
	}

	private static final int ROW_SPACING = 5;

	/** Moves the bar onto the next line and resets the left offset. */
	private void nextRow() {
		GuiIngameForge.left_height += ROW_SPACING;
		this.top = this.height - GuiIngameForge.left_height;
		this.zLevel--;

		this.left = 0;
	}

	/** Holds the current armor material's icon index. */
	private int materialIndex;
	/** Holds the current armor color. */
	private int color;
	/** Holds the current glint flag. */
	private boolean glint;
	/** {@link #last} holds the previous cumulative armor, {@link #next} holds the current cumulative armor. */
	private int last, next;
	/** {@link #left} holds the current left offset, {@link top} holds the current top of the bar and {@link height} holds the window height. */
	private int left, top, height;

	/** Renders the full armor bar.
	 * @param width The width of the scaled GUI, in pixels
	 * @param height The height of the scaled GUI, in pixels */
	public void renderArmorBar(int width, int height) {
		mc.mcProfiler.startSection("armor");
		this.resetState();
		int left = width / 2 - 91;
		GuiIngameForge.left_height -= ROW_SPACING;
		this.height = height;

		glPushAttrib(GL_TEXTURE_BIT);
		mc.getTextureManager().bindTexture(ARMOR_ICONS);

		for(int i = 0; i < mc.thePlayer.inventory.armorInventory.length; i++) {
			ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
			if(stack == null || stack.getItem() == null) continue;

			boolean addBreak = this.setState(stack, i);
			this.renderArmorBarPart(left, addBreak);

			if(this.last == 0 && this.next > 0) { // First armor, draw the background
				this.zLevel--;
				this.drawTexturedModalRect(left, this.top, 175, 247, 82, 9);
				this.zLevel++;
			}

			// Move forward in the bar
			this.last = this.next;
		}
		// Draw extra line at the end of the bar if necessary
		if(last % 2 == 1) {
			this.drawTexturedModalRect(left + this.left - 4, this.top, 166, 247, 9, 9);
		}

		glPopAttrib();
		mc.mcProfiler.endSection();
	}

	/** Sets the state of the bar based on the given stack.
	 * @param stack The {@link ItemStack} to read from
	 * @param slot The armor slot (0=boots, 1=leggings, 2=chestplate, 3=helmet)
	 * @return {@code true} if a break should be rendered at the start of this piece */
	public boolean setState(ItemStack stack, int slot) {
		Item item = stack.getItem();
		boolean addBreak = ArmorChroma.INSTANCE.config.alwaysBreak ||
				this.glint != (this.glint = item.hasEffect(stack, 0));

		boolean leather = item instanceof ItemArmor && (item == Items.leather_helmet || item == Items.leather_chestplate || item == Items.leather_leggings || item == Items.leather_boots);
		if(!ArmorChroma.INSTANCE.config.renderColor && leather) {
			// Use pre-colored leather
			addBreak |= this.materialIndex != (this.materialIndex = ArmorChroma.INSTANCE.config.iconLeather);
			addBreak |= this.color != (this.color = 0xFFFFFF);
		} else {
			addBreak |= this.materialIndex != (this.materialIndex = ArmorChroma.INSTANCE.config.getIcon(stack));
			if(leather) {
				addBreak |= this.color != (this.color = item.getColorFromItemStack(stack, 0));
			} else {
				addBreak |= this.color != (this.color = 0xFFFFFF);
			}
		}

		this.next = this.last;
		if(item instanceof ItemArmor) {
			this.next += ((ItemArmor) item).damageReduceAmount;
		} else if(item instanceof ISpecialArmor) {
			this.next += ((ISpecialArmor) item).getArmorDisplay(mc.thePlayer, stack, slot);
		}

		return addBreak;
	}

	/** The amount of 9x9 tiles that fit horizontally in the tile sheet. */
	public static final int SHEET_WIDTH = 28;

	/** Calculates UV coordinates, in pixels, for the current quad.
	 * @param pos The index in the armor bar, starting at 0 and ending at 19
	 * @return A vector containing UV pixel coordinates */
	public Vector2d getUV(int pos) {
		int offsetX = pos % 2 == 1 ? 4 : 0;
		return new Vector2d(
				(this.materialIndex % SHEET_WIDTH) * 9 + offsetX,
				(this.materialIndex / SHEET_WIDTH) * 9);
	}

	/** Renders an armor piece of the given width, using the current state.
	 * @param left The X position to start at
	 * @param top The Y position to start at
	 * @param last The previous cumulative armor
	 * @param next The current cumulative armor
	 * @param addBreak Whether or not to add a break at the start of the bar part, if necessary */
	private void renderArmorBarPart(int left, boolean addBreak) {
		for(int i = this.last; i < this.next; i++, this.left += 4) {
			if(i % 20 == 0) {
				this.nextRow();
			}
			Vector2d uv = this.getUV(i);
			this.drawTexturedModalRectWithColor(left + this.left, this.top, (int) uv.x, (int) uv.y, 5, 9);

			if(addBreak) { // Add a break if the state of the bar has changed
				addBreak = false;
				if(i % 2 == 1) {
					this.drawTexturedModalRectWithColor(left + this.left - 4, this.top, 166, 247, 9, 9);
				}
			}
			if(ArmorChroma.INSTANCE.config.renderGlint && glint) { // Draw glint
				this.drawTexturedGlintRect(left + this.left, this.top, (int) uv.x, (int) uv.y, 5, 9);
			}
		}
	}

	/** Draws a textured rectangle at the stored z-value.
	 * @param x X coordinate of the top-left
	 * @param y Y coordinate of the top-left
	 * @param u Texture X coordinate, in pixels
	 * @param v Texture Y coordinate, in pixels
	 * @param width The width of the quad to draw, in pixels
	 * @param height The height of the quad to draw, in pixels
	 * @param renderColor The color to use while rendering */
	public void drawTexturedModalRectWithColor(int x, int y, int u, int v, int width, int height) {
		double minU = (double) u / 256.0;
		double maxU = (double) (u + width) / 256.0;
		double minV = (double) v / 256.0;
		double maxV = (double) (v + height) / 256.0;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(this.color);
		tessellator.addVertexWithUV(x, y + height, this.zLevel, minU, maxV);
		tessellator.addVertexWithUV(x + width, y + height, this.zLevel, maxU, maxV);
		tessellator.addVertexWithUV(x + width, y, this.zLevel, maxU, minV);
		tessellator.addVertexWithUV(x, y, this.zLevel, minU, minV);
		tessellator.draw();
	}

	/** Bits which are modified while rendering item glints, so must be pushed/popped.
	 * @see #drawTexturedGlintRect(int, int, int, int, int, int) */
	public static final int GL_GLINT_BITS =
			GL_ENABLE_BIT | GL_TEXTURE_BIT | GL_TRANSFORM_BIT |
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
		mc.mcProfiler.startSection("glint");
		// Push bits we modify to restore later on
		glPushAttrib(GL_GLINT_BITS);

		glDepthFunc(GL_EQUAL);
		glDisable(GL_LIGHTING);
		glEnable(GL_BLEND);
		// Mapped OpenGL constants from [768, 1, 1, 0]
		OpenGlHelper.glBlendFunc(GL_SRC_COLOR, GL_ONE, GL_ONE, GL_ZERO);

		glColor4f(0.38F, 0.19F, 0.608F, 1.0F); // #61309B, light purple tint
		mc.getTextureManager().bindTexture(RES_ITEM_GLINT);

		glMatrixMode(GL_TEXTURE);

		long time = Minecraft.getSystemTime();

		// Rect #1
		glPushMatrix();
		glScalef(0.125F, 0.125F, 0.125F);
		glTranslatef((float) (time % 3000L) / 3000.0F * 8.0F, 0.0F, 0.0F);
		glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		this.drawTexturedModalRect(x, y, u, v, width, height);
		glPopMatrix();

		// Rect #2
		glPushMatrix();
		glScalef(0.125F, 0.125F, 0.125F);
		glTranslatef((float) (time % 4873L) / 4873.0F * -8.0F, 0.0F, 0.0F);
		glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
		this.drawTexturedModalRect(x, y, u, v, width, height);
		glPopMatrix();

		glPopAttrib();
		mc.mcProfiler.endSection();
	}
}
