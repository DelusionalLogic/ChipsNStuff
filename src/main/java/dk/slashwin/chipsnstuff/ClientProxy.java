package dk.slashwin.chipsnstuff;

import cpw.mods.fml.client.registry.ClientRegistry;
import dk.slashwin.chipsnstuff.renderer.TESRChip;
import dk.slashwin.chipsnstuff.tileentity.TEChip;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerTESR()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TEChip.class, new TESRChip());
	}
}
