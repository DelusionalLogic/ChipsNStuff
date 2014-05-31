package dk.slashwin.chipsnstuff;

public enum PadMode
{
	Input(0),
	Output(1);

	public static final PadMode[] VALID_VALUES = {Input, Output};
	private final int Modes = 2;

	private int mode;

	PadMode(int i)
	{
		mode = i;
	}

	public static PadMode fromInt(int i)
	{
		return VALID_VALUES[i];
	}

	public PadMode next()
	{
		return fromInt((mode + 1) % Modes);
	}
}
