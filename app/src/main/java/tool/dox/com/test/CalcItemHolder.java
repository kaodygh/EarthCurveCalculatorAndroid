package tool.dox.com.test;


import com.xwray.groupie.databinding.BindableItem;

import tool.dox.com.test.databinding.ItemCalcBinding;


public class CalcItemHolder extends BindableItem<ItemCalcBinding>
{
	private final String mName;
	private final String mValue;
	private final String mFooter;

	public CalcItemHolder(String name, String[] values)
	{
		mName   = name;
		mValue  = values[0];
		mFooter = values[1];
	}

	@Override
	public void bind(ItemCalcBinding binding, int position)
	{
		binding.setName(mName);
		binding.setValue(mValue);
		binding.setFooter(mFooter);
	}

	@Override
	public int getLayout()
	{
		return R.layout.item_calc;
	}
}
