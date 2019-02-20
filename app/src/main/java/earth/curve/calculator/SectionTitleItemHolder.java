package earth.curve.calculator;

import earth.curve.calculator.R;
import com.xwray.groupie.databinding.BindableItem;

import earth.curve.calculator.databinding.ItemSectionTitleBinding;


public class SectionTitleItemHolder extends BindableItem<ItemSectionTitleBinding>
{
	private final String mTitle;

	public SectionTitleItemHolder(String title)
	{
		mTitle = title;
	}

	@Override
	public void bind(ItemSectionTitleBinding binding, int position)
	{
		binding.setTitle(mTitle);
	}

	@Override
	public int getLayout()
	{
		return R.layout.item_section_title;
	}
}
