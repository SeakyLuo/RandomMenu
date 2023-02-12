package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.adapters.TagListAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.fragments.TagsFragment;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class ChooseTagActivity extends SwipeBackActivity {
    public static final String SELECTED_TAGS = "selected", EXCLUDED_TAGS = "excluded", FOOD = "source_food";
    private AutoCompleteTextView inputBox;
    private TagListAdapter tagListAdapter;
    private TagsFragment tagsFragment;
    private String guessFoodName;
    private ArrayList<Tag> original_tags, selected_tags = new ArrayList<>(), excluded_tags;
    private ArrayAdapter<String> suggestionTagListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tag);

        inputBox = findViewById(R.id.input_box);
        inputBox.requestFocus();
        suggestionTagListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        inputBox.setAdapter(suggestionTagListAdapter);
        inputBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag = suggestionTagListAdapter.getItem(position);
                chooseTag(tagListAdapter.getData().first(t -> t.Name.equals(tag)));
                inputBox.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                suggestionTagListAdapter.clear();
                suggestionTagListAdapter.addAll(tagListAdapter.getData().without(tagsFragment.getData()).convert(t -> t.Name).toList());
                suggestionTagListAdapter.notifyDataSetChanged();
            }
        });
        inputBox.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submitTag();
                return true;
            }
            return false;
        });

        findViewById(R.id.back_button).setOnClickListener(v -> {
            if (tagsFragment.getData().equals(selected_tags)){
                setResult(RESULT_CANCELED);
                finish();
            }else{
                AskYesNoDialog dialog = new AskYesNoDialog();
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
                dialog.setMessage(R.string.ask_save);
                dialog.setOnYesListener(view -> {
                    finishActivity();
                });
                dialog.setOnNoListener(view -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
            }
        });
        findViewById(R.id.confirm_button).setOnClickListener(v -> submitTag());

        Intent intent = getIntent();
        original_tags = intent.getParcelableArrayListExtra(SELECTED_TAGS);
        excluded_tags = intent.getParcelableArrayListExtra(EXCLUDED_TAGS);
        guessFoodName = intent.getStringExtra(FOOD);

        selected_tags.addAll(original_tags);
        if (Settings.settings.AutoTag && selected_tags.size() == 0 && guessFoodName != null){
            for (Tag tag: Helper.guessTags(guessFoodName)){
                if (!selected_tags.contains(tag)){
                    selected_tags.add(tag);
                }
                if (!Settings.settings.Tags.contains(tag)){
                    Settings.settings.Tags.add(tag);
                }
            }
        }
        tagListAdapter = new TagListAdapter(this, Settings.settings.Tags.copy().without(excluded_tags), selected_tags);
        tagListAdapter.setTagClickedListener((viewHolder, tag) -> chooseTag(tag));

        RecyclerView tag_recycler_view = findViewById(R.id.listed_tag_recycler_view);
        tag_recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        tag_recycler_view.setAdapter(tagListAdapter);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        else
            tagsFragment = (TagsFragment) getSupportFragmentManager().getFragment(savedInstanceState, TagsFragment.TAG);
        tagsFragment.setSpanCount(1);
        tagsFragment.SetCloseable(true);
        tagsFragment.setData(selected_tags);
        tagsFragment.setTagClosedListener((viewHolder, tag) -> tagListAdapter.checkTag(tag, false));
    }

    private void chooseTag(Tag tag){
        if (tagsFragment.GetAdapter().getItemCount() == Tag.MAX_TAGS){
            Toast.makeText(ChooseTagActivity.this, getString(R.string.tag_limit), Toast.LENGTH_SHORT).show();
        }else{
            if (tagsFragment.contains(tag)){
                tagsFragment.remove(tag);
            }else{
                tagsFragment.add(tag, 0);
                tagsFragment.recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private void submitTag(){
        String tag_name = inputBox.getText().toString().trim();
        if (tag_name.length() == 0){
            finishActivity();
        }else{
            Tag tag = new Tag(tag_name);
            int index = tagsFragment.indexOf(tag);
            if (index == -1){
                if (tagListAdapter.contains(tag))
                    tagListAdapter.checkTag(tag, true);
                tagsFragment.add(tag, 0);
            }else{
                tagsFragment.move(index, 0);
            }
            inputBox.setText("");
        }
    }

    private void finishActivity(){
        Intent intent = new Intent();
        AList<Tag> tags = tagsFragment.getData();
        intent.putExtra(SELECTED_TAGS, tags.toArrayList());
        if (tags.equals(original_tags)) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, TagsFragment.TAG, tagsFragment);
    }
}