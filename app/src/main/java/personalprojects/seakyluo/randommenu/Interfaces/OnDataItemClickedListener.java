package personalprojects.seakyluo.randommenu.Interfaces;

import personalprojects.seakyluo.randommenu.CustomAdapter;

public interface OnDataItemClickedListener<T> {
    void Click(CustomAdapter<T>.CustomViewHolder viewHolder, T data);
}
