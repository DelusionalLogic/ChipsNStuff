package dk.slashwin.chipsnstuff.circuit;

public class NPNComponent extends GateComponent
{
	public static final byte ID = 11;

	public NPNComponent()
	{
		super(NComponent.ID, PComponent.ID, ID);
	}

	@Override
	public void resetPowerStatus(Wafer wafer, int x, int y, int layer, short metadata)
	{
		metadata = setGateOpen(metadata, !getPowerStatus(metadata));
		super.resetPowerStatus(wafer, x, y, layer, metadata);
	}
}
