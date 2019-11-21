package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class ChooseTagActivity extends AppCompatActivity {
    public static final String TAG = "tag";
    private AutoCompleteTextView tag_box;
    private TagListAdapter tagListAdapter;
    private TagFragment tagFragment;
    private ArrayList<Tag> original_tags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tag);

        tag_box = findViewById(R.id.tag_box);
        tag_box.requestFocus();
        tag_box.setAdapter(new TagListAdapter(((viewHolder, tag) -> {

        })));
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
        tag_box.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                SubmitTag();
                return true;
            }
            return false;
        });

        findViewById(R.id.back_button).setOnClickListener(v -> {
            if (tagFragment.GetTags().SameCollection(original_tags)){
                setResult(RESULT_CANCELED);
            }else{
                // ask save
                setResult(RESULT_CANCELED);
            }
            finish();
        });

        findViewById(R.id.confirm_button).setOnClickListener(v -> SubmitTag());

        tagListAdapter = new TagListAdapter((viewHolder, tag) -> {
            if (tagFragment.GetData().Count() == Tag.MAX_TAGS){
                Toast.makeText(ChooseTagActivity.this, "Tags Limit!", Toast.LENGTH_SHORT).show();
            }else{
                if (tagFragment.Contains(tag)){
                    tagFragment.Remove(tag);
                }else{
                    tagFragment.Add(new ToggleTag(tag, true));
                }
            }
        });
        original_tags = getIntent().getParcelableArrayListExtra(TAG);
        HashSet<Tag> tag_set = new HashSet<>(original_tags);
        for (Tag tag : Settings.settings.tags) tagListAdapter.add(new ToggleTag(tag, tag_set.contains(tag)));
        tagListAdapter.addAll(0, new AList<>(tag_set).SetDifference(Settings.settings.tags).Convert(t -> new ToggleTag(t, true)).ToArrayList());

        RecyclerView tag_recycler_view = findViewById(R.id.listed_tag_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        tag_recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        tag_recycler_view.setLayoutManager(manager);
        tag_recycler_view.setNestedScrollingEnabled(true);
        tagListAdapter.setActivity(this);
        tag_recycler_view.setAdapter(tagListAdapter);

        tagFragment = new TagFragment();
        tagFragment.SetLinear(true);
        tagFragment.SetClose(true);
        tagFragment.SetTags(original_tags);
        tagFragment.SetTagClickedListener(((viewHolder, tag) -> {
            ((TagAdapter.ViewHolder)viewHolder).SetCloseButtonVisibility(tag.Toggle());
            tagListAdapter.set(tag, tagListAdapter.getData().IndexOf(tag));
        }));
        getSupportFragmentManager().beginTransaction().add(R.id.tags_frame, tagFragment).commit();
    }

    private void SubmitTag(){
        String tag_name = tag_box.getText().toString();
        if (tag_name.length() > 0){
            Tag tag = new Tag(tag_name);
            if (!tagListAdapter.Contains(tag)){
                tagListAdapter.add(new ToggleTag(tag, true));
                tagFragment.AddTag(tag);
            }
            tag_box.setText("");
        }else{
            Intent intent = new Intent();
            ArrayList<Tag> tags = tagFragment.GetTags().ToArrayList();
            intent.putExtra(TAG, tags);
            if (tags.size() == 0) setResult(RESULT_CANCELED);
            else setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
