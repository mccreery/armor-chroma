package nukeduck.armorchroma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

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
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import nukeduck.armorchroma.config.Config;
import nukeduck.armorchroma.config.IconData;
import nukeduck.armorchroma.config.IconData.ModEntry;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.3", acceptedMinecraftVersions = "[1.12,1.13)")
public class ArmorChroma {
	public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public static final String MODID = "armorchroma";
	public static Logger logger;
	public static Config config;

	private static final GuiArmor GUI = new GuiArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();

		config = new Config(new File(new File(e.getModConfigurationDirectory(), MODID), MODID + ".cfg"));
		config.load();
	}

	public static final IconData iconData = new IconData();

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager())
				.registerReloadListener(m -> {
			iconData.clear();

			for(String modid : Loader.instance().getIndexedModList().keySet()) {
				try {
					IResource resource = m.getResource(new ResourceLocation(modid, "textures/gui/armor_chroma.json"));

					try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
						ModEntry entry = new Gson().fromJson(reader, ModEntry.class);
						iconData.put(modid, entry);
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
	public void onKeyInput(KeyInputEvent e) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.getEventKeyState()) {
			config.load();
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
