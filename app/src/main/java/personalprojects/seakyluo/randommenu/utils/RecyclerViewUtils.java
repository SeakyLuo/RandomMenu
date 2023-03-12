package personalprojects.seakyluo.randommenu.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.Pager;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class RecyclerViewUtils {

    public static <T> void setAsPaged(RecyclerView recyclerView, Pager<T> pager) {
        setAsPaged(recyclerView, 20, pager);
    }

    public static <T> void setAsPaged(RecyclerView recyclerView, int pageSize, Pager<T> pager){

        final int[] currentPage = {1};
        final int[] lastItemPosition = {0};

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                CustomAdapter<T> adapter = (CustomAdapter<T>) recyclerView.getAdapter();
                if (adapter == null){
                    return;
                }
                if (newState == SCROLL_STATE_IDLE && lastItemPosition[0] == adapter.getItemCount()){
                    adapter.add(pager.selectByPage(++currentPage[0], pageSize).getData());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    int firstItem = manager.findFirstVisibleItemPosition();
                    int lastItem = manager.findLastCompletelyVisibleItemPosition();
                    lastItemPosition[0] = firstItem + (lastItem - firstItem) + 1;
                }
            }
        });
    }

}
