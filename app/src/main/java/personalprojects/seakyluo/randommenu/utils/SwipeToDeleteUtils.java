package personalprojects.seakyluo.randommenu.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import org.apache.commons.lang3.StringUtils;

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
        apply(recyclerView, context, null, remove, add, getName);
    }

    public static <T> void apply(RecyclerView recyclerView,
                                 Context context,
                                 Function<Integer, Boolean> check,
                                 Function<Integer, T> remove,
                                 Consumer<T> add,
                                 Function<T, String> getName) {
        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getBindingAdapterPosition();
                boolean shouldNotBeDeleted = check != null && check.apply(position);
                T item = remove.apply(position);
                String name = getName.apply(item);
                if (shouldNotBeDeleted){
                    Toast.makeText(context, String.format("\"%s\"正在使用中，无法删除", name), Toast.LENGTH_SHORT).show();
                    add.accept(item);
                    recyclerView.scrollToPosition(position);
                    return;
                }
                if (StringUtils.isEmpty(name)) {
                    return;
                }
                Snackbar snackbar = Snackbar.make(recyclerView, String.format("\"%s\"已被删除", name), Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", view -> {
                    add.accept(item);
                    recyclerView.scrollToPosition(position);
                });
                snackbar.show();
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}
