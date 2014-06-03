package dk.slashwin.chipsnstuff.block;

import dk.slashwin.chipsnstuff.ChipsnStuff;
import dk.slashwin.chipsnstuff.gui.GChip;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BChip extends Block implements ITileEntityProvider
{
	public BChip()
	{
		super(Material.iron);
		setBlockName("chip");
		setCreativeTab(CreativeTabs.tabRedstone);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		setBlockTextureName("chipsnstuff:chip");
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float dx, float dy, float dz)
	{
		if(player.isSneaking())
			return false;
		player.openGui(ChipsnStuff.instance, GChip.GUI_ID, world, x, y, z);
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TEChip)
				((TEChip) te).setPower(direction, getInputStrength(world, x, y, z, direction) != 0);
		}
		super.onNeighborBlockChange(world, x, y, z, block);
	}

	protected int getInputStrength(World world, int x, int y, int z, ForgeDirection dir)
	{
		int x1 = x + dir.offsetX;
		int y1 = y + dir.offsetY;
		int z1 = z + dir.offsetZ;
		int indirectPowerLevelTo = world.getIndirectPowerLevelTo(x1, y1, z1, dir.ordinal());
		return indirectPowerLevelTo >= 15 ? indirectPowerLevelTo : Math.max(indirectPowerLevelTo, world.getBlock(x1, y1, z1) == Blocks.redstone_wire ? world.getBlockMetadata(x1, y1, z1) : 0);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
	{
		return isProvidingWeakPower(p_149748_1_, p_149748_2_, p_149748_3_, p_149748_4_, p_149748_5_);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int dir)
	{
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if(te instanceof TEChip)
		{
			int val = ((TEChip) te).isSendingPower(ForgeDirection.getOrientation(dir).getOpposite()) ? 15 : 0;
			return val;
		}
		return 10;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TEChip();
	}
}
