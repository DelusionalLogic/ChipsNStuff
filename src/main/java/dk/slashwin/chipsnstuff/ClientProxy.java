package dk.slashwin.chipsnstuff;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dk.slashwin.chipsnstuff.renderer.TESRChip;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerTESR()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TEChip.class, new TESRChip());
	}

	@Override
	public World getWorld(int dimId)
	{
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public boolean isClient()
	{
		return true;
	}
}
