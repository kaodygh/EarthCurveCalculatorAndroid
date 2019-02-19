package earth.curve.calculator;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.xwray.groupie.GroupAdapter;

import earth.curve.calculator.model.UnitPref;

import static earth.curve.calculator.Calc.DF5;


public class CurveCalcViewModel extends ViewModel implements Observable
{
	private static final String TAG = CurveCalcViewModel.class.getSimpleName();

	private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

	private GroupAdapter mGroupieAdapter;

	double meters2Feet  = 3.28084;
	double kiloms2Miles = 0.621371;

	private UnitPref mUnit;

	private double distance;

	private double userHeight;

	public CurveCalcViewModel()
	{
		mUnit = getImperialConfig();
	}

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
		updateUnits((checkedId == R.id.imperial));
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

		double distanceInUnits =  distance * mUnit.getPer();  // distance in feet or meters

		int rVal = mUnit.isImperial() ? 3959 : 6371;

		double r = rVal * mUnit.getPer(); // radius of the earth in (feet or meters)

		double rr  = 7.0 / 6.0 * r;

		mGroupieAdapter.clear();

		Calc calc = new Calc(mUnit.isImperial(), distanceInUnits, userHeight, r, rr);

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
	}

	private void updateUnits(boolean isImperial)
	{
		if(isImperial){
			mUnit = getImperialConfig();
		}
		else{
			mUnit = getMetricConfig();
		}

		notifyPropertyChanged(earth.curve.calculator.BR.distanceHeader);
		notifyPropertyChanged(earth.curve.calculator.BR.heightHeader);
		notifyPropertyChanged(earth.curve.calculator.BR.distance);
		notifyPropertyChanged(earth.curve.calculator.BR.height);
	}

	private UnitPref getImperialConfig()
	{
		return new UnitPref(true, 5280,"Miles", "Feet", 0, 0);
	}

	private UnitPref getMetricConfig()
	{
		return new UnitPref(false, 1000,"Kilometers", "Meters", 1.0 / meters2Feet, 1.0 / kiloms2Miles);
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
		return String.valueOf(mUnit.getBigMult() * distance);
	}

	@Bindable
	public String getHeight()
	{
		return String.valueOf(mUnit.getSmallMult() * userHeight);
	}

	@Bindable
	public String getDistanceHeader()
	{
		return "Distance in " + mUnit.getBigUnits();
	}

	@Bindable
	public String getHeightHeader()
	{
		return "Height in " + mUnit.getSmallUnits();
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

	void notifyChange()
	{
		callbacks.notifyCallbacks(this, 0, null);
	}

	void notifyPropertyChanged(int fieldId)
	{
		callbacks.notifyCallbacks(this, fieldId, null);
	}
}
