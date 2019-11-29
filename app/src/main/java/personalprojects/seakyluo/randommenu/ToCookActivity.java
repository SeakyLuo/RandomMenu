package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Food;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class ToCookActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SimpleFoodListAdapter adapter;
    private boolean updated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_cook);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.tc_fab).setOnClickListener(v -> {
            InputDialog dialog = new InputDialog();
            dialog.SetPlaceHolder("Food Name");
            dialog.SetConfirmListener(text -> {
                Settings.settings.ToCook.Add(text, 0);
                adapter.Add(text, 0);
                updated = true;
            });
            dialog.showNow(getSupportFragmentManager(), InputDialog.TAG);
        });
        recyclerView = findViewById(R.id.food_list_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.SetData(Settings.settings.ToCook);
        adapter.SetOnDataItemClickedListener((viewHolder, data) -> {
            Intent intent = new Intent(this, EditFoodActivity.class);
            intent.putExtra(EditFoodActivity.FOOD, new Food(data));
            startActivityForResult(intent, EditFoodActivity.FOOD_CODE);
        });
        adapter.SetOnDeleteClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format("Do you want to delete %s?", data));
            dialog.setOnYesListener(dv -> {
                Settings.settings.ToCook.Remove(data);
                adapter.Remove(data);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.WARNING);
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void finish() {
        if (updated) Helper.Save(this);
        updated = false;
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String food = ((Food) data.getParcelableExtra(EditFoodActivity.FOOD)).Name;
        Settings.settings.ToCook.Without(food);
        adapter.Remove(food);
        updated = true;
    }
}
