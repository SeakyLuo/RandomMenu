package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.TagAdapter;
import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.database.services.FoodTagDaoService;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.models.TagMapEntry;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;

public class TagMapperDialog extends DialogFragment {
    public static final String TAG = "TagMapperDialog";
    @Setter
    private TagMapEntry tagMapEntry;
    private EditText keywordContent;
    private AutoCompleteTextView tagContent;
    private RecyclerView recyclerView;
    private TagAdapter adapter = new TagAdapter(true);
    @Setter
    private Consumer<TagMapEntry> confirmListener;
    private ArrayAdapter<String> suggestionTagListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_mapper, container,false);

        AppCompatButton confirm = view.findViewById(R.id.confirm_button);
        AppCompatButton cancel = view.findViewById(R.id.cancel_button);
        ImageButton add = view.findViewById(R.id.add_button);
        keywordContent = view.findViewById(R.id.keyword_content);
        tagContent = view.findViewById(R.id.tag_content);
        recyclerView = view.findViewById(R.id.tags_recycler_view);
        suggestionTagListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);

        tagContent.setAdapter(suggestionTagListAdapter);
        tagContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag = suggestionTagListAdapter.getItem(position);
                tagContent.setText(tag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tagContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                suggestionTagListAdapter.clear();
                String keyword = s.toString().trim();
                suggestionTagListAdapter.addAll(SearchUtils.searchTags(FoodTagDaoService.selectAll(), keyword));
                suggestionTagListAdapter.notifyDataSetChanged();
            }
        });
        tagContent.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submitTag();
                return true;
            }
            return false;
        });
        setData(tagMapEntry);
        confirm.setOnClickListener(v -> onConfirm());
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> submitTag());

        recyclerView.setAdapter(adapter);
        return view;
    }

    private void setData(TagMapEntry tagMapEntry){
        if (tagMapEntry == null){
            return;
        }
        keywordContent.setText(tagMapEntry.getKeyword());
        adapter.setData(tagMapEntry.getTags());
    }

    private void onConfirm(){
        String keyword = keywordContent.getText().toString().trim();
        if (StringUtils.isEmpty(keyword)){
            Toast.makeText(getContext(), R.string.empty_keyword, Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.isEmpty()){
            Toast.makeText(getContext(), R.string.keyword_at_least_one_tag, Toast.LENGTH_SHORT).show();
            return;
        }
        TagMapEntry tagMapEntry = buildTagMapper();
        if (tagMapEntry.getId() == 0){
            TagMapEntry existing = AutoTagMapperDaoService.selectByKeyword(tagMapEntry.getKeyword());
            if (existing == null){
                AutoTagMapperDaoService.insert(tagMapEntry);
            } else {
                existing.getTags().addAll(adapter.getData());
                if (existing.getTags().size() > 10){
                    Toast.makeText(getContext(), R.string.duplicate_tag_mapper_with_tag_overflow, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    tagMapEntry = existing;
                    Toast.makeText(getContext(), R.string.duplicate_tag_mapper, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            AutoTagMapperDaoService.update(tagMapEntry);
        }
        if (confirmListener != null) confirmListener.accept(tagMapEntry);
        dismiss();
    }

    private TagMapEntry buildTagMapper(){
        TagMapEntry mapper = tagMapEntry == null ? new TagMapEntry() : JsonUtils.copy(tagMapEntry);
        mapper.setKeyword(keywordContent.getText().toString().trim());
        mapper.setTags(adapter.getData());
        return mapper;
    }

    public void submitTag(){
        if (adapter.getItemCount() >= 10){
            Toast.makeText(getContext(), R.string.tag_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        String tagName = tagContent.getText().toString().trim();
        if (StringUtils.isEmpty(tagName)){
            return;
        }
        int index = adapter.indexOf(t -> t.getName().equals(tagName));
        if (index == -1){
            adapter.add(new Tag(tagName), 0);
        } else {
            adapter.move(index, 0);
        }
        tagContent.setText("");
    }
}
