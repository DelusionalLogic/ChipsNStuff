package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dk.slashwin.chipsnstuff.ChipsnStuff;
import dk.slashwin.chipsnstuff.circuit.Side;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import io.netty.buffer.ByteBuf;

public class WaferPlacePacket extends TEPacket<WaferPlacePacket>
{
	public int xGrid;
	public int yGrid;
	public int layerGrid;
	public int side;
	public byte id;

	public WaferPlacePacket(){super();}

	public WaferPlacePacket(int dimId, int x, int y, int z, int xGrid, int yGrid, int layerGrid, int side, byte id)
	{
		super(dimId, x, y, z);
		this.xGrid = xGrid;
		this.yGrid = yGrid;
		this.layerGrid = layerGrid;
		this.side = side;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(xGrid);
		buffer.writeInt(yGrid);
		buffer.writeInt(layerGrid);
		buffer.writeInt(side);
		buffer.writeByte(id);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		xGrid = buffer.readInt();
		yGrid = buffer.readInt();
		layerGrid = buffer.readInt();
		side = buffer.readInt();
		id = buffer.readByte();
	}

	@Override
	public IMessage onMessage(WaferPlacePacket message, MessageContext ctx)
	{
		passItOn(message, ctx);
		TEChip chip = getTE(message, ctx, TEChip.class);
		chip.getWafer().placeComponent(message.id, message.xGrid, message.yGrid, message.layerGrid, Side.fromInt(message.side));
		return null;
	}
}
