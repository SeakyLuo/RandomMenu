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

public class ToEatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SimpleFoodListAdapter adapter;
    private boolean updated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_eat);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.te_fab).setOnClickListener(v -> {
            InputDialog dialog = new InputDialog();
            dialog.SetHint("Food Name");
            dialog.SetConfirmListener(text -> {
                if (Settings.settings.ToEat.Remove(text)) adapter.Remove(text);
                Settings.settings.ToEat.Add(text, 0);
                adapter.Add(text, 0);
                updated = true;
            });
            dialog.showNow(getSupportFragmentManager(), InputDialog.TAG);
        });
        recyclerView = findViewById(R.id.food_list_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SimpleFoodListAdapter();
        adapter.SetData(Settings.settings.ToEat);
        adapter.SetOnDeleteClickedListener((viewHolder, data) -> {
            AskYesNoDialog dialog = new AskYesNoDialog();
            dialog.setMessage(String.format("Do you want to delete %s?", data));
            dialog.setOnYesListener(dv -> {
                Settings.settings.ToEat.Remove(data);
                adapter.Remove(data);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
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
}
