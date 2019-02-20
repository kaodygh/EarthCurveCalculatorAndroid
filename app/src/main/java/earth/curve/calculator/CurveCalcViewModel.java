package earth.curve.calculator;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.xwray.groupie.GroupAdapter;

import earth.curve.calculator.model.Calc;

import static earth.curve.calculator.model.Calc.DF5;


public class CurveCalcViewModel extends ViewModel implements Observable
{
	private static final String TAG = CurveCalcViewModel.class.getSimpleName();

	private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

	private GroupAdapter mGroupieAdapter;

	private boolean mIsImperial = true;

	private int    mPer           = 5280;
	private String mBigUnits      = "Miles";
	private String mSmallUnits    = "Feet";
	private double mSmallMult     = 0;
	private double mBigMult       = 0;

	private double distance;

	private double userHeight;


	public void setGroupieAdapter(GroupAdapter groupieAdapter)
	{
		mGroupieAdapter = groupieAdapter;
	}

	public void onDistanceChanged(CharSequence s, int start, int before, int count)
	{
		if(TextUtils.isEmpty(s)){
			return;
		}
		distance = Double.parseDouble(s.toString());
		computeCurve();
	}

	public void onHeightChanged(CharSequence s, int start, int before, int count)
	{
		if(TextUtils.isEmpty(s)){
			return;
		}
		userHeight = Double.parseDouble(s.toString());
		computeCurve();
	}

	public void onUnitUpdated(RadioGroup group, int checkedId)
	{
		mIsImperial = (checkedId == R.id.imperial);

		updateUnits();
		computeCurve();
	}

	public void clearClicked(View view)
	{
		setDistance(0.0);
		setUserHeight(0.0);
		mGroupieAdapter.clear();
	}

	public void computeCurve()
	{
		if(distance == 0 || userHeight == 0){
			return;
		}

		StringBuilder output = new StringBuilder();

		double distanceInUnits =  distance * mPer;  // distance in feet or meters

		int rVal = mIsImperial ? 3959 : 6371;

		double r = rVal * mPer; // radius of the earth in (feet or meters)

		double rr  = 7.0 / 6.0 * r;

		mGroupieAdapter.clear();

		Calc calc = new Calc(mIsImperial, distanceInUnits, userHeight, r, rr);

		mGroupieAdapter.add(new SectionTitleItemHolder("Observer"));
		mGroupieAdapter.add(new CalcItemHolder("Distance",      calc.getDistanceText()));
		mGroupieAdapter.add(new CalcItemHolder("Viewer Height", calc.getViewerHeightText()));
		mGroupieAdapter.add(new CalcItemHolder("Radius",        calc.getRText()));

		mGroupieAdapter.add(new SectionTitleItemHolder("Without Standard Refraction"));
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

		mGroupieAdapter.add(new SectionTitleItemHolder("Extra..."));
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
		double meters2Feet   = 3.28084;
		double kiloms2Meters = 0.621371;

		if(mIsImperial){
			mPer        = 5280;  // feet per mile
			mBigUnits   = "Miles";
			mSmallUnits = "Feet";
			mSmallMult  = meters2Feet;
			mBigMult    = kiloms2Meters;
		}
		else{
			mPer        = 1000; // m per km
			mBigUnits   = "Kilometers";
			mSmallUnits = "Meters";
			mSmallMult  = 1.0 / meters2Feet;
			mBigMult    = 1.0 / kiloms2Meters;
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
		notifyPropertyChanged(BR.distance);
	}

	@Bindable
	public void setUserHeight(double uh)
	{
		userHeight = uh;
		notifyPropertyChanged(BR.height);
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
	public void removeOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback)
	{
		callbacks.remove(callback);
	}

	void notifyPropertyChanged(int fieldId)
	{
		callbacks.notifyCallbacks(this, fieldId, null);
	}
}
