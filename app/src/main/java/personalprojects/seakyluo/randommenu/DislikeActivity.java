package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;

import personalprojects.seakyluo.randommenu.adapters.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
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
        SwipeBackHelper.onCreate(this);

        titleText = findViewById(R.id.title_text_view);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.food_list_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.DislikeFood);
        adapter.SetOnDeletedClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(getString(R.string.ask_delete, data));
            dialog.setOnYesListener(dv -> {
                Settings.settings.DislikeFood.remove(data);
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
                if (Settings.settings.DislikeFood.remove(text)) adapter.remove(text);
                Settings.settings.DislikeFood.add(text, 0);
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
        titleText.setText(Tag.format(this, R.string.dislike_food, adapter.getData().count()));
    }

    @Override
    public void finish() {
        if (updated) Helper.save();
        updated = false;
        super.finish();
    }
}
