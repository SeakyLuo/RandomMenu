package personalprojects.seakyluo.randommenu.adapters;

import android.widget.BaseAdapter;

import personalprojects.seakyluo.randommenu.interfaces.EditSpinnerFilter;

public abstract class BaseEditSpinnerAdapter extends BaseAdapter {
    /**
     * editText输入监听
     * @return
     */
    public abstract EditSpinnerFilter getEditSpinnerFilter();

    /**
     * 获取需要填入editText的字符串
     * @param position
     * @return
     */
    public abstract String getItemString(int position);

}
