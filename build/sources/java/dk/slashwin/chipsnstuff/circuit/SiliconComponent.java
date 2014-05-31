package dk.slashwin.chipsnstuff.circuit;

import java.awt.*;
import java.util.EnumSet;

public abstract class SiliconComponent extends WaferComponent implements IConnectable
{

	protected SiliconComponent(int componentID)
	{
		super(componentID);
	}

	@Override
	public void onNeighbourNotify(Wafer wafer, int x, int y, int layer, short metadata, Side side)
	{
		if(side == Side.NONE)
			return;
		EnumSet<Side> connected = getConnectedSides(metadata);
		int x1 = x + side.OffsetX;
		int y1 = y + side.OffsetY;
		byte id = wafer.getID(x1, y1, layer);
		if(id == 0)
		{
			if(connected.contains(side))
				connected.remove(side);
			metadata = setConnectedSides(metadata, connected);
			wafer.setMetadata(x, y, layer, metadata);
		}

		short metadata1 = wafer.getMetadata(x1, y1, layer);
		WaferComponent component = ComponentRegistry.getComponent(id);
		if(component instanceof IConnectable)
		{
			IConnectable connectableComponent = (IConnectable) component;

			if(connectableComponent.connectedToSide(wafer, x1, y1, layer, side.opposite()))
			{
				connected.add(side);
			}else{
				connected.remove(side);
			}
			metadata = setConnectedSides(metadata, connected);
			wafer.setMetadata(x, y, layer, metadata);
		}
	}

	@Override
	public void onPowerPropagate(Wafer wafer, int x, int y, int layer, short metadata, Side side)
	{
		if(getPowerStatus(metadata))
			return;

		metadata = setPowerStatus(metadata, true);
		wafer.setMetadata(x, y, layer, metadata);

		propagatePower(wafer, x, y, layer);
	}

	@Override
	public void placeAt(Wafer wafer, int x, int y, int layer, Side side)
	{
		if(wafer.getID(x, y, layer) == 0)
			wafer.setID(x, y, layer, (byte) componentID);
		if(side == Side.NONE)
			return;

		if(wafer.getID(x, y, layer) == componentID)
		{
			Side opposite = side.opposite();
			int connectedX = x + opposite.OffsetX;
			int connectedY = y + opposite.OffsetY;

			if(wafer.getID(connectedX, connectedY, layer) != componentID)
				return;

			short metadata = wafer.getMetadata(x, y, layer);
			EnumSet<Side> connected = getConnectedSides(metadata);
			connected.add(opposite);
			metadata = setConnectedSides(metadata, connected);
			wafer.setMetadata(x, y, layer, metadata);
			notifyNeighbour(wafer, x, y, layer);
		}
	}

	@Override
	public void removeAt(Wafer wafer, int x, int y, int layer)
	{
		wafer.setID(x, y, layer, (byte) 0);
		notifyNeighbour(wafer, x, y, layer);
	}

	public short setPowerStatus(short metadata, boolean power)
	{
		int newState = power ? 1 : 0;
		return (short)((metadata & ~(1 << 4)) | (newState << 4));
	}

	public boolean getPowerStatus(short metadata)
	{
		return ((metadata >>> 4) & 1) == 1;
	}

	public boolean connectedToSide(Wafer wafer, int x, int y, int layer, Side side)
	{
		return getConnectedSides(wafer.getMetadata(x, y, layer)).contains(side);
	}

	public short setConnectedSides(short metadata, EnumSet<Side> connected)
	{
		metadata &= ~0xF;
		for(Side side : connected)
		{
			if(side == Side.NONE)
				continue;
			int sideValue = side.flag;
			metadata |= sideValue;
		}
		return metadata;
	}

	public EnumSet<Side> getConnectedSides(short metadata)
	{
		EnumSet<Side> connected = EnumSet.noneOf(Side.class);
		for(Side side : Side.VALID_VALUES)
		{
			int sideValue = side.flag;
			if((metadata & sideValue) != 0)
				connected.add(side);
		}
		return connected;
	}

	public void propagatePower(Wafer wafer, int x, int y, int layer)
	{
		for(Side side : getConnectedSides(wafer.getMetadata(x, y, layer)))
		{
			int x1 = x + side.OffsetX;
			int y1 = y + side.OffsetY;
			byte id = wafer.getID(x1, y1, layer);
			if(id == 0)
				continue;
			ComponentRegistry.getComponent(id).onPowerPropagate(wafer, x1, y1, layer, wafer.getMetadata(x1, y1, layer), side.opposite());
		}

		if(wafer.hasVia(x, y))
		{
			for(int i = 0; i < wafer.layers; i++)
			{
				byte id = wafer.getID(x, y, i);
				if(id == 0)
					continue;
				ComponentRegistry.getComponent(id).onPowerPropagate(wafer, x, y, i, wafer.getMetadata(x, y, i), Side.NONE);
			}
		}
	}

	public abstract Color getColor();

	public Color getPoweredColor()
	{
		return getColor().darker();
	}
}
