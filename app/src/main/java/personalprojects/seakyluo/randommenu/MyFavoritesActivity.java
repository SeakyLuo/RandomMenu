package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class MyFavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView myFavorites;
    private FoodListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.food_list_recycler_view);
        myFavorites = findViewById(R.id.title_text_view);
        adapter = new FoodListAdapter(((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.SetFood(food);
            dialog.SetFoodEditedListener((before, after) -> {
                adapter.setData(Settings.settings.Favorites);
                dialog.SetFood(after);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.WARNING);
        }));
        myFavorites.setText(Tag.Format("My Favorites", Settings.settings.Favorites.Count()));
        adapter.setData(Settings.settings.Favorites);
        adapter.setActivity(this);
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