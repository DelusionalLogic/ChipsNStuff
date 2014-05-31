package dk.slashwin.chipsnstuff.circuit;

import dk.slashwin.chipsnstuff.PadMode;
import dk.slashwin.chipsnstuff.PowerPad;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedList;
import java.util.List;

public class Wafer
{
	private int[][][] SiliconGrid;

	public int xSize;
	public int ySize;
	public int layers;
	private List<PowerPad> pads = new LinkedList<PowerPad>();

	public boolean active = true;

	public Wafer(int xSize, int ySize, int layers)
	{
		this.xSize = xSize;
		this.ySize = ySize;
		this.layers = layers;
		SiliconGrid = new int[xSize][ySize][layers];
	}

	public void addPowerPad(PowerPad pad)
	{
		for(int x = 0; x < pad.width; x++)
		{
			for(int y = 0; y < pad.height; y++)
			{
				int xPos = x + pad.x;
				int yPos = y + pad.y;

				for(Side side : Side.values())
				{
					if(side == Side.NONE)
						continue;
					ComponentRegistry.getComponent(pad.id).placeAt(this, xPos, yPos, pad.layer, side);
				}
			}
		}
		pads.add(pad);
	}

	public List<PowerPad> getPowerPads()
	{
		return pads;
	}

	public void update()
	{
		if(!active)
			return;

		//Reset powerstate for every element
		for(int xGrid = 0; xGrid < xSize; xGrid++)
		{
			for(int yGrid = 0; yGrid < ySize; yGrid++)
			{
				for(int layer = 0; layer < layers; layer++)
				{
					byte id = getID(xGrid, yGrid, layer);
					if(id == 0)
						continue;

					short metadata = getMetadata(xGrid, yGrid, layer);
					ComponentRegistry.getComponent(id).resetPowerStatus(this, xGrid, yGrid, layer, metadata);
				}
			}
		}

		for(PowerPad pad : pads)
		{
			if(pad.mode != PadMode.Input || !pad.powered)
				continue;
			for(int x = 0; x < pad.width; x++)
			{
				for(int y = 0; y < pad.height; y++)
				{
					int xPos = x + pad.x;
					int yPos = y + pad.y;
					byte id = getID(xPos, yPos, pad.layer);
					if(id != pad.id)
						continue;

					ComponentRegistry.getComponent(id).onPowerPropagate(this, xPos, yPos, pad.layer, getMetadata(xPos, yPos, pad.layer), Side.NONE);
				}
			}
		}

		for(PowerPad pad : pads)
		{
			if(pad.mode != PadMode.Output)
				continue;
			pad.powered = checkPad(pad);
		}
	}

	private boolean checkPad(PowerPad pad)
	{
		for(int x = 0; x < pad.width; x++)
		{
			for(int y = 0; y < pad.height; y++)
			{
				int xPos = x + pad.x;
				int yPos = y + pad.y;
				byte id = getID(xPos, yPos, pad.layer);
				if(id != pad.id)
					continue;

				boolean powerStatus = ComponentRegistry.getComponent(id).getPowerStatus(getMetadata(xPos, yPos, pad.layer));
				if(powerStatus)
					return true;
			}
		}
		return false;
	}

	private boolean isInPad(int x, int y, int layer)
	{
		for(PowerPad pad : pads)
		{
			if(pad.layer != layer)
				continue;

			if(pad.contains(x, y))
				return true;
		}
		return false;
	}

	public boolean hasVia(int x, int y)
	{
		if(x < 0 || x >= xSize || y < 0 || y >= ySize)
			return false;
		return ((SiliconGrid[x][y][0] >>> 8) & 1) == 1;
	}

	public void setVia(int x, int y, boolean state)
	{
		SiliconGrid[x][y][0] &= ~(1 << 8);
		if(state)
			SiliconGrid[x][y][0] |= (1 << 8);
	}

	public byte getID(int x, int y, int layer)
	{
		if(x < 0 || x >= xSize || y < 0 || y >= ySize)
			return 0;
		return (byte)SiliconGrid[x][y][layer];
	}

	public short getMetadata(int x, int y, int layer)
	{
		if(x < 0 || x >= xSize || y < 0 || y >= ySize)
			return 0;
		return (short)(SiliconGrid[x][y][layer] >>> 16);
	}

	public void setMetadata(int x, int y, int layer, short metadata)
	{
		SiliconGrid[x][y][layer] &= ~(0xFFFF << 16);
		SiliconGrid[x][y][layer] |= (metadata << 16);
	}

	public void setID(int x, int y, int layer, byte ID)
	{
		if(isInPad(x, y, layer))
			return;
		SiliconGrid[x][y][layer] = ID;
	}

	public void placeComponent(byte id, int x, int y, int layer, Side side)
	{
		ComponentRegistry.getComponent(id).placeAt(this, x, y, layer, side);
	}

	public void removeComponent(int x, int y, int layer)
	{
		byte id = getID(x, y, layer);
		if(id == 0) return;
		ComponentRegistry.getComponent(id).removeAt(this, x, y, layer);
	}

	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setInteger("xSize", xSize);
		tagCompound.setInteger("ySize", ySize);
		tagCompound.setInteger("layers", layers);

		int[] flatWafer = new int[xSize * ySize * layers];
		for(int i = 0; i < xSize; i++)
		{
			for(int j = 0; j < ySize; j++)
			{
				for(int k = 0; k < layers; k++)
				{
					int index = i*ySize*layers + j*layers + k;
					flatWafer[index] = SiliconGrid[i][j][k];
				}
			}
		}
		tagCompound.setIntArray("waferArray", flatWafer);
	}

	public void readFromNBT(NBTTagCompound tagCompound)
	{
		xSize = tagCompound.getInteger("xSize");
		ySize = tagCompound.getInteger("ySize");
		layers = tagCompound.getInteger("layers");

		SiliconGrid = new int[xSize][ySize][layers];
		int[] flatWafer = tagCompound.getIntArray("waferArray");
		for(int i = 0; i < xSize; i++)
		{
			for(int j = 0; j < ySize; j++)
			{
				for(int k = 0; k < layers; k++)
				{
					int index = i*ySize*layers + j*layers + k;
					SiliconGrid[i][j][k] = flatWafer[index];
				}
			}
		}
	}
}
