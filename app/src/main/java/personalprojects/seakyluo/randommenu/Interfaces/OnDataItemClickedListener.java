package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;

public interface OnDataItemClickedListener<T> {
    void click(CustomAdapter.CustomViewHolder viewHolder, T data);
}
