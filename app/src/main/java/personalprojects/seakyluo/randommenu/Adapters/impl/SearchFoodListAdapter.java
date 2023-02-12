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
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchFoodListAdapter extends BaseFoodListAdapter {
    private static final String dots = "...";

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
    protected void fillViewHolder(CustomAdapter.CustomViewHolder viewHolder, Food data, int position) {
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
        noteContent.setText(data.Note);
        if (StringUtils.isEmpty(data.Note)){
            noteRow.setVisibility(View.GONE);
        } else {
            if (showNote){
                noteRow.setVisibility(View.VISIBLE);
            }
        }

        foodName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                highlightText(foodName, keyword);
                if (showTags){
                    highlightText(tagContent, keyword);
                }
                if (showNote){
                    highlightText(noteContent, keyword);
                }
                foodName.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
    private static void highlightText(TextView textView, String keyword){
        int maxLines = textView.getMaxLines();
        adjustTextView(textView, keyword);
        String content = getVisibleText(textView);
        Spannable wordToSpan = new SpannableString(content);
        for (int start = 0, index; start < content.length(); start = index + 1) {
            index = content.indexOf(keyword, start);
            if (index == -1)
                break;
            else {
                wordToSpan.setSpan(new ForegroundColorSpan(Color.RED), index, index + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(wordToSpan, TextView.BufferType.SPANNABLE);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxLines(maxLines);
    }

    /**
     * 备注可能因为太长不展示，需要调整展示内容
     */
    private static void adjustTextView(TextView textView, String keyword){
        String text = textView.getText().toString();
        if (noNeedForFurtherAdjustment(textView, text, keyword, getVisibleText(textView), text.indexOf(keyword))){
            return;
        }
        // 找第一个带关键词的段落
        String[] paragraphs = text.split("\n");
        for (int i = 0; i < paragraphs.length; i++){
            String paragraph = paragraphs[i];
            if (!paragraph.contains(keyword)){
                continue;
            }
            if (i == 0){
                textView.setText(paragraph);
            }else{
                // 如果不是第一段，开头加上省略号
                textView.setText(dots + paragraph);
            }
            // 如果关键词还是看不到
            // 不停地重新计算可见范围（用while主要是针对else if）
            while (true){
                String visibleText = getVisibleText(textView);
                int index = paragraph.indexOf(keyword);
                int start, end;
                if (noNeedForFurtherAdjustment(textView, paragraph, keyword, visibleText, index)){
                    break;
                }
                start = Math.max(paragraph.length() - visibleText.length(), 0);
                String subString;
                // 如果在最后面的可见范围内
                if (start <= index + keyword.length()){
                    // 读取剩下的全部
                    end = paragraph.length();
                    // 如果关键词会被前面的省略号覆盖
                    if (index - start < dots.length()){
                        end -= dots.length();
                    }
                    // 如果关键词会被后面的省略号覆盖
                    else if (index + keyword.length() + dots.length() > visibleText.length()){
                        start += dots.length();
                    }
                    subString = paragraph.substring(start, end);
                }else{
                    // 可见范围取关键词在中间
                    start = Math.max(index - visibleText.length() / 2, 0);
                    end = Math.min(index + keyword.length() + visibleText.length() / 2, paragraph.length());
                    subString = paragraph.substring(start, end);
                }
                textView.setText(dots + subString);
            }
            break;
        }
    }

    private static boolean noNeedForFurtherAdjustment(TextView textView, String content, String keyword, String visibleText, int index){
        if (visibleText.endsWith(keyword)){
            // 如果可见文本以关键词结尾，可能会存在最后一个字看不到的情况
            // 所以如果可能的话往后挪一点，不能的话就算了
            int start = dots.length() + Math.min(keyword.length(), dots.length());
            int end = start + index;
            if (end <= content.length()){
                String subString = content.substring(start, end);
                textView.setText(dots + subString);
            }
            return true;
        }
        return visibleText.contains(keyword);
    }

    private static String getVisibleText(TextView textView) {
        String text = textView.getText().toString();
//        int maxLines = getMaxLines(textView);
        int maxLines = textView.getMaxLines();
        if (maxLines < 1 || maxLines > textView.getLineCount()){
            return text;
        }
        Layout layout = textView.getLayout();
        int end = layout.getLineEnd(maxLines - 1);
        // 默认了 TextUtils.TruncateAt.END
        int ellCount = layout.getEllipsisCount(maxLines - 1);
        return text.substring(0, end - ellCount);
    }

    private static int getMaxLines(TextView textView){
        int lineHeight = textView.getLineHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Rect bounds = new Rect();
            textView.getLineBounds(0, bounds);
            lineHeight = bounds.bottom - bounds.top;
        }
        if (lineHeight == 0) return 0;
        return (int) textView.getHeight() / lineHeight;
    }
}
