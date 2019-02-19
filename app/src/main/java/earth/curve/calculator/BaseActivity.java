package earth.curve.calculator;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.Date;


abstract class BaseActivity<B extends ViewDataBinding> extends AppCompatActivity
{
	private static final String TAG = BaseActivity.class.getSimpleName();

	protected B mBinding;

	abstract boolean showBackButton();

	abstract              void setTheToolbar();
	abstract @DrawableRes int  getBackIcon();
	abstract @LayoutRes   int  getLayoutId();


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mBinding = DataBindingUtil.setContentView(this, getLayoutId());

		setTheToolbar();

		if(showBackButton()){
			getSupportActionBar().setHomeAsUpIndicator(getBackIcon());
			getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton());
			getSupportActionBar().setDisplayShowHomeEnabled(showBackButton());
		}
	}

	@Override
	public boolean onSupportNavigateUp()
	{
		onBackPressed();
		return true;
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	protected void hideKeyboard()
	{
		View view = this.getCurrentFocus();
		if(view != null){
			InputMethodManager imm = (InputMethodManager) getSystemService(
				Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static int dpToPx(int dp)
	{
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	protected void boobybaby()
	{
		Date today   = new Date();
		Calendar endDate = Calendar.getInstance();

		endDate.setTime(today);
		endDate.add(Calendar.DATE, 10);

		if(today.after(endDate.getTime())){
			finish();
		}
	}
}
