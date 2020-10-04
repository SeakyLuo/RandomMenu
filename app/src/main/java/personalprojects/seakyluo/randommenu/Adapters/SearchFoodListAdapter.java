package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Food;

public class SearchFoodListAdapter extends BaseFoodListAdapter {
    private static final String dots = "...";

    private boolean showTags, showNote;
    private String comma;
    private String keyword;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food_search, parent, false));
    }

    @Override
    public void setContext(Context context){
        super.setContext(context);
        comma = context.getResources().getString(R.string.comma);
    }

    public void setShowTags(boolean showTags) {
        this.showTags = showTags;
    }

    public void setShowNote(boolean showNote) {
        this.showNote = showNote;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView tag_content, note_content;
        private LinearLayout note_row;

        public ViewHolder(View view) {
            super(view);
            tag_content = view.findViewById(R.id.tag_content);
            note_content = view.findViewById(R.id.note_content);
            note_row = view.findViewById(R.id.note_row);

            if (!showTags){
                view.findViewById(R.id.tag_row).setVisibility(View.GONE);
            }
            if (!showNote){
                note_row.setVisibility(View.GONE);
            }
        }

        @Override
        public void setData(Food data) {
            super.setData(data);
            String tags = String.join(comma, data.getTags().convert(tag -> tag.Name).toArrayList());
            tag_content.setText(tags);
            note_content.setText(data.Note);

            if (Helper.isNullOrEmpty(data.Note)){
                note_row.setVisibility(View.GONE);
            }else{
                if (showNote){
                    note_row.setVisibility(View.VISIBLE);
                }
            }
            afterDrew(food_name);
        }

        public void highlight(){
            highlightText(food_name, keyword);
            if (showTags){
                highlightText(tag_content, keyword);
            }
            if (showNote){
                highlightText(note_content, keyword);
            }
        }

        private void afterDrew(TextView textView){
            ViewTreeObserver observer = textView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    highlight();
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
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
