package nukeduck.armorchroma;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ArmorChroma.MODID, name = "Armor Chroma", version = "1.0.1")
public class ArmorChroma {
	/** Minecraft instance for convenience purposes (don't want to call {@link Minecraft#getMinecraft()} every time). */
	public static final Minecraft mc = Minecraft.getMinecraft();

	/** Main instance of the mod */
	@Instance(ArmorChroma.MODID)
	public static ArmorChroma INSTANCE;
	/** Mod ID<br/>
	 * Always use this rather than literals. */
	public static final String MODID = "armorchroma";

	/** Mod configuration to load into */
	public final Config config;
	/** Instance of the Gui class for rendering */
	private final GuiArmor armor;

	public ArmorChroma() {
		this.config = new Config();
		this.armor = new GuiArmor();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		this.config.init(e.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderOverlay(RenderGameOverlayEvent.Pre e) {
		if(e.type == ElementType.ARMOR) {
			e.setCanceled(true); // Don't want anything else rendering on top
			this.armor.renderArmorBar(e.resolution.getScaledWidth(), e.resolution.getScaledHeight());
		}
	}

	/*@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e) {
		if(e.itemStack.getItem() instanceof ItemArmor) {
			e.toolTip.add(((ItemArmor) e.itemStack.getItem()).getArmorMaterial().toString());
		}
		e.toolTip.add(Item.itemRegistry.getNameForObject(e.itemStack.getItem()));
	}*/
}
