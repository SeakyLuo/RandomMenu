package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.TagMapperDialog;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.TagMapEntry;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;

public class TagMapperAdapter extends CustomAdapter<TagMapEntry> {

    public TagMapperAdapter(Context context){
        this.context = context;
    }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_tag_mapper;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, TagMapEntry data, int position) {
        View view = viewHolder.getView();
        TextView keyword = view.findViewById(R.id.keyword_content);
        ImageButton more = view.findViewById(R.id.more_button);
        RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);
        TagAdapter adapter = new TagAdapter();

        recyclerView.setAdapter(adapter);
        view.setOnClickListener(v -> showTagMapperDialog(data));
        more.setOnClickListener(v -> showMoreMenu(v, data));
        keyword.setText(data.getKeyword());
        adapter.setData(data.getTags());
    }

    private void showMoreMenu(View view, TagMapEntry tm){
        PopupMenuHelper helper = new PopupMenuHelper(R.menu.tag_mapper_menu, context, view);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.duplicate_item:
                    showTagMapperDialog(JsonUtils.copy(tm));
                    return true;
                case R.id.delete_item:
                    AskYesNoDialog dialog = new AskYesNoDialog();
                    dialog.setMessage(context.getString(R.string.ask_delete_keyword, tm.getKeyword()));
                    dialog.setYesListener(v -> {
                        remove(tm);
                        AutoTagMapperDaoService.delete(tm);
                    });
                    dialog.showNow(getContextAsFragmentActivity().getSupportFragmentManager(), AskYesNoDialog.TAG);
                    return true;
                default:
                    return false;
            }
        });
        helper.show();
    }

    public void showTagMapperDialog(TagMapEntry tagMapEntry){
        TagMapperDialog dialog = new TagMapperDialog();
        dialog.setTagMapEntry(tagMapEntry);
        dialog.setConfirmListener(tm -> {
            int index = indexOf(i -> i.getId() == tm.getId());
            if (index == -1){
                add(tm, 0);
            } else {
                set(tm, index);
            }
        });
        dialog.showNow(getContextAsFragmentActivity().getSupportFragmentManager(), TagMapperDialog.TAG);
    }
}
