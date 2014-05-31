package dk.slashwin.chipsnstuff.tileentity;

import dk.slashwin.chipsnstuff.ChipsnStuff;
import dk.slashwin.chipsnstuff.PadMode;
import dk.slashwin.chipsnstuff.PowerPad;
import dk.slashwin.chipsnstuff.WaferProvider;
import dk.slashwin.chipsnstuff.circuit.MetalComponent;
import dk.slashwin.chipsnstuff.circuit.Side;
import dk.slashwin.chipsnstuff.circuit.Wafer;
import dk.slashwin.chipsnstuff.network.PacketHandler;
import dk.slashwin.chipsnstuff.network.PadPowerChangePacket;
import dk.slashwin.chipsnstuff.network.WaferPlacePacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map;

public class TEChip extends TileEntity
{
	Wafer wafer = new Wafer(39, 25, 2);
	int waferID = -1;
	boolean updateNext = true;
	boolean updateNow = true;

	EnumMap<ForgeDirection, PowerPad> pads = new EnumMap<ForgeDirection, PowerPad>(ForgeDirection.class);

	public TEChip()
	{
		pads.put(ForgeDirection.NORTH, new PowerPad(4, 0, 1, 31, 3, PadMode.Output, false, MetalComponent.ID));
		pads.put(ForgeDirection.SOUTH, new PowerPad(4, 22, 1, 31, 3, PadMode.Output, false, MetalComponent.ID));
		pads.put(ForgeDirection.WEST, new PowerPad(0, 4, 1, 3, 17, PadMode.Output, false, MetalComponent.ID));
		pads.put(ForgeDirection.EAST, new PowerPad(36, 4, 1, 3, 17, PadMode.Input, false, MetalComponent.ID));
		for(PowerPad pad : pads.values())
		{
			wafer.addPowerPad(pad);
		}
	}

	private Wafer wafer()
	{
		if(waferID == -1)
			waferID = WaferProvider.newWafer(39, 25, 2);
		return WaferProvider.getWafer(waferID);
	}

	public Wafer getWafer()
	{
		return wafer;
	}

	public boolean isSendingPower(ForgeDirection dir)
	{
		if(dir == ForgeDirection.DOWN || dir == ForgeDirection.UP || dir == ForgeDirection.UNKNOWN)
			return false;
		return pads.get(dir).mode == PadMode.Output && pads.get(dir).powered;
	}

	public boolean isPowered(ForgeDirection dir)
	{
		if(dir == ForgeDirection.DOWN || dir == ForgeDirection.UP || dir == ForgeDirection.UNKNOWN)
			return false;
		return pads.get(dir).powered;
	}

	public void setPower(ForgeDirection dir, boolean state)
	{
		if(dir == ForgeDirection.DOWN || dir == ForgeDirection.UP || dir == ForgeDirection.UNKNOWN)
			return;
		if(pads.get(dir).mode != PadMode.Input)
			return;
		if(pads.get(dir).powered != state && !worldObj.isRemote)
		{
			setPowerNetwork(dir, state);
			PacketHandler.INSTANCE.sendToDimension(new PadPowerChangePacket(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, dir, state), worldObj.provider.dimensionId);
		}
	}

	public void padModeChange()
	{
		worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
	}

	public void setPowerNetwork(ForgeDirection dir, boolean state)
	{
		updateNow = true;
		updateNext = true;

		pads.get(dir).powered = state;

		if(state)
			return;

		updateNext = false;
		for(PowerPad pad : pads.values())
		{
			if (pad.powered && pad.mode == PadMode.Input)
			{
				updateNext = true;
				break;
			}
		}
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		if(!updateNow)
			return;

		EnumMap<ForgeDirection, Boolean> padStates = new EnumMap<ForgeDirection, Boolean>(ForgeDirection.class);
		for(ForgeDirection padDir : pads.keySet())
		{
			final PowerPad powerPad = pads.get(padDir);
			padStates.put(padDir, powerPad.powered);
		}

		wafer.update();

		for(ForgeDirection padDir : pads.keySet())
		{
			final PowerPad powerPad = pads.get(padDir);
			boolean beforeState = padStates.get(padDir);
			if(beforeState != powerPad.powered)
				worldObj.notifyBlockOfNeighborChange(xCoord + padDir.offsetX, yCoord + padDir.offsetY, zCoord + padDir.offsetZ, worldObj.getBlock(xCoord, yCoord, zCoord));
		}
		updateNow = updateNext;
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		NBTTagCompound WaferTag = new NBTTagCompound();
		wafer.writeToNBT(WaferTag);
		tagCompound.setTag("wafer", WaferTag);

		int[] padDirs = new int[pads.size()];
		int count = 0;
		for(Map.Entry<ForgeDirection, PowerPad> entry : pads.entrySet())
		{
			NBTTagCompound padTag = new NBTTagCompound();
			entry.getValue().writeToNBT(padTag);
			padDirs[count++] = entry.getKey().ordinal();
			tagCompound.setTag("PowerPad" + entry.getKey().ordinal(), padTag);
		}
		tagCompound.setIntArray("padDirs", padDirs);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		wafer.readFromNBT(tagCompound.getCompoundTag("wafer"));

		int[] padDirs = tagCompound.getIntArray("padDirs");
		for (int padDir : padDirs)
		{
			PowerPad pad = pads.get(ForgeDirection.getOrientation(padDir));
			pad.readFromNBT(tagCompound.getCompoundTag("PowerPad" + padDir));
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
		readFromNBT(pkt.func_148857_g());
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbttagcompound);
	}
}
