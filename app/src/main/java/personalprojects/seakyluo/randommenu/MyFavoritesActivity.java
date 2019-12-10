package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class MyFavoritesActivity extends AppCompatActivity {
    private FoodListFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.title_text_view);
        title.setText(Tag.Format(this,R.string.my_favorites, Settings.settings.Favorites.Count()));

        fragment = (FoodListFragment) getSupportFragmentManager().findFragmentById(R.id.food_list_fragment);
        fragment.SetData(Settings.settings.Favorites);
        fragment.SetFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.SetFood(food);
            dialog.SetFoodEditedListener((before, after) -> {
                fragment.SetData(Settings.settings.Favorites);
                dialog.SetFood(after);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        fragment.AttachItemTouchHelper(new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
//                int position = viewHolder.getAdapterPosition();
//                adapter.data.Pop(position);
//                adapter.notifyDataSetChanged();
            }
        }));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
