package dk.slashwin.chipsnstuff.circuit;

public abstract class WaferComponent
{
	public final int componentID;

	protected WaferComponent(int componentID)
	{
		this.componentID = componentID;
	}

	public abstract void onNeighbourNotify(Wafer wafer, int x, int y, int layer, short metadata, Side side);

	public abstract void onPowerPropagate(Wafer wafer, int x, int y, int layer, short metadata, Side side);

	public abstract void placeAt(Wafer wafer, int x, int y, int layer, Side side);
	public abstract void removeAt(Wafer wafer, int x, int y, int layer);

	protected void notifyNeighbour(Wafer wafer, int x, int y, int layer)
	{
		for(Side side : Side.values())
		{
			int x1 = x + side.OffsetX;
			int y1 = y + side.OffsetY;
			if(x1 < 0 || x1 >= wafer.xSize || y1 < 0 || y1 >= wafer.ySize)
				continue;
			byte id = wafer.getID(x1, y1, layer);
			if(id == 0)
				continue;
			ComponentRegistry.getComponent(id).onNeighbourNotify(wafer, x1, y1, layer, wafer.getMetadata(x1, y1, layer), side.opposite());
		}
	}

	public abstract short setPowerStatus(short metadata, boolean state);
	public abstract boolean getPowerStatus(short metadata);
	public void resetPowerStatus(Wafer wafer, int x, int y, int layer, short metadata)
	{
		metadata = setPowerStatus(metadata, false);
		wafer.setMetadata(x, y, layer, metadata);
	}
	public abstract void propagatePower(Wafer wafer, int x, int y, int layer);
}
