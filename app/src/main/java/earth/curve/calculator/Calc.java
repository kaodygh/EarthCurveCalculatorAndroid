package earth.curve.calculator;

import android.util.Log;

import java.text.DecimalFormat;


/**
 * Created by Zel <mancdev@gmail.com> on 19/02/19.
 */

public class Calc
{
	private static final String TAG = Calc.class.getSimpleName();

	public static DecimalFormat DF2 = new DecimalFormat("0.00");
	public static DecimalFormat DF3 = new DecimalFormat("0.000");
	public static DecimalFormat DF4 = new DecimalFormat("0.0000");
	public static DecimalFormat DF5 = new DecimalFormat("0.00000");

	private boolean imperial;

	private double  distance;

	private double  userHeight;

	private double r;  // radius            (metres or feet)
	private double rr; // refractive radius (metres or feet)

	/* Without refraction */
	double distanceToHorizon;
	double hidden;
	double bulge;
	double drop;
	double dipRadians;
	double dipDegrees;

	/* standard refraction */
	double refDistanceToHorizon;
	double refHidden;
	double refDrop;
	double refDipRadians;
	double refDipDegrees;

	public Calc(boolean isImperial, double d, double uHeight, double r1, double r2)
	{
		imperial   = isImperial;
		distance   = d;
		userHeight = uHeight;
		r          = r1;
		rr         = r2;

		distanceToHorizon = Math.sqrt((r+userHeight)*(r+userHeight) - r*r); // distance to horizon (feet or meters)
		hidden            = Math.sqrt(distanceToHorizon*distanceToHorizon - 2*distanceToHorizon*distance + distance*distance + r* r)- r;
		bulge             = r-Math.sqrt(4* r* r - distance*distance)/2;
		drop              = r-Math.sqrt(r* r-distance*distance);
		dipRadians        = Math.asin(distanceToHorizon/(r+userHeight));
		dipDegrees        = dipRadians * 180 / Math.PI;

		refDistanceToHorizon = Math.sqrt((rr+userHeight)*(rr+userHeight) - rr*rr); // refracted distance to horizon (feet or meters)
		refHidden            = Math.sqrt(refDistanceToHorizon*refDistanceToHorizon - 2*refDistanceToHorizon*distance + distance*distance + rr*rr)-rr;
		refDrop              = rr-Math.sqrt(rr*rr-distance*distance);
		refDipRadians        = Math.asin(refDistanceToHorizon/(rr+userHeight));
		refDipDegrees        = refDipRadians * 180 / Math.PI;

	}

	private String[] distanceString(double value)
	{
		if(imperial){

			Log.d(TAG, "HEREHERE: " + value);
			//Log.d(TAG, "!!!value: " + value);

			double inches = (value*12*100)/100;
			double feet   = (value*100)/100;
			double miles  = (value/5280*100)/100;

			if(feet < 5280)
				return new String[]{ DF2.format(feet)  + " Feet", DF2.format(inches) + " Inches"};
			else
				return new String[]{DF2.format(miles) + " Miles", DF2.format(feet)  + " Feet"};
		}
		else{

			double m  = (value * 100) / 100;
			double km = (value / 1000 * 100) / 100;

			if(m < 1000)
				return new String[]{DF2.format(m) + " meters", ""};
			else
				return new String[]{DF2.format(km) + " km", DF2.format(m) +  " m"};
		}
	}

	public String[] getRText()
	{
		return distanceString(r);
	}
	public String[] getDistanceText()
	{
		return distanceString(distance);
	}

	public String[] getViewerHeightText()
	{
		return distanceString(userHeight);
	}

	public String[] getHiddenText()
	{
		String[] hiddenText;
		if(distanceToHorizon < distance)
			hiddenText = distanceString(hidden);
		else
			hiddenText = new String[]{"None, horizon is beyond the target distance", ""};

		return hiddenText;
	}

	public String[] getDistanceToHorizon()
	{
		return distanceString(distanceToHorizon);
	}

	public String[] getBulgeText()
	{
		return distanceString(bulge);
	}

	public String[] getDropText()
	{
		return distanceString(drop);
	}

	public String[] getDipText()
	{
		return new String[]{DF4.format(dipDegrees)  + " Degrees", DF4.format(dipRadians) + " Radians"};
	}

	public String[] getTiltText()
	{
		double tiltRadians = distance / r;
		double tiltDegrees = distance / r * 180 / Math.PI;
		String[] tiltText = new String[]{DF3.format(tiltDegrees) + " Degrees", DF4.format(tiltRadians) + " Radians"}; // 3 dec place and 4 dec place
		return tiltText;
	}

	public String[] getRefRadiusText()
	{
		return distanceString(rr);
	}

	public String[] getRefHorizonText()
	{
		return distanceString(refDistanceToHorizon);
	}

	public String[] getRefHiddenText()
	{
		String[] refHiddenText;

		if(refDistanceToHorizon < distance)
			refHiddenText = distanceString(refHidden);
		else
			refHiddenText = new String[]{"None, refracted horizon is beyond the target distance", ""};
		return refHiddenText;
	}

	public String[] getRefDipText()
	{
		return new String[]{DF4.format(refDipDegrees) + " Degrees", DF4.format(refDipRadians) + " Radians"};
	}

	public String[] refDropText()
	{
		return distanceString(refDrop);
	}
}









