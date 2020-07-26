package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import personalprojects.seakyluo.randommenu.Adapters.TagListAdapter;
import personalprojects.seakyluo.randommenu.Dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.Fragments.TagsFragment;
import personalprojects.seakyluo.randommenu.Helpers.Helper;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class ChooseTagActivity extends SwipeBackActivity {
    public static final String SELECTED_TAGS = "selected", EXCLUDED_TAGS = "excluded", FOOD = "source_food";
    private AutoCompleteTextView inputBox;
    private TagListAdapter tagListAdapter;
    private TagsFragment tagsFragment;
    private String guessFoodName;
    private ArrayList<Tag> selected_tags, excluded_tags;
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
                ChooseTag(tagListAdapter.GetData().First(t -> t.Name.equals(tag)));
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
                suggestionTagListAdapter.addAll(tagListAdapter.GetData().RemoveAll(tagsFragment.GetData()).Convert(t -> t.Name).ToArrayList());
                suggestionTagListAdapter.notifyDataSetChanged();
            }
        });
        inputBox.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                SubmitTag();
                return true;
            }
            return false;
        });

        findViewById(R.id.back_button).setOnClickListener(v -> {
            if (tagsFragment.GetData().Equals(selected_tags)){
                setResult(RESULT_CANCELED);
                finish();
            }else{
                AskYesNoDialog dialog = new AskYesNoDialog();
                dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
                dialog.setMessage(R.string.ask_save);
                dialog.setOnYesListener(view -> {
                    FinishActivity();
                });
                dialog.setOnNoListener(view -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
            }
        });
        findViewById(R.id.confirm_button).setOnClickListener(v -> SubmitTag());

        Intent intent = getIntent();
        selected_tags = intent.getParcelableArrayListExtra(SELECTED_TAGS);
        excluded_tags = intent.getParcelableArrayListExtra(EXCLUDED_TAGS);
        guessFoodName = intent.getStringExtra(FOOD);

        if (Settings.settings.AutoTag && guessFoodName != null){
            List<Tag> tags = Helper.GuessTags(guessFoodName);
            for (Tag tag: tags){
                if (!selected_tags.contains(tag)){
                    selected_tags.add(tag);
                }
                if (!Settings.settings.Tags.Contains(tag)){
                    Settings.settings.Tags.Add(tag);
                }
            }
        }
        tagListAdapter = new TagListAdapter(this, Settings.settings.Tags.RemoveAll(excluded_tags), selected_tags);
        tagListAdapter.SetTagClickedListener((viewHolder, tag) -> ChooseTag(tag));

        RecyclerView tag_recycler_view = findViewById(R.id.listed_tag_recycler_view);
        tag_recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        tag_recycler_view.setAdapter(tagListAdapter);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.tags_frame, tagsFragment = new TagsFragment()).commit();
        else
            tagsFragment = (TagsFragment) getSupportFragmentManager().getFragment(savedInstanceState, TagsFragment.TAG);
        tagsFragment.SetSpanCount(1);
        tagsFragment.SetCloseable(true);
        tagsFragment.SetData(selected_tags);
        tagsFragment.SetTagClosedListener((viewHolder, tag) -> tagListAdapter.CheckTag(tag, false));
    }

    private void ChooseTag(Tag tag){
        if (tagsFragment.GetAdapter().getItemCount() == Tag.MAX_TAGS){
            Toast.makeText(ChooseTagActivity.this, getString(R.string.tag_limit), Toast.LENGTH_SHORT).show();
        }else{
            if (tagsFragment.Contains(tag)){
                tagsFragment.Remove(tag);
            }else{
                tagsFragment.Add(tag, 0);
                tagsFragment.recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private void SubmitTag(){
        String tag_name = inputBox.getText().toString();
        if (tag_name.length() > 0){
            Tag tag = new Tag(tag_name);
            if (!tagsFragment.Contains(tag)){
                if (tagListAdapter.Contains(tag))
                    tagListAdapter.CheckTag(tag, true);
                tagsFragment.Add(tag, 0);
            }
            inputBox.setText("");
        }else{
            FinishActivity();
        }
    }

    private void FinishActivity(){
        Intent intent = new Intent();
        AList<Tag> tags = tagsFragment.GetData();
        intent.putExtra(SELECTED_TAGS, tags.ToArrayList());
        if (tags.Equals(selected_tags)) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, TagsFragment.TAG, tagsFragment);
    }
}