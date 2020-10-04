package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.adapters.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class ToCookActivity extends SwipeBackActivity {
    private TextView titleText;
    private SimpleFoodListAdapter adapter;
    private boolean updated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_food_list);

        titleText = findViewById(R.id.title_text_view);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        RecyclerView recyclerView = findViewById(R.id.food_list_recycler_view);
        findViewById(R.id.sf_toolbar).setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.ToCook);
        adapter.SetOnDataItemClickedListener((viewHolder, data) -> {
            Intent intent = new Intent(this, EditFoodActivity.class);
            intent.putExtra(EditFoodActivity.FOOD, new Food(data));
            startActivityForResult(intent, EditFoodActivity.FOOD_CODE);
        });
        adapter.SetOnDeletedClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format(getString(R.string.ask_delete), data));
            dialog.setOnYesListener(dv -> {
                Settings.settings.ToCook.remove(data);
                adapter.remove(data);
                SetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.sf_fab).setOnClickListener(v -> {
            InputDialog dialog = new InputDialog();
            dialog.SetHint(getString(R.string.food_name));
            dialog.SetConfirmListener(text -> {
                if (Settings.settings.ToCook.remove(text)) adapter.remove(text);
                Settings.settings.ToCook.add(text, 0);
                adapter.add(text, 0);
                recyclerView.smoothScrollToPosition(0);
                updated = true;
                SetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), InputDialog.TAG);
        });
        SetTitle();
    }

    public void SetTitle(){
        titleText.setText(Tag.format(this, R.string.to_cook, adapter.getData().count()));
    }

    @Override
    public void finish() {
        if (updated) Helper.save();
        updated = false;
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String food = ((Food) data.getParcelableExtra(EditFoodActivity.FOOD)).Name;
        Settings.settings.ToCook.remove(food);
        adapter.remove(food);
        updated = true;
        SetTitle();
    }
}
