package personalprojects.seakyluo.randommenu.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
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

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.TagAdapter;
import personalprojects.seakyluo.randommenu.utils.SearchUtils;
import personalprojects.seakyluo.randommenu.interfaces.DataOperationListener;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.TagMapper;

public class TagMapperDialog extends DialogFragment {
    public static final String TAG = "TagMapperDialog";
    private TagMapper tagMapper;
    private EditText keyword_content;
    private AutoCompleteTextView tag_content;
    private RecyclerView recyclerView;
    private ImageButton add;
    private AppCompatButton confirm, cancel;
    private TagAdapter adapter = new TagAdapter(true);
    private DataOperationListener<TagMapper> confirmListener;
    private ArrayAdapter<String> suggestionTagListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tag_mapper, container,false);

        confirm = view.findViewById(R.id.confirm_button);
        cancel = view.findViewById(R.id.cancel_button);
        add = view.findViewById(R.id.add_button);
        keyword_content = view.findViewById(R.id.keyword_content);
        tag_content = view.findViewById(R.id.tag_content);
        recyclerView = view.findViewById(R.id.tags_recycler_view);
        suggestionTagListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line);

        tag_content.setAdapter(suggestionTagListAdapter);
        tag_content.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag = suggestionTagListAdapter.getItem(position);
                tag_content.setText(tag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tag_content.addTextChangedListener(new TextWatcher() {
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
                suggestionTagListAdapter.addAll(SearchUtils.searchTags(Settings.settings.Tags.stream(), keyword));
                suggestionTagListAdapter.notifyDataSetChanged();
            }
        });
        tag_content.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submitTag();
                return true;
            }
            return false;
        });
        confirm.setOnClickListener(v -> {
            String keyword = keyword_content.getText().toString().trim();
            if (StringUtils.isEmpty(keyword)){
                Toast.makeText(getContext(), R.string.empty_keyword, Toast.LENGTH_SHORT).show();
                return;
            }
            if (adapter.getData().size() == 0){
                Toast.makeText(getContext(), R.string.keyword_at_least_one_tag, Toast.LENGTH_SHORT).show();
                return;
            }
            if (confirmListener != null) confirmListener.operate(new TagMapper(keyword, adapter.getData()));
            dismiss();
        });
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> submitTag());
        if (Objects.nonNull(tagMapper)){
            keyword_content.setText(tagMapper.key);
            adapter.setData(tagMapper.value);
        }
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setConfirmListener(DataOperationListener<TagMapper> listener) { confirmListener = listener; }
    public void setTagMapper(TagMapper tagMapper){
        this.tagMapper = tagMapper;
    }
    public void submitTag(){
        if (tagMapper != null && tagMapper.value != null && tagMapper.value.size() > 10){
            Toast.makeText(getContext(), R.string.tag_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        String tag = tag_content.getText().toString().trim();
        if (StringUtils.isEmpty(tag)){
            return;
        }
        int index = adapter.indexOf(t -> t.Name.equals(tag));
        if (index == -1){
            adapter.add(tag, 0);
        }else{
            adapter.move(index, 0);
        }
        tag_content.setText("");
    }
}
