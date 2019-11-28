package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import personalprojects.seakyluo.randommenu.Models.Settings;

public class ToCookActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SimpleFoodListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_cook);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.food_list_recycler_view);
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.ToCook);
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
//                int position = viewHolder.getAdapterPosition();
//                adapter.data.Pop(position);
//                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
