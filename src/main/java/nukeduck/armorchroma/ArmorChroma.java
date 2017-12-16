package nukeduck.armorchroma;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import nukeduck.armorchroma.config.Config;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.3")
public class ArmorChroma {
	public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public static final String MODID = "armorchroma";
	public static Logger logger;
	public static Config config;

	private static final GuiArmor GUI = new GuiArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();

		config = new Config(e.getSuggestedConfigurationFile());
		config.load();
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre e) {
		if(e.type == ElementType.ARMOR) {
			e.setCanceled(true); // Don't want anything else rendering on top
			GUI.draw(e.resolution.getScaledWidth(), e.resolution.getScaledHeight());
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent e) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.getEventKeyState()) {
			config.load();
		}
	}
}
