package personalprojects.seakyluo.randommenu.activities.impl;

import android.os.Bundle;
import android.widget.EditText;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.utils.BackupUtils;
import personalprojects.seakyluo.randommenu.models.Settings;

public class NoteActivity extends SwipeBackActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        editText = findViewById(R.id.editText);
        editText.setText(Settings.settings.Note);
    }

    @Override
    public void finish(){
        Settings.settings.Note = editText.getText().toString();
        BackupUtils.save();
        super.finish();
    }
}
