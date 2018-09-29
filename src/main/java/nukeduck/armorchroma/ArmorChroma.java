package nukeduck.armorchroma;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.google.gson.Gson;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nukeduck.armorchroma.config.ArmorChromaConfig;
import nukeduck.armorchroma.config.IconData;
import nukeduck.armorchroma.config.IconData.ModEntry;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.3", acceptedMinecraftVersions = "[1.12,1.13)")
public class ArmorChroma {
	public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public static final String MODID = "armorchroma";
	public static Logger logger;
	public static final ArmorChromaConfig CONFIG = new ArmorChromaConfig();

	private static final GuiArmor GUI = new GuiArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
	}

	public static final IconData iconData = new IconData();

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager())
				.registerReloadListener(m -> {
			Map<String, ModEntry> modEntries = iconData.getModEntries();
			modEntries.clear();

			for(String modid : Loader.instance().getIndexedModList().keySet()) {
				try {
					IResource resource = m.getResource(new ResourceLocation(modid, "textures/gui/armor_chroma.json"));

					try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
						ModEntry entry = new Gson().fromJson(reader, ModEntry.class);

						modEntries.merge(modid, entry, (a, b) -> {
							a.putAll(b);
							return a;
						});
					}
				} catch(FileNotFoundException e) {
					// Ignore missing files
				} catch(IOException e) {
					logger.error("Unable to load icons for modid " + modid, e);
				}
			}
		});
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
