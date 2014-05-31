package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import io.netty.buffer.ByteBuf;

public class WaferRemovePacket extends TEPacket<WaferRemovePacket>
{
	public int xGrid;
	public int yGrid;
	public int layerGrid;

	public WaferRemovePacket()
	{

	}

	public WaferRemovePacket(int dimId, int x, int y, int z, int xGrid, int yGrid, int layerGrid)
	{
		super(dimId, x, y, z);
		this.xGrid = xGrid;
		this.yGrid = yGrid;
		this.layerGrid = layerGrid;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(xGrid);
		buffer.writeInt(yGrid);
		buffer.writeInt(layerGrid);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		xGrid = buffer.readInt();
		yGrid = buffer.readInt();
		layerGrid = buffer.readInt();
	}

	@Override
	public IMessage onMessage(WaferRemovePacket message, MessageContext ctx)
	{
		passItOn(message, ctx);
		TEChip chip = getTE(message, ctx, TEChip.class);
		chip.getWafer().removeComponent(message.xGrid, message.yGrid, message.layerGrid);
		return null;
	}
}
