package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;

public class PadPowerChangePacket extends TEPacket<PadPowerChangePacket>
{
	public ForgeDirection direction;
	public boolean powerState;

	public PadPowerChangePacket()
	{
	}

	public PadPowerChangePacket(int dimId, int x, int y, int z, ForgeDirection direction, boolean powerState)
	{
		super(dimId, x, y, z);
		this.direction = direction;
		this.powerState = powerState;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(direction.ordinal());
		buffer.writeBoolean(powerState);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		direction = ForgeDirection.getOrientation(buffer.readInt());
		powerState = buffer.readBoolean();
	}

	@Override
	public IMessage onMessage(PadPowerChangePacket message, MessageContext ctx)
	{
		TEChip chip = getTE(message, ctx, TEChip.class);
		chip.setPowerNetwork(message.direction, message.powerState);
		return null;
	}
}
