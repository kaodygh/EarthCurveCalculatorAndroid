package earth.curve.calculator;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;

import com.xwray.groupie.GroupAdapter;

import earth.curve.calculator.SectionTitleItemHolder;

import static earth.curve.calculator.Calc.DF5;


public class CurveCalcViewModel extends ViewModel implements Observable
{
	private static final String TAG = CurveCalcViewModel.class.getSimpleName();

	private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

	private GroupAdapter mGroupieAdapter;

	private boolean mIsImperial = true;

	private int    mPer           = 5280;
	private String mBigUnits      = "Miles";
	private String mSmallUnits    = "Feet";
	private double mMeters2Feet   = 3.28084;  // meters to feet
	private double mKiloms2Meters = 0.621371; // km to miles
	private double mSmallMult     = 0;
	private double mBigMult       = 0;


	private double distance;

	private double userHeight;

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

	public void setGroupieAdapter(GroupAdapter groupieAdapter)
	{
		mGroupieAdapter = groupieAdapter;
	}

	public void onDistanceChanged(CharSequence s, int start, int before, int count)
	{
		if(TextUtils.isEmpty(s)){
			return;
		}
		Log.d(TAG, "onDistanceChanged " + s);
		distance = Double.parseDouble(s.toString());
		computeCurve();
	}

	public void onHeightChanged(CharSequence s, int start, int before, int count)
	{
		if(TextUtils.isEmpty(s)){
			return;
		}
		Log.d(TAG, "onHeightChanged " + s);
		userHeight = Double.parseDouble(s.toString());
		computeCurve();
	}

	public void onUnitUpdated(RadioGroup group, int checkedId)
	{
		Log.d(TAG, "onUnitUpdated " + checkedId);

		mIsImperial = (checkedId == R.id.imperial);
		Log.d(TAG, "!!!: " + (mIsImperial ? "imperial" : "metric"));



		updateUnits();

		computeCurve();
	}

	public void computeCurve()
	{
		if(distance == 0 || userHeight == 0){
			Log.d(TAG, "!!!Cannot have zero values!");
			return;
		}

		StringBuilder output = new StringBuilder();

		double distanceInUnits =  distance * mPer;  // distance in feet or meters

		int rVal = mIsImperial ? 3959 : 6371;

		double r = rVal * mPer; // radius of the earth in (feet or meters)

		double rr  = 7.0 / 6.0 * r;

		distanceToHorizon = Math.sqrt((r+userHeight)*(r+userHeight) - r*r); // distance to horizon (feet or meters)
		hidden            = Math.sqrt(distanceToHorizon*distanceToHorizon - 2*distanceToHorizon*distanceInUnits + distanceInUnits*distanceInUnits + r* r)- r;
		bulge             = r-Math.sqrt(4* r* r - distanceInUnits*distanceInUnits)/2;
		drop              = r-Math.sqrt(r* r-distanceInUnits*distanceInUnits);
		dipRadians        = Math.asin(distanceToHorizon/(r+userHeight));
		dipDegrees        = dipRadians * 180 / Math.PI;

		refDistanceToHorizon = Math.sqrt((rr+userHeight)*(rr+userHeight) - rr*rr); // refracted distance to horizon (feet or meters)
		refHidden            = Math.sqrt(refDistanceToHorizon*refDistanceToHorizon - 2*refDistanceToHorizon*distanceInUnits + distanceInUnits*distanceInUnits + rr*rr)-rr;
		refDrop              = rr-Math.sqrt(rr*rr-distanceInUnits*distanceInUnits);
		refDipRadians        = Math.asin(refDistanceToHorizon/(rr+userHeight));
		refDipDegrees        = refDipRadians * 180 / Math.PI;

		mGroupieAdapter.clear();

		Calc calc = new Calc(mIsImperial, distanceInUnits, userHeight, r, rr);

		mGroupieAdapter.add(new SectionTitleItemHolder("Observer Details"));
		mGroupieAdapter.add(new CalcItemHolder("Distance",      calc.getDistanceText()));
		mGroupieAdapter.add(new CalcItemHolder("Viewer Height", calc.getViewerHeightText()));
		mGroupieAdapter.add(new CalcItemHolder("Radius",        calc.getRText()));

		mGroupieAdapter.add(new SectionTitleItemHolder("Without Refraction"));
		mGroupieAdapter.add(new CalcItemHolder("Horizon",       calc.getDistanceToHorizon()));
		mGroupieAdapter.add(new CalcItemHolder("Bulge",         calc.getBulgeText()));
		mGroupieAdapter.add(new CalcItemHolder("Drop",          calc.getDropText()));
		mGroupieAdapter.add(new CalcItemHolder("Hidden",        calc.getHiddenText()));
		mGroupieAdapter.add(new CalcItemHolder("Dip",           calc.getDipText()));

		mGroupieAdapter.add(new SectionTitleItemHolder("With Standard Refraction"));
		mGroupieAdapter.add(new CalcItemHolder("Horizon",       calc.getRefHorizonText()));
		mGroupieAdapter.add(new CalcItemHolder("Drop",          calc.refDropText()));
		mGroupieAdapter.add(new CalcItemHolder("Hidden",        calc.getRefHiddenText()));
		mGroupieAdapter.add(new CalcItemHolder("Dip",           calc.getRefDipText()));

		mGroupieAdapter.add(new SectionTitleItemHolder("More..."));
		mGroupieAdapter.add(new CalcItemHolder("Tilt Angle",    calc.getTiltText()));

		double fovRadians   = Math.toRadians(60);
		double dropFraction = Math.sqrt(userHeight*(userHeight+2*r))/r*Math.tan(fovRadians/4)/2;
		double Z            = (r*Math.sqrt((r+userHeight)*(r+userHeight)-r*r))/(r+userHeight);
		double H            = (((r+userHeight)*(r+userHeight)-r*r)/(r+userHeight));
		double dropAngle    = (Math.atan(Z/H)-Math.atan(Z*Math.cos(fovRadians/2)/H));

		// Second method via: https://www.metabunk.org/are-lynchs-horizon-calculations-correct.t7877/#post-189661
		double dropAngle2 = Math.atan(2*dropFraction*Math.tan(fovRadians/2));

		mGroupieAdapter.add(new CalcItemHolder("Horizon Curve Fraction", new String[]{DF5.format(dropFraction),                 ""}));
		mGroupieAdapter.add(new CalcItemHolder("Horizon Curve Angle v1", new String[]{DF5.format(Math.toDegrees(dropAngle)),    ""}));
		mGroupieAdapter.add(new CalcItemHolder("Horizon Curve Angle v2", new String[]{DF5.format(Math.toDegrees(dropAngle2)),   ""}));

		Log.d(TAG, "!!! " + output.toString());
	}

