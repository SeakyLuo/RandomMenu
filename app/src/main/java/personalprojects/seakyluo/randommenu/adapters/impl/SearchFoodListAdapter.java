package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.BaseFoodListAdapter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.utils.TextViewUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchFoodListAdapter extends BaseFoodListAdapter {

    private boolean showTags, showNote;
    private String comma;
    private String keyword;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_food_search;
    }

    @Override
    public void setContext(Context context){
        super.setContext(context);
        comma = context.getResources().getString(R.string.comma);
    }

    @Override
    protected void fillViewHolder(CustomAdapter.CustomViewHolder viewHolder, SelfMadeFood data, int position) {
        super.fillViewHolder(viewHolder, data, position);
        View view = viewHolder.getView();

        TextView tagContent = view.findViewById(R.id.tag_content);
        TextView noteContent = view.findViewById(R.id.note_content);
        LinearLayout noteRow = view.findViewById(R.id.note_row);
        TextView foodName = view.findViewById(R.id.food_name);
        View tagRow = view.findViewById(R.id.tag_row);

        if (!showTags){
            tagRow.setVisibility(View.GONE);
        }
        if (!showNote){
            noteRow.setVisibility(View.GONE);
        }
        String tags = data.getTags().stream().map(Tag::getName).collect(Collectors.joining(comma));
        tagContent.setText(tags);
        String note = data.getNote();
        noteContent.setText(note);
        if (StringUtils.isEmpty(note)){
            noteRow.setVisibility(View.GONE);
        } else {
            if (showNote){
                noteRow.setVisibility(View.VISIBLE);
            }
        }

        TextViewUtils.highlightTextView(foodName, keyword);
        TextViewUtils.highlightTextView(tagContent, keyword);
        TextViewUtils.highlightTextView(noteContent, keyword);
    }

}
