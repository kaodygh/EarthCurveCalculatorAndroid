package earth.curve.calculator;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


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
}
