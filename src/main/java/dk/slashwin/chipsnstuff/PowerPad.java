package dk.slashwin.chipsnstuff;

import net.minecraft.nbt.NBTTagCompound;

public class PowerPad
{
	public int x;
	public int y;
	public int layer;
	public int width;
	public int height;
	public boolean powered;
	public byte id;
	public PadMode mode;

	public PowerPad(int x, int y, int layer, int width, int height, PadMode mode, boolean powered, byte id)
	{
		this.x = x;
		this.y = y;
		this.layer = layer;
		this.width = width;
		this.height = height;
		this.mode = mode;
		this.powered = powered;
		this.id = id;
	}

	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setInteger("x", x);
		tagCompound.setInteger("y", y);
		tagCompound.setInteger("layer", layer);
		tagCompound.setInteger("width", width);
		tagCompound.setInteger("height", height);
		tagCompound.setByte("id", id);

		tagCompound.setBoolean("powered", powered);
		tagCompound.setInteger("mode", mode.ordinal());
	}

	public void readFromNBT(NBTTagCompound tagCompound)
	{
		x = tagCompound.getInteger("x");
		y = tagCompound.getInteger("y");
		layer = tagCompound.getInteger("layer");
		width = tagCompound.getInteger("width");
		height = tagCompound.getInteger("height");
		id = tagCompound.getByte("id");

		powered = tagCompound.getBoolean("powered");
		mode = PadMode.fromInt(tagCompound.getInteger("mode"));
	}

	public boolean contains(int xGrid, int yGrid)
	{
		if(xGrid >= x && xGrid < x + width && yGrid >= y && yGrid < y + height)
			return true;
		return false;
	}
}

