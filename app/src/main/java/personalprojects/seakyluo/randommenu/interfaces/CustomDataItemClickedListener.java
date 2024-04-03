package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;

public interface CustomDataItemClickedListener<T, K> {
    void click(CustomAdapter<T>.CustomViewHolder viewHolder, K data);
}