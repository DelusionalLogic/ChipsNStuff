package dk.slashwin.chipsnstuff;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ServerProxy extends CommonProxy
{
	@Override
	public World getWorld(int dimId)
	{
		return DimensionManager.getWorld(dimId);
	}

	@Override
	public boolean isClient()
	{
		return false;
	}
}
