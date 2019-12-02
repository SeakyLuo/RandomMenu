package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class ChooseTagActivity extends AppCompatActivity {
    public static final String TAG = "tag";
    private RecyclerView selectedTags;
    private AutoCompleteTextView tag_box;
    private TagAdapter tagAdapter;
    private TagListAdapter tagListAdapter;
    private ArrayList<Tag> original_tags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tag);

        tag_box = findViewById(R.id.tag_box);
        tag_box.requestFocus();
        final ArrayAdapter<String> suggestionTagListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        tag_box.setAdapter(suggestionTagListAdapter);
        tag_box.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag = suggestionTagListAdapter.getItem(position);
                ChooseTag(tagListAdapter.GetData().Find(t -> t.Name.equals(tag)));
                tag_box.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tag_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                suggestionTagListAdapter.clear();
                suggestionTagListAdapter.addAll(tagListAdapter.GetData().Without(tagAdapter.GetData()).Convert(t -> t.Name).ToArrayList());
                suggestionTagListAdapter.notifyDataSetChanged();
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
            if (tagAdapter.GetData().SameCollection(original_tags)){
                setResult(RESULT_CANCELED);
                finish();
            }else{
                AskYesNoDialog dialog = new AskYesNoDialog();
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
                dialog.setMessage("You Have unsaved changes. \nDo you want to quit without saving?");
                dialog.setOnYesListener(view -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
                dialog.setOnNoListener(view -> {
                    FinishActivity();
                });
            }
        });

        findViewById(R.id.confirm_button).setOnClickListener(v -> SubmitTag());

        original_tags = getIntent().getParcelableArrayListExtra(TAG);
        Log.d("fuck", Settings.settings.toString());
        tagListAdapter = new TagListAdapter(Settings.settings.Tags, original_tags);
        tagListAdapter.SetTagClickedListener((viewHolder, tag) -> ChooseTag(tag));

        RecyclerView tag_recycler_view = findViewById(R.id.listed_tag_recycler_view);
        tag_recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        tag_recycler_view.setAdapter(tagListAdapter);

        tagAdapter = new TagAdapter(true);
        tagAdapter.SetData(original_tags);
        tagAdapter.SetTagCloseListener((viewHolder, tag) -> tagListAdapter.CheckTag(tag, false));
        selectedTags = findViewById(R.id.selected_tags_recycler_view);
        selectedTags.setAdapter(tagAdapter);
    }

    private void ChooseTag(Tag tag){
        if (tagAdapter.getItemCount() == Tag.MAX_TAGS){
            Toast.makeText(ChooseTagActivity.this, "Tags Limit!", Toast.LENGTH_SHORT).show();
        }else{
            if (tagAdapter.Contains(tag)){
                tagAdapter.Remove(tag);
            }else{
                tagAdapter.Add(tag, 0);
                selectedTags.scrollToPosition(0);
            }
        }
    }

    private void SubmitTag(){
        String tag_name = tag_box.getText().toString();
        if (tag_name.length() > 0){
            Tag tag = new Tag(tag_name);
            if (!tagAdapter.Contains(tag)){
                if (tagListAdapter.Contains(tag)) tagListAdapter.CheckTag(tag, true);
                else{
                    tagListAdapter.Add(tag);
                    tagListAdapter.SetTagChecked(tag, true);
                }
                tagAdapter.Add(tag);
            }
            tag_box.setText("");
        }else{
            FinishActivity();
        }
    }

    private void FinishActivity(){
        Intent intent = new Intent();
        AList<Tag> tags = tagAdapter.GetData();
        intent.putExtra(TAG, tags.ToArrayList());
        if (tags.SameCollection(original_tags)) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}