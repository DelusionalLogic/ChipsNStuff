package dk.slashwin.chipsnstuff.circuit;

public enum Side
{
	LEFT(-1, 0),
	UP(0 , -1),
	RIGHT(1, 0),
	DOWN(0, 1),
	NONE(0, 0);

	public final int OffsetX;
	public final int OffsetY;
	public final int flag;
	public static final Side[] VALID_VALUES = {LEFT, UP, RIGHT, DOWN};
	public static final int[] OPPOSITES = {2, 3, 0, 1, 4};

	Side(int x, int y)
	{
		OffsetX = x;
		OffsetY = y;
		flag = 1 << ordinal();
	}

	public Side opposite()
	{
		return fromInt(OPPOSITES[ordinal()]);
	}

	public static Side fromInt(int id)
	{
		if(id < VALID_VALUES.length)
			return VALID_VALUES[id];
		return NONE;
	}

	public boolean horizontal()
	{
		return OffsetX != 0;
	}
}
