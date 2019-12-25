package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.jude.swipbackhelper.SwipeBackHelper;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

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
        adapter.SetData(Settings.settings.DislikeFood);
        adapter.SetOnDeleteClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format(getString(R.string.ask_delete), data));
            dialog.setOnYesListener(dv -> {
                Settings.settings.DislikeFood.Remove(data);
                adapter.Remove(data);
                SetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.sf_fab).setOnClickListener(v -> {
            InputDialog dialog = new InputDialog();
            dialog.SetHint(getString(R.string.food_name));
            dialog.SetConfirmListener(text -> {
                if (Settings.settings.DislikeFood.Remove(text)) adapter.Remove(text);
                Settings.settings.DislikeFood.Add(text, 0);
                adapter.Add(text, 0);
                recyclerView.scrollToPosition(0);
                updated = true;
                SetTitle();
            });
            dialog.showNow(getSupportFragmentManager(), InputDialog.TAG);
        });
        SetTitle();
    }

    public void SetTitle(){
        titleText.setText(Tag.Format(this, R.string.dislike_food, adapter.GetData().Count()));
    }

    @Override
    public void finish() {
        if (updated) Helper.Save(this);
        updated = false;
        super.finish();
    }
}
