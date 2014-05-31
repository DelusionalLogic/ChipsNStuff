package dk.slashwin.chipsnstuff.circuit;

import java.util.EnumSet;

public class GateComponent extends WaferComponent implements IConnectable
{
	private final byte FirstComponent;
	private final byte SecondComponent;

	public GateComponent(byte firstComponent, byte secondComponent, byte ID)
	{
		super(ID);
		FirstComponent = firstComponent;
		SecondComponent = secondComponent;
	}

	@Override
	public void onNeighbourNotify(Wafer wafer, int x, int y, int layer, short metadata, Side side)
	{
		if(side == Side.NONE)
			return;

		int x1 = x + side.OffsetX;
		int y1 = y + side.OffsetY;
		byte id1 = wafer.getID(x1, y1, layer);
		if(id1 == 0)
		{
			if(side.horizontal() == getHorizontal(metadata))
			{
				removeAt(wafer, x, y, layer);
			}
			else
			{
				if (side == Side.UP || side == Side.RIGHT)
					metadata = setTop(metadata, false);
				else
					metadata = setBottom(metadata, false);
				wafer.setMetadata(x, y, layer, metadata);

				if(!getTop(metadata) && !getBottom(metadata))
					removeAt(wafer, x, y, layer);

			}
		}
	}

	@Override
	public void onPowerPropagate(Wafer wafer, int x, int y, int layer, short metadata, Side side)
	{
		if(getPowerStatus(metadata) && getPowered(metadata))
			return;
		if(side == Side.NONE)
			return;

		if(side.horizontal() != getHorizontal(metadata))
			metadata = setPowerStatus(metadata, true);
		else
			metadata = setPowered(metadata, true);
		wafer.setMetadata(x, y, layer, metadata);

		propagatePower(wafer, x, y, layer);
	}

	@Override
	public void placeAt(Wafer wafer, int x, int y, int layer, Side from)
	{
		if(from == Side.NONE)
			return;
		Side opposite = from.opposite();
		int x1 = x + opposite.OffsetX;
		int y1 = y + opposite.OffsetY;
		byte id = wafer.getID(x, y, layer);
		short metadata = wafer.getMetadata(x, y, layer);

		if (!canPlace(wafer, x, y, layer, opposite))
			return;

		if(wafer.getID(x, y, layer) == FirstComponent)
		{
			wafer.setID(x, y, layer, (byte) componentID);
			metadata = wafer.getMetadata(x, y, layer);
			metadata = setHorizontal(metadata, opposite == Side.UP || opposite == Side.DOWN);
		}
		if(opposite == Side.UP || opposite == Side.RIGHT)
		{
			boolean top = getHorizontal(metadata) ? opposite == Side.UP : opposite == Side.RIGHT;
			metadata = setTop(metadata, top);
		}else{
			boolean bottom = getHorizontal(metadata) ? opposite == Side.DOWN : opposite == Side.LEFT;
			metadata = setBottom(metadata, bottom);
		}
		wafer.setMetadata(x, y, layer, metadata);
		notifyNeighbour(wafer, x, y, layer);
	}

	private boolean canPlace(Wafer wafer, int x, int y, int layer, Side side)
	{
		int x1 = x + side.OffsetX;
		int y1 = y + side.OffsetY;
		byte id = wafer.getID(x, y, layer);

		if(wafer.getID(x1, y1, layer) != SecondComponent)
			return false;

		WaferComponent component = ComponentRegistry.getComponent(id);
		if(component instanceof SiliconComponent)
		{
			SiliconComponent siliconComponent = (SiliconComponent) component;
			EnumSet<Side> connected1 = siliconComponent.getConnectedSides(wafer.getMetadata(x, y, layer));
			if(connected1.size() != 2)
				return false;

			Side side1 = (Side) connected1.toArray()[0];
			if(!connected1.contains(side1.opposite()))
				return false;

			if(wafer.getID(x + side1.OffsetX, y + side1.OffsetY, layer) != FirstComponent ||
					wafer.getID(x + side1.opposite().OffsetX, y + side1.opposite().OffsetY, layer) != FirstComponent)
				return false;
		}
		return true;
	}

