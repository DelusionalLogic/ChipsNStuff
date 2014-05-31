package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import dk.slashwin.chipsnstuff.PadMode;
import dk.slashwin.chipsnstuff.tileentity.TEChip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PadModeChangePacket extends TEPacket<PadModeChangePacket>
{
	public int padID;
	public PadMode mode;

	public PadModeChangePacket(){}

	public PadModeChangePacket(int dimId, int x, int y, int z, int padID, PadMode mode)
	{
		super(dimId, x, y, z);
		this.padID = padID;
		this.mode = mode;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(padID);
		buffer.writeInt(mode.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		padID = buffer.readInt();
		mode = PadMode.fromInt(buffer.readInt());
	}

	@Override
	public IMessage onMessage(PadModeChangePacket message, MessageContext ctx)
	{
		passItOn(message, ctx);
		TEChip chip = getTE(message, ctx, TEChip.class);
		chip.getWafer().getPowerPads().get(message.padID).mode = message.mode;
		chip.padModeChange();

		return null;
	}
}
