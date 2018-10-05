package nukeduck.armorchroma;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nukeduck.armorchroma.config.IconData;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.4-beta", acceptedMinecraftVersions = "[1.12,1.13)")
public class ArmorChroma {
	public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public static final String MODID = "armorchroma";
	private static final GuiArmor GUI = new GuiArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
	}

	private static final IconData ICON_DATA = new IconData();
	public static IconData getIconData() {return ICON_DATA;}

	private static Logger logger;
	public static Logger getLogger() {return logger;}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)manager).registerReloadListener(ICON_DATA::reload);
		} else {
			logger.error("Unable to register icon loader");
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre e) {
		if(e.getType() == ElementType.ARMOR) {
			e.setCanceled(true); // Don't want anything else rendering on top
			GUI.draw(e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
		}
	}

	@SubscribeEvent
	public void onRenderTooltip(ItemTooltipEvent e) {
		if(e.getFlags().isAdvanced() && e.getItemStack().getItem() instanceof ItemArmor) {
			final String material = ((ItemArmor)e.getItemStack().getItem()).getArmorMaterial().getName();
			e.getToolTip().add(TextFormatting.DARK_GRAY + "Armor Material: " + material);
		}
	}
}