	private short setTop(short metadata, boolean state)
	{
		int newState = state ? 1 : 0;
		return (short) ((metadata & ~(1 << 2)) | (newState << 2));
	}

	private short setBottom(short metadata, boolean state)
	{
		int newState = state ? 1 : 0;
		return (short) ((metadata & ~(1 << 3)) | (newState << 3));
	}

	private boolean getTop(short metadata)
	{
		return ((metadata >>> 2) & 1) == 1;
	}

	private boolean getBottom(short metadata)
	{
		return ((metadata >>> 3) & 1) == 1;
	}

	@Override
	public void removeAt(Wafer wafer, int x, int y, int layer)
	{
		wafer.setID(x, y, layer, (byte) 0);
		notifyNeighbour(wafer, x, y, layer);
	}

	public short setHorizontal(short metadata, boolean isHorizontal)
	{
		int newState = isHorizontal ? 1 : 0;
		return (short) ((metadata & ~(1 << 1)) | (newState << 1));
	}

	public boolean getHorizontal(short metadata)
	{
		return ((metadata >>> 1) & 1) == 1;
	}

	public short setGateOpen(short metadata, boolean state)
	{
		int newState = state ? 1 : 0;
		return (short) ((metadata & ~(1 << 4)) | (newState << 4));
	}

	public boolean getGateOpen(short metadata)
	{
		return ((metadata >>> 4) & 1) == 1;
	}

	public short setPowered(short metadata, boolean state)
	{
		int newState = state ? 1 : 0;
		return (short) ((metadata & ~(1 << 5)) | (newState << 5));
	}

	public boolean getPowered(short metadata)
	{
		return ((metadata >>> 5) & 1) == 1;
	}

	@Override
	public short setPowerStatus(short metadata, boolean state)
	{
		int newState = state ? 1 : 0;
		return (short) ((metadata & ~1) | newState);
	}

	@Override
	public boolean getPowerStatus(short metadata)
	{
		return (metadata & 1) == 1;
	}

	@Override
	public void resetPowerStatus(Wafer wafer, int x, int y, int layer, short metadata)
	{
		metadata = setPowered(metadata, false);
		wafer.setMetadata(x, y, layer, metadata);
		super.resetPowerStatus(wafer, x, y, layer, metadata);
	}

	@Override
	public void propagatePower(Wafer wafer, int x, int y, int layer)
	{
		short metadata = wafer.getMetadata(x, y, layer);
		for(Side side : Side.VALID_VALUES)
		{
			if(side.horizontal() == getHorizontal(metadata))
			{
				if (!getGateOpen(metadata) || !getPowered(metadata))
					continue;
			}
			else
			{
				if (!getPowerStatus(metadata))
					continue;
				else if((side == Side.UP || side == Side.RIGHT) && !getTop(metadata))
					continue;
				else if((side == Side.DOWN || side == Side.LEFT) && !getTop(metadata))
					continue;
			}
			int x1 = x + side.OffsetX;
			int y1 = y + side.OffsetY;
			byte id = wafer.getID(x1, y1, layer);
			if(id == 0)
				continue;
			ComponentRegistry.getComponent(id).onPowerPropagate(wafer, x1, y1, layer, wafer.getMetadata(x1, y1, layer), side.opposite());
		}
	}

	@Override
	public boolean connectedToSide(Wafer wafer, int x, int y, int layer, Side side)
	{
		short metadata = wafer.getMetadata(x, y, layer);
		if(getHorizontal(metadata))
			return (getTop(metadata) && side == Side.UP) || (getBottom(metadata) && side == Side.DOWN) || side == Side.LEFT || side == Side.RIGHT;
		else
			return (getTop(metadata) && side == Side.RIGHT) || (getBottom(metadata) && side == Side.LEFT) || side == Side.UP || side == Side.DOWN;
	}
}
