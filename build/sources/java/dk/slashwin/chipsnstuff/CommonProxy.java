package dk.slashwin.chipsnstuff;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dk.slashwin.chipsnstuff.block.BChip;
import dk.slashwin.chipsnstuff.circuit.*;
import dk.slashwin.chipsnstuff.network.*;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.world.World;

public abstract class CommonProxy
{
	public void registerBlocks()
	{
		GameRegistry.registerBlock(new BChip(), "chip");

		GameRegistry.registerTileEntity(TEChip.class, "chipTE");

		ComponentRegistry.registerComponent(PComponent.ID, new PComponent());
		ComponentRegistry.registerComponent(NComponent.ID, new NComponent());
		ComponentRegistry.registerComponent(MetalComponent.ID, new MetalComponent());
		ComponentRegistry.registerComponent(PNPComponent.ID, new PNPComponent());
		ComponentRegistry.registerComponent(NPNComponent.ID, new NPNComponent());
	}

	public void registerGUI()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ChipsnStuff.instance, new GuiHandler());
	}

	public void registerTESR()
	{
	}

	public abstract World getWorld(int dimId);
	public abstract boolean isClient();
}
