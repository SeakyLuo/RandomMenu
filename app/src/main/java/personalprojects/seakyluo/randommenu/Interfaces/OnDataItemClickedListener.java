package personalprojects.seakyluo.randommenu.Interfaces;

import personalprojects.seakyluo.randommenu.Adapters.CustomAdapter;

public interface OnDataItemClickedListener<T> {
    void Click(CustomAdapter.CustomViewHolder viewHolder, T data);
}
