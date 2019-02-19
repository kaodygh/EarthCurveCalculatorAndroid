package earth.curve.calculator.model;


public class UnitPref
{
	private boolean mIsImperial;

	private int    mPer;
	private String mBigUnits;
	private String mSmallUnits;
	private double mSmallMult;
	private double mBigMult;

	public UnitPref(boolean isImperial, int per, String bigUnits, String smallUnits, double smallMult, double bigMult)
	{
		mIsImperial = isImperial;
		mPer = per;
		mBigUnits = bigUnits;
		mSmallUnits = smallUnits;
		mSmallMult = smallMult;
		mBigMult = bigMult;
	}

	public boolean isImperial()
	{
		return mIsImperial;
	}

	public int getPer()
	{
		return mPer;
	}

	public String getBigUnits()
	{
		return mBigUnits;
	}

	public String getSmallUnits()
	{
		return mSmallUnits;
	}

	public double getSmallMult()
	{
		return mSmallMult;
	}

	public double getBigMult()
	{
		return mBigMult;
	}
}
