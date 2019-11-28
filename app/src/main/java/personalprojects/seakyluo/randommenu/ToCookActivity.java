package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class ToCookActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SimpleFoodListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_cook);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.tc_fab).setOnClickListener(v -> {

        });
        recyclerView = findViewById(R.id.food_list_recycler_view);
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.ToCook);
        adapter.setActivity(this);
        adapter.SetOnDataItemClickedListener(((viewHolder, data) -> {
            Intent intent = new Intent(this, EditFoodActivity.class);
            intent.putExtra(EditFoodActivity.FOOD, new Food(data));
            startActivityForResult(intent, EditFoodActivity.FOOD_CODE);
        }));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String food = ((Food) data.getParcelableExtra(EditFoodActivity.FOOD)).Name;
        Settings.settings.ToCook.Without(food);
        adapter.remove(food);
    }
}
