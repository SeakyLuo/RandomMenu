package personalprojects.seakyluo.randommenu.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.BaseEditSpinnerAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.EditSpinnerAdapter;
import personalprojects.seakyluo.randommenu.interfaces.EditSpinnerFilter;

public class EditSpinner extends RelativeLayout {
    private EditText editText;
    private ImageView mRightIv;
    private View mRightImageTopView;
    private Context mContext;
    private ListPopupWindow popupWindow;
    private BaseEditSpinnerAdapter adapter;
    private boolean isPopupWindowShowing;
    private Animation mAnimation;
    private Animation mResetAnimation;

    public EditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context, attrs);
        popupWindow = initPopupWindow(context);
        initAnimation();
    }

    public void setItemData(List<String> data) {
        setAdapter(new EditSpinnerAdapter(mContext, data));
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setTextColor(int color) {
        editText.setTextColor(color);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setHint(String hint) {
        editText.setHint(hint);
    }

    public void setTextSize(float size){
        editText.setTextSize(size);
    }

    public float getTextSize(){
        return editText.getTextSize();
    }

    public void setRightImageDrawable(Drawable drawable) {
        mRightIv.setImageDrawable(drawable);
    }

    public void setRightImageResource(int res) {
        mRightIv.setImageResource(res);
    }

    public void setAdapter(BaseEditSpinnerAdapter adapter) {
        this.adapter = adapter;
        popupWindow.setAdapter(adapter);
    }

    private void initAnimation() {
        mAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(300);
        mAnimation.setFillAfter(true);
        mResetAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResetAnimation.setDuration(300);
        mResetAnimation.setFillAfter(true);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditSpinner);
        LayoutInflater.from(context).inflate(R.layout.edit_spinner, this);
        editText = findViewById(R.id.edit_spinner_edit);
        mRightIv = findViewById(R.id.edit_spinner_expand);
        mRightIv.setOnClickListener(this::clickRightImage);

        mRightImageTopView = findViewById(R.id.edit_spinner_expand_above);
        mRightImageTopView.setOnClickListener(this::clickRightImage);
        mRightImageTopView.setClickable(false);
        editText.setOnClickListener(v -> popupWindow.show());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString().trim();
                editText.setSelection(key.length());
                showFilterData(key);
            }
        });
        float pixelSize = a.getDimensionPixelSize(R.styleable.EditSpinner_textSize, 0);
        float textSizeInSP = pixelSize == 0 ? 18 : pixelSize / getResources().getDisplayMetrics().scaledDensity;
        setTextSize(textSizeInSP);
        a.recycle();
    }

    private ListPopupWindow initPopupWindow(Context context) {
        ListPopupWindow popupWindow = new ListPopupWindow(context) {
            @Override
            public boolean isShowing() {
                return isPopupWindowShowing;
            }

            @Override
            public void show() {
                try {
                    super.show();
                    isPopupWindowShowing = true;
                    mRightImageTopView.setClickable(true);
                    mRightIv.startAnimation(mAnimation);
                } catch (Exception e){
                    // 没创建完view的时候setText会导致报错
                    Log.w("initPopupWindow", "show", e);
                }
            }
        };
        popupWindow.setOnItemClickListener(this::onPopupItemClick);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setPromptPosition(ListPopupWindow.POSITION_PROMPT_BELOW);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnchorView(editText);
        popupWindow.setOnDismissListener(() -> {
            isPopupWindowShowing = false;
            mRightIv.startAnimation(mResetAnimation);
        });
        return popupWindow;
    }


    private void clickRightImage(View v) {
        if (adapter == null || popupWindow == null) {
            return;
        }
        if (v.getId() == R.id.edit_spinner_expand_above) {
            v.setClickable(false);
            return;
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showFilterData("");
        }
    }

    private void onPopupItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(adapter.getItemString(position));
        mRightImageTopView.setClickable(false);
        popupWindow.dismiss();
    }

    private void showFilterData(String key) {
        if (popupWindow == null){
            return;
        }
        if (adapter == null) {
            popupWindow.dismiss();
            return;
        }
        EditSpinnerFilter filter = adapter.getEditSpinnerFilter();
        if (filter == null || filter.onFilter(key)){
            popupWindow.dismiss();
            return;
        }
        popupWindow.show();
    }

    // 关闭弹窗
    public void dismissPopWindow(){
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
}
