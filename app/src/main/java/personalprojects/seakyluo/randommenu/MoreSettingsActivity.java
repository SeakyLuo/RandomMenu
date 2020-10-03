package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Settings;

public class MoreSettingsActivity extends SwipeBackActivity {

    private Switch autoTagSwitch;
    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_settings);

        autoTagSwitch = findViewById(R.id.auto_tag_switch);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.tag_map_button).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TagMapActivity.class));
            overridePendingTransition(R.anim.push_left_in, 0);
        });

        autoTagSwitch.setChecked(Settings.settings.AutoTag);
        autoTagSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            Settings.settings.AutoTag = b;
            updated = true;
        });
    }

    @Override
    public void finish() {
        if (updated) Helper.save();
        updated = false;
        super.finish();
    }
}
