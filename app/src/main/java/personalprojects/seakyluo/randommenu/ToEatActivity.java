package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;

import personalprojects.seakyluo.randommenu.adapters.impl.SimpleFoodListAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.InputDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class ToEatActivity extends SwipeBackActivity {
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
        findViewById(R.id.sf_toolbar).setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.setData(Settings.settings.ToEat);
        adapter.SetOnDeletedClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format(getString(R.string.ask_delete), data));
            dialog.setYesListener(dv -> {
                Settings.settings.ToEat.remove(data);
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
                if (Settings.settings.ToEat.remove(text)) adapter.remove(text);
                Settings.settings.ToEat.with(text, 0);
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
        titleText.setText(Tag.format(this, R.string.to_eat, adapter.getData().size()));
    }

    @Override
    public void finish() {
        if (updated) Helper.save();
        updated = false;
        super.finish();
    }
}
