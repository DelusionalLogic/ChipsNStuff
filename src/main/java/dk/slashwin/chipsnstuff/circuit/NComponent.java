package dk.slashwin.chipsnstuff.circuit;

import java.awt.*;
import java.util.EnumSet;

public class NComponent extends SiliconComponent
{
	public static final byte ID = 2;

	public NComponent()
	{
		super(ID);
	}

	@Override
	public void placeAt(Wafer wafer, int x, int y, int layer, Side side)
	{
		if(wafer.getID(x, y, layer) == PComponent.ID || wafer.getID(x, y, layer) == PNPComponent.ID)
			ComponentRegistry.getComponent(PNPComponent.ID).placeAt(wafer, x, y, layer, side);
		else
			super.placeAt(wafer, x, y, layer, side);
	}

	@Override
	public Color getColor()
	{
		return Color.yellow;
	}
}
