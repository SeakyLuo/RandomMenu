package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class ChooseTagActivity extends AppCompatActivity {
    public static final String TAG = "tag";
    private AutoCompleteTextView tag_box;
    private RecyclerView recyclerView;
    private TagListAdapter tagAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tag);

        tag_box = findViewById(R.id.tag_box);
        tag_box.requestFocus();
        tag_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = tag_box.getText().toString();
                if (tag.length() > 0){
                    Intent intent = new Intent();
                    intent.putExtra(TAG, tag);
                    setResult(RESULT_OK, intent);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });

        tagAdapter = new TagListAdapter(new ListedTagClickedListener() {
            @Override
            public void TagClicked(Tag tag) {
                tag_box.setText(tag.getName());
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(true);
        tagAdapter.setActivity(this);
        recyclerView.setAdapter(tagAdapter);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
