package dk.slashwin.chipsnstuff.circuit;

import java.awt.*;

public class MetalComponent extends SiliconComponent
{
	public static final byte ID = 100;

	public MetalComponent()
	{
		super(ID);
	}

	@Override
	public Color getColor()
	{
		return new Color(200, 200, 200, 200);
	}
}
