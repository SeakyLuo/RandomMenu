package personalprojects.seakyluo.randommenu.interfaces;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;

public interface DataItemClickedListener<T> {
    void click(CustomAdapter<T>.CustomViewHolder viewHolder, T data);
}