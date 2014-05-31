package dk.slashwin.chipsnstuff.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.awt.*;

public abstract class TEPacket<T extends TEPacket> implements IMessage, IMessageHandler<T, IMessage>
{
	protected int dimId;
	private int x;
	private int y;
	private int z;

	public TEPacket(){}

	protected TEPacket(int dimId, int x, int y, int z)
	{
		this.dimId = dimId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(dimId);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		dimId = buffer.readInt();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	protected <T extends TileEntity> T getTE(TEPacket message, MessageContext ctx, Class<T> type)
	{
		World world = DimensionManager.getWorld(message.dimId);

		if(ctx.side == Side.CLIENT)
			world = getClientWorld(dimId);

		TileEntity te = world.getTileEntity(message.x, message.y, message.z);
		if(te == null || !type.isInstance(te))
			return null;
		return type.cast(te);
	}

	@SideOnly(Side.CLIENT)
	private World getClientWorld(int dimID)
	{
		return Minecraft.getMinecraft().theWorld;
	}

	@SideOnly(Side.SERVER)
	private World getServerWorld(int dimId)
	{
		return MinecraftServer.getServer().worldServerForDimension(dimId);
	}

	protected void passItOn(TEPacket packet, MessageContext ctx)
	{
		if(ctx.side == cpw.mods.fml.relauncher.Side.SERVER)
			PacketHandler.INSTANCE.sendToDimension(packet, packet.dimId);
	}
}
