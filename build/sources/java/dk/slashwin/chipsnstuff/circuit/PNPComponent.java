package dk.slashwin.chipsnstuff.circuit;

public class PNPComponent extends GateComponent
{
	public static final byte ID = 10;

	public PNPComponent()
	{
		super(PComponent.ID, NComponent.ID, ID);
	}

	@Override
	public void resetPowerStatus(Wafer wafer, int x, int y, int layer, short metadata)
	{
		metadata = setGateOpen(metadata, getPowerStatus(metadata));
		super.resetPowerStatus(wafer, x, y, layer, metadata);
	}
}
