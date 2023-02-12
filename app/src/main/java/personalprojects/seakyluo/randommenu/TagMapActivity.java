package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.TagMapAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.TagMapperDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.models.TagMapper;

public class TagMapActivity extends SwipeBackActivity {
    private boolean updated = false;
    private RecyclerView tagMapRecyclerView;
    private TagMapAdapter tagMapAdapter = new TagMapAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_map);

        tagMapRecyclerView = findViewById(R.id.tag_map_recycler_view);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.tm_fab).setOnClickListener(v -> showTagMapperDialog(null));
        findViewById(R.id.tm_toolbar).setOnClickListener(v -> tagMapRecyclerView.smoothScrollToPosition(0));

        tagMapAdapter.context = getApplicationContext();
        tagMapAdapter.setDataItemClickedListener((vh, data) -> showTagMapperDialog(data));
        tagMapAdapter.setMoreClickedListener((tm, more) -> {
            PopupMenuHelper helper = new PopupMenuHelper(R.menu.tag_mapper_menu, this, more);
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                switch (menuItem.getItemId()){
                    case R.id.duplicate_item:
                        showTagMapperDialog(new TagMapper("", tm.value));
                        return true;
                    case R.id.delete_item:
                        AskYesNoDialog dialog = new AskYesNoDialog();
                        dialog.setMessage(getString(R.string.ask_delete_keyword, tm.key));
                        dialog.setOnYesListener(v -> {
                            tagMapAdapter.remove(tm);
                            Settings.settings.AutoTagMap.remove(tm.key);
                        });
                        dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
                        return true;
                    default:
                        return false;
                }
            });
            helper.show();
        });
        tagMapAdapter.setData(mapToAList(Settings.settings.AutoTagMap));
        tagMapRecyclerView.setAdapter(tagMapAdapter);
    }

    public void showTagMapperDialog(TagMapper tagMapper){
        TagMapperDialog dialog = new TagMapperDialog();
        if (Objects.nonNull(tagMapper)){{
            dialog.setTagMapper(tagMapper);
        }}
        dialog.setConfirmListener(tm -> {
            int index = tagMapAdapter.data.indexOf(tm);
            if (tagMapper == null){
                if (index > 0){
                    List<Tag> tags = tagMapAdapter.data.get(index).value.stream().filter(i -> !tm.value.contains(i)).collect(Collectors.toList());
                    tm.value.addAll(tags);
                    if (tm.value.size() > 10){
                        Toast.makeText(this, R.string.duplicate_tag_mapper_with_tag_overflow, Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        tagMapAdapter.set(tm, index);
                        tagMapAdapter.move(index, 1);
                        Toast.makeText(this, R.string.duplicate_tag_mapper, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    tagMapAdapter.add(tm, 1);
                    Settings.settings.putTagMapper(tm);
                }
            }else{
                tagMapAdapter.set(tm, index);
                Settings.settings.putTagMapper(tm);
            }
            updated = true;
        });
        dialog.showNow(getSupportFragmentManager(), TagMapperDialog.TAG);
    }

    private static AList<TagMapper> mapToAList(LinkedHashMap<String, List<String>> map){
        AList<TagMapper> list = new AList<>();
        map.forEach((key, value) -> {
            TagMapper tagMapper = new TagMapper(key);
            tagMapper.setValue(value);
            list.with(tagMapper, 0);
        });
        list.add(0, null);
        return list;
    }

    @Override
    public void finish() {
        if (updated) Helper.save();
        updated = false;
        super.finish();
    }
}
