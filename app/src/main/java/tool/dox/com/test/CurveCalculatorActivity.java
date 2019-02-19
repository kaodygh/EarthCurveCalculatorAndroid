package tool.dox.com.test;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.xwray.groupie.GroupAdapter;

import tool.dox.com.test.databinding.CurveActivityBinding;


public class CurveCalculatorActivity extends BaseActivity<CurveActivityBinding>
{
	private static final String TAG = CurveCalculatorActivityBack.class.getSimpleName();

	private CurveCalcViewModel mViewModel;

	private GroupAdapter mGroupieAdapter;

	@Override
	boolean showBackButton()
	{
		return true;
	}

	@Override
	void setTheToolbar()
	{
		setSupportActionBar(mBinding.tb);
	}

	@Override
	int getBackIcon()
	{
		return R.drawable.ic_arrow_back_blue;
	}

	@Override
	int getLayoutId()
	{
		return R.layout.curve_activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mGroupieAdapter = new GroupAdapter();

		mViewModel = ViewModelProviders.of(this).get(CurveCalcViewModel.class);
		mBinding.setViewmodel(mViewModel);

		mGroupieAdapter = new GroupAdapter();
		mViewModel.setGroupieAdapter(mGroupieAdapter);

		mBinding.list.setAdapter(mGroupieAdapter);

		//((EditText)mBinding.getRoot().findViewById(R.id.distance)).setText("100");
		//((EditText)mBinding.getRoot().findViewById(R.id.height)).setText("10");
		//mViewModel.setCurrentDistance(100);
		//mViewModel.setCurrentHeight(10);

		mViewModel.computeCurve();

		//mBinding.distance.addTextChangedListener(mTextWatcher);
		//mBinding.height.addTextChangedListener(mTextWatcher);
		//String html = computeCurve();
		//mWebView.loadData(html, "text/html; charset=UTF-8", null);
	}

}


/*


function updateUnits (change) {
	if (isImperial){
		per = 5280;  // feet per mile
		mBigUnits = "Miles";
		mSmallUnits = "Feet";
		mSmallMult = mMeters2Feet;
		mBigMult = mKiloms2Meters;
	}
	else  {
		per = 1000; // m per km
		mBigUnits = "Kilometers";
		mSmallUnits = "Meters";
		mSmallMult = 1.0/mMeters2Feet;
		mBigMult = 1.0/mKiloms2Meters;

	}

	document.getElementById("distanceUnits").innerHTML = mBigUnits;
	document.getElementById("heightUnits").innerHTML = mSmallUnits;
	document.getElementById("radiusUnits").innerHTML = mBigUnits;

	if (change) {
		$('#distance').val(mBigMult*Number(document.getElementById("distance").value));
		$('#height').val(mSmallMult*Number(document.getElementById("height").value));
		var r = Number(document.getElementById("radius").value);
		if (isImperial && r == 6371)
			r = 3959;
		else if (!isImperial && r == 3959)
			r = 6371;
		else
			r = r * mBigMult;
		$('#radius').val(r);
    }
}

*/




