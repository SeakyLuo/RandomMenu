package personalprojects.seakyluo.randommenu.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Food;

public class SearchFoodListAdapter extends BaseFoodListAdapter {
    private boolean showTags, showNote;
    private String comma;
    private String keyword;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        comma = context.getResources().getString(R.string.comma);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food_search, parent, false));
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
            String note = data.Note;
            if (Helper.isNullOrEmpty(note)){
                view.findViewById(R.id.note_row).setVisibility(View.GONE);
            }else{
                if (showNote){
                    note_row.setVisibility(View.VISIBLE);
                }
                note_content.setText(note);
            }
            highlight(keyword);
        }

        public void highlight(String keyword){
            highlightText(food_name, keyword);
            if (showTags){
                highlightText(tag_content, keyword);
            }
            if (showNote){
                if (Helper.contains(data.Note, keyword) && !highlightText(note_content, keyword)){
                    // 备注可能因为太长不展示，先取段落，如果段落还是太长就直接取substring
                    reHighlightNoteContent();
                }
            }
        }

        private void reHighlightNoteContent(){
            for (String paragraph: data.Note.split("\n")){
                if (!paragraph.contains(keyword)){
                    continue;
                }
                note_content.setText(paragraph);
                if (!highlightText(note_content, keyword)){
                    note_content.setText(data.Note.substring(data.Note.indexOf(keyword)));
                    highlightText(note_content, keyword);
                }
                break;
            }
        }

        private boolean highlightText(TextView textView, String keyword){
            boolean isHighlighted = false;
            String content = textView.getText().toString();
            Spannable wordToSpan = new SpannableString(content);
            for (int start = 0, index; start < content.length(); start = index + 1) {
                index = content.indexOf(keyword, start);
                if (index == -1)
                    break;
                else {
                    isHighlighted = true;
                    wordToSpan.setSpan(new ForegroundColorSpan(Color.RED), index, index + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            textView.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            return isHighlighted;
        }
    }
}
