package personalprojects.seakyluo.randommenu.activities.impl;

import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.utils.BackupUtils;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class DislikeActivity extends SwipeBackActivity {
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
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.DislikeFood);
        adapter.setItemDeletedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(getString(R.string.ask_delete, data));
            dialog.setYesListener(dv -> {
                Settings.settings.DislikeFood.remove(data);
                adapter.remove(data);
                updated = true;
                resetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.sf_fab).setOnClickListener(v -> {
            InputDialog dialog = new InputDialog();
            dialog.SetHint(getString(R.string.food_name));
            dialog.setConfirmListener(text -> {
                if (Settings.settings.DislikeFood.remove(text)) adapter.remove(text);
                Settings.settings.DislikeFood.add(0, text);
                adapter.add(text, 0);
                recyclerView.smoothScrollToPosition(0);
                updated = true;
                resetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), InputDialog.TAG);
        });
        resetTitle();
    }

    public void resetTitle(){
        titleText.setText(Tag.format(this, R.string.dislike_food, adapter.getData().size()));
    }

    @Override
    public void finish() {
        if (updated) BackupUtils.save();
        updated = false;
        super.finish();
    }
}