/*

double dropt = r / Math.cos(d / r) - r;
		double x2    = r/Math.cos(d/r - Math.asin(Math.sqrt(h*(2*r+h)) / (r+h) ) ) - r;

		double fovRadians   = Math.toRadians(fovDegrees);
		double dropFraction = Math.sqrt(h*(h+2*r))/r*Math.tan(fovRadians/4)/2;
		double dropPixels   = dropFraction*fovPixels;
		double Z            = (r*Math.sqrt((r+h)*(r+h)-r*r))/(r+h);
		double H            = (((r+h)*(r+h)-r*r)/(r+h));
		double dropAngle    = (Math.atan(Z/H)-Math.atan(Z*Math.cos(fovRadians/2)/H));

		// Second method via: https://www.metabunk.org/are-lynchs-horizon-calculations-correct.t7877/#post-189661
		double dropAngle2 = Math.atan(2*dropFraction*Math.tan(fovRadians/2));

		String dropFractionText = "" + dropFraction;
		String dropPixelsText   = "" + dropPixels;
		String dropAngleText    = "" + Math.toDegrees(dropAngle);
		String dropAngle2Text   = "" + Math.toDegrees(dropAngle2);

		output.append("<br>");
		output.append("Distance d = "+distanceString(d)+"<br>");
		output.append("Radius of Earth r = "+distanceString(r)+"<br>");
		output.append("Distance to horizon, a = sqrt((r+h)*(r+h) - r*r), a = "+distanceString(a)+"<br>");

		output.append("<br><b>Hidden</b> is the amount of the distant object hidden by the curve of the Earth<br>");

		//output.append("Distance to horizon (in miles) = a/5280 = "+a_m+"<br>";
		if(a<d){
			output.append("Hidden = sqrt(a*a - 2*a*d + d*d + r*r)-r  = " + distanceString(x) + " " + distanceStringFull(x) + "<br><br>");
			output.append("The following is the hidden amount calculated using slightly more complex trig<br>");
			output.append("And assuming the 'distance' is the distance across the surface of the earth<br>");
			output.append("This is more accurate for large distances and heights<br>");
			output.append("But essentially the same for distances under 100 miles<br>");
			output.append("<b>True hidden = r/cos( d/r - asin(sqrt(h*(2*r+h)) / (r+h) ) ) - r = " + distanceString(x2) + "</b><br>");
		}
		else{
			output.append("Nothing is hidden, as the horizon is beyond the target distance<br>");
		}

		output.append("<br>");

	output.append("<br><b>Bulge</b> is the amount of rise of the earth's curve from a straight line connecting two points on the surface<br>");
		output.append("'Bulge' b = r-sqrt(4*r*r - d*d)/2, b = " + distanceString(b) + "<br>");

		output.append("<br>");
		output.append("<b>Drop</b> is the amount the curve of the earth drops away from level, it can be calculated in various ways<br>");
		output.append("which are all <i>about the same</i> for under 100 miles distance<br>");
		output.append("<ul>");
		output.append("<li>Drop as r-sqrt(r^2-d^2) = "+distanceString(drop)+" "+distanceStringFull(drop)+" (perpendicular to level line)</li>");
		output.append("<li>Drop calculated as 8 inches per mile squared = 8 * d*d / 12 = " + distanceString(drop8) + "</li>");
		output.append("<li>Drop calculated with trig = r/cos(d/r) - r = " + distanceString(dropt) + " (perpendicular to target surface)</li>");
		output.append("</ul>");

		output.append("<br>");
		output.append("<b>Tilt Angle</b> is angle between 'up' at the camera and 'up' at the distant object<br>");
		output.append("It is the amount a distant object is leaning away from you<br>");
		output.append("It's the distance divided by the radius (in radians)<br>");
		//output.append("d/r = "+ d.toFixed(2)+"/"+r+" = "+tilt.toFixed(8)+" radians, *180/PI = <b>" + tilt_d.toFixed(8)+"</b> degrees<br>");

		output.append("<br>");
		output.append("<b>Horizon Dip</b> is the angle that the horizon is below level<br>");
		output.append("as seen from the viewer height<br>");
		//output.append("It's arcsin(a/(r+h)), or arcsin("+a.toFixed(2)+"/("+r.toFixed(2)+"+"+h.toFixed(2)+")) = " + dip.toFixed(8)+" radians, *180/PI = " + dip_d.toFixed(8)+" degrees";

		output.append("<br>");
		output.append("<br>");
		output.append("<b>Horizon Curve</b> refers to how much the left and right ends of the horizon are<br>");
		output.append("vertically lower than the middle of the image, measured in various ways<br>");
		output.append("<img src='https://www.metabunk.org/sk/20170627-164622-8p6uj.jpg' /> <br>");

		output.append("<b>Horizon Curve Fraction</b> = Horizon Curve as a fraction of the <b>width</b> of the image<br>");
		output.append("We use a fraction of the width not height as it's simpler<br>");
		output.append("It's sqrt(h*(h+2*r))/r*tan(FOV/4)/2;</br>");
		output.append("<br>");

		output.append("<b>Horizon Curve Fraction</b> = Horizon Curve height in pixels<br>");
		output.append("Its the above, multipied by the width of the image in pixels<br>");
		output.append("<br>");

		output.append("<b>Horizon Curve Angle</b> = Horizon Curve height expresses as a visual angle<br>");
		output.append("There are two ways of calculating this which are very similar for most heights");

		output.append("");

 */
