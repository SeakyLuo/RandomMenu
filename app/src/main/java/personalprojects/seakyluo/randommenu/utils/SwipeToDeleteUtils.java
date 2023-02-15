package personalprojects.seakyluo.randommenu.utils;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;
import personalprojects.seakyluo.randommenu.helpers.SwipeToDeleteCallback;

public class SwipeToDeleteUtils {

    public static <T> void apply(RecyclerView recyclerView,
                                 Context context,
                                 Function<Integer, T> remove,
                                 Consumer<T> add,
                                 Function<T, String> getName) {
        new ItemTouchHelper(new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                T item = remove.apply(position);
                Snackbar snackbar = Snackbar.make(recyclerView, String.format("\"%s\"已被删除", getName.apply(item)), Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", view -> {
                    add.accept(item);
                    recyclerView.scrollToPosition(position);
                });
                snackbar.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

}
