package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import io.netty.buffer.ByteBuf;

public class WaferPlaceViaPacket extends TEPacket<WaferPlaceViaPacket>
{
	public int xGrid;
	public int yGrid;
	public boolean viaState;

	public WaferPlaceViaPacket()
	{
	}

	public WaferPlaceViaPacket(int dimId, int x, int y, int z, int xGrid, int yGrid, boolean viaState)
	{
		super(dimId, x, y, z);
		this.xGrid = xGrid;
		this.yGrid = yGrid;
		this.viaState = viaState;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(xGrid);
		buffer.writeInt(yGrid);
		buffer.writeBoolean(viaState);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		xGrid = buffer.readInt();
		yGrid = buffer.readInt();
		viaState = buffer.readBoolean();
	}

	@Override
	public IMessage onMessage(WaferPlaceViaPacket message, MessageContext ctx)
	{
		passItOn(message, ctx);
		TEChip chip = getTE(message, ctx, TEChip.class);
		chip.getWafer().setVia(message.xGrid, message.yGrid, message.viaState);
		return null;
	}
}