	private void updateUnits()
	{
		if(mIsImperial){
			mPer        = 5280;  // feet per mile
			mBigUnits   = "Miles";
			mSmallUnits = "Feet";
			mSmallMult  = mMeters2Feet;
			mBigMult    = mKiloms2Meters;
		}
		else{
			mPer        = 1000; // m per km
			mBigUnits   = "Kilometers";
			mSmallUnits = "Meters";
			mSmallMult  = 1.0 / mMeters2Feet;
			mBigMult    = 1.0 / mKiloms2Meters;
		}

		notifyPropertyChanged(earth.curve.calculator.BR.distanceHeader);
		notifyPropertyChanged(earth.curve.calculator.BR.heightHeader);
		notifyPropertyChanged(earth.curve.calculator.BR.distance);
		notifyPropertyChanged(earth.curve.calculator.BR.height);

	}

	@Bindable
	public void setDistance(double d)
	{
		distance = d;
	}

	@Bindable
	public String getDistance()
	{
		return String.valueOf(mBigMult * distance);
	}

	@Bindable
	public String getHeight()
	{
		return String.valueOf(mSmallMult * userHeight);
	}

	@Bindable
	public String getDistanceHeader()
	{
		return "Distance in " + mBigUnits;
	}

	@Bindable
	public String getHeightHeader()
	{
		return "Height in " + mSmallUnits;
	}

	@Override
	public void addOnPropertyChangedCallback(
		Observable.OnPropertyChangedCallback callback)
	{
		callbacks.add(callback);
	}

	@Override
	public void removeOnPropertyChangedCallback(
		Observable.OnPropertyChangedCallback callback)
	{
		callbacks.remove(callback);
	}

	/**
	 * Notifies observers that all properties of this instance have changed.
	 */
	void notifyChange()
	{
		callbacks.notifyCallbacks(this, 0, null);
	}

	/**
	 * Notifies observers that a specific property has changed. The getter for the
	 * property that changes should be marked with the @Bindable annotation to
	 * generate a field in the BR class to be used as the fieldId parameter.
	 *
	 * @param fieldId The generated BR id for the Bindable field.
	 */
	void notifyPropertyChanged(int fieldId)
	{
		callbacks.notifyCallbacks(this, fieldId, null);
	}
}
