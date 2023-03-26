package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
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
            Intent intent = new Intent(this, EditSelfMadeFoodActivity.class);
            intent.putExtra(EditSelfMadeFoodActivity.DATA, new SelfMadeFood(data));
            startActivityForResult(intent, ActivityCodeConstant.FOOD_CODE);
        });
        adapter.SetOnDeletedClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format(getString(R.string.ask_delete), data));
            dialog.setYesListener(dv -> {
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
            dialog.setConfirmListener(text -> {
                if (Settings.settings.ToCook.remove(text)) adapter.remove(text);
                Settings.settings.ToCook.with(text, 0);
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
        titleText.setText(Tag.format(this, R.string.to_cook, adapter.getData().size()));
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
        SelfMadeFood food = (SelfMadeFood) data.getParcelableExtra(EditSelfMadeFoodActivity.DATA);
        String foodName = food.getName();
        Settings.settings.ToCook.remove(foodName);
        adapter.remove(foodName);
        updated = true;
        SetTitle();
    }
}
