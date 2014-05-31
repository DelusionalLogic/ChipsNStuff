package dk.slashwin.chipsnstuff.circuit;

import java.awt.*;
import java.util.EnumSet;

public class PComponent extends SiliconComponent
{
	public static final byte ID = 1;

	public PComponent()
	{
		super(ID);
	}

	@Override
	public void placeAt(Wafer wafer, int x, int y, int layer, Side side)
	{
		if(wafer.getID(x, y, layer) == NComponent.ID || wafer.getID(x, y, layer) == NPNComponent.ID)
			ComponentRegistry.getComponent(NPNComponent.ID).placeAt(wafer, x, y, layer, side);
		else
			super.placeAt(wafer, x, y, layer, side);
	}

	@Override
	public Color getColor()
	{
		return Color.red;
	}
}
