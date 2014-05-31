package dk.slashwin.chipsnstuff;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dk.slashwin.chipsnstuff.network.PacketHandler;

@Mod(modid = ChipsnStuff.MOD_ID, version = ChipsnStuff.VERSION)
public class ChipsnStuff
{
    public static final String MOD_ID = "chipsnstuff";
    public static final String VERSION = "0.1";

	@Mod.Instance(ChipsnStuff.MOD_ID)
	public static ChipsnStuff instance;

	@SidedProxy(clientSide="dk.slashwin.chipsnstuff.ClientProxy", serverSide="dk.slashwin.chipsnstuff.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		PacketHandler.init();
		proxy.registerBlocks();
		proxy.registerGUI();
		proxy.registerTESR();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
	}

	@EventHandler
	public void postInitialize(FMLPostInitializationEvent event)
	{
	}
}
