package earth.curve.calculator;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.xwray.groupie.GroupAdapter;

import earth.curve.calculator.databinding.CurveActivityBinding;


public class CurveCalculatorActivity extends BaseActivity<CurveActivityBinding>
{
	private static final String TAG = CurveCalculatorActivity.class.getSimpleName();

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

		mViewModel.computeCurve();
	}
}
