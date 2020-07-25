package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class MoreSettingsActivity extends SwipeBackActivity {

    private Switch autoTagSwitch;
    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_settings);

        autoTagSwitch = findViewById(R.id.auto_tag_switch);
        autoTagSwitch.setChecked(Settings.settings.AutoTag);
        autoTagSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            Settings.settings.AutoTag = b;
            updated = true;
        });
    }

    @Override
    public void finish() {
        if (updated) Helper.Save();
        updated = false;
        super.finish();
    }
}
