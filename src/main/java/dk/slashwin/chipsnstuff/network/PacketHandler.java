package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dk.slashwin.chipsnstuff.ChipsnStuff;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ChipsnStuff.MOD_ID);

	public static void init()
	{
		INSTANCE.registerMessage(PadModeChangePacket.class, PadModeChangePacket.class, 0, Side.SERVER);
		INSTANCE.registerMessage(WaferPlacePacket.class, WaferPlacePacket.class, 1, Side.SERVER);
		INSTANCE.registerMessage(WaferPlaceViaPacket.class, WaferPlaceViaPacket.class, 2, Side.SERVER);
		INSTANCE.registerMessage(WaferRemovePacket.class, WaferRemovePacket.class, 3, Side.SERVER);
		INSTANCE.registerMessage(PadModeChangePacket.class, PadModeChangePacket.class, 100, Side.CLIENT);
		INSTANCE.registerMessage(WaferPlacePacket.class, WaferPlacePacket.class, 101, Side.CLIENT);
		INSTANCE.registerMessage(WaferPlaceViaPacket.class, WaferPlaceViaPacket.class, 102, Side.CLIENT);
		INSTANCE.registerMessage(WaferRemovePacket.class, WaferRemovePacket.class, 103, Side.CLIENT);
		INSTANCE.registerMessage(PadPowerChangePacket.class, PadPowerChangePacket.class, 200, Side.CLIENT);
	}
}
