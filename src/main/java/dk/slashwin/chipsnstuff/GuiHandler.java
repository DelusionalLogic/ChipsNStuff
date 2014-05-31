package dk.slashwin.chipsnstuff;

import cpw.mods.fml.common.network.IGuiHandler;
import dk.slashwin.chipsnstuff.gui.GChip;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == GChip.GUI_ID)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TEChip)
				return new GChip((TEChip) te, ((TEChip) te).getWafer());
			else System.out.println("Error: No TE");
		}
		return null;
	}
}
