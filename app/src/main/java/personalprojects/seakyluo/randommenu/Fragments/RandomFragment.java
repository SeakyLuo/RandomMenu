package personalprojects.seakyluo.randommenu.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import personalprojects.seakyluo.randommenu.dialogs.FilterDialog;
import personalprojects.seakyluo.randommenu.dialogs.MenuDialog;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment", TAG_MENU = "menu", TAG_PREFERRED_TAGS = "preferred_tags", TAG_EXCLUDED_TAGS = "excluded_tags";
    private AList<Food> food_pool = new AList<>();
    private FoodCardFragment foodCardFragment;
    private AList<Food> menu = new AList<>();
    private MenuDialog menuDialog = new MenuDialog();
    private FilterDialog filterDialog = new FilterDialog();
    private AList<Tag> preferred_tags = new AList<>(), excluded_tags = new AList<>();
    private View food_card;
    private Animation good_food, bad_food;
    private Animator flip_in, flip_out;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        food_card = view.findViewById(R.id.food_card_view);
        if (savedInstanceState == null)
            getChildFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        else{
            foodCardFragment = (FoodCardFragment) getChildFragmentManager().getFragment(savedInstanceState, FoodCardFragment.TAG);
            menu.copyFrom(savedInstanceState.getParcelableArrayList(TAG_MENU));
            preferred_tags.copyFrom(savedInstanceState.getParcelableArrayList(TAG_PREFERRED_TAGS));
            excluded_tags.copyFrom(savedInstanceState.getParcelableArrayList(TAG_EXCLUDED_TAGS));
        }
        SetAnimations();
        view.findViewById(R.id.check_button).setOnClickListener(v -> GoodFood());
        view.findViewById(R.id.cross_button).setOnClickListener(v -> BadFood());
        view.findViewById(R.id.refresh_button).setOnClickListener(v -> ResetFood());
        filterDialog.SetTagFilterListener((preferred, excluded) -> {
            preferred_tags.copyFrom(preferred);
            excluded_tags.copyFrom(excluded);
            filterDialog.dismiss();
            Reset();
            foodCardFragment.SetFood(NextFood());
        });
        filterDialog.SetOnResetListener(v -> {
            filterDialog.SetData(preferred_tags.clear(), excluded_tags.clear());
            Reset();
            foodCardFragment.SetFood(NextFood());
        });
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            filterDialog.SetData(preferred_tags, excluded_tags);
            filterDialog.showNow(getChildFragmentManager(), FilterDialog.TAG);
        });
        ImageButton menuButton = view.findViewById(R.id.menu_button);
        menuDialog.SetFoodAddedListener((viewHolder, data) -> {
            food_pool.removeAll(data);
            menu.copyFrom(data);
            SetMenuHeader();
            if (menu.contains(foodCardFragment.GetFood())) NextFood();
        });
        menuDialog.SetFoodRemovedListener((viewHolder, data) -> {
            menu.remove(data);
            SetMenuHeader();
            food_pool.add(data, Helper.RandRange(0, food_pool.count()));
        });
        menuDialog.SetOnClearListener(button -> {
            food_pool.addAll(menu).shuffle();
            menu.clear();
            menuDialog.SetHeaderText(String.format(getString(R.string.food_count), 0));
            menuDialog.Clear();
        });
        menuButton.setOnClickListener(v -> {
            menuDialog.SetHeaderText(String.format(getString(R.string.food_count), menu.count()));
            menuDialog.SetData(menu);
            menuDialog.showNow(getChildFragmentManager(), MenuDialog.TAG);
        });
        Reset();
        if (!food_pool.isEmpty()) foodCardFragment.loadFood(food_pool.pop(0));
        return view;
    }

    private void SetMenuHeader() { menuDialog.SetHeaderText(String.format(getString(R.string.food_count), menu.count())); }
    private void GoodFood() { food_card.startAnimation(good_food); }
    private void BadFood() { food_card.startAnimation(bad_food); }
    private void ResetFood() { flip_in.setTarget(food_card); flip_in.start(); }

    private Food NextFood(){
        if (food_pool.isEmpty()){
            Reset();
            Toast.makeText(getContext(), getString(R.string.reshuffle), Toast.LENGTH_SHORT).show();
        }
        return food_pool.isEmpty() ? null : foodCardFragment.SetFood(food_pool.pop(0));
    }

    private AList<Food> Reset(){
        AList<Food> source = Settings.settings.Foods.forEach(f -> f.HideCount = Math.max(f.HideCount - 1, 0)).find(f -> !menu.contains(f) && f.HideCount == 0);
        if (!preferred_tags.isEmpty()) source.remove(f -> !preferred_tags.any(f::HasTag));
        if (!excluded_tags.isEmpty()) source.remove(f -> excluded_tags.any(f::HasTag));
        do source.shuffle();
        while (!food_pool.isEmpty() && food_pool.get(0).equals(source.get(0)));
        return food_pool.copyFrom(source);
    }

    public void Refresh() { foodCardFragment.Refresh(); }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getChildFragmentManager().putFragment(outState, FoodCardFragment.TAG, foodCardFragment);
        outState.putParcelableArrayList(TAG_MENU, menu.toArrayList());
        outState.putParcelableArrayList(TAG_PREFERRED_TAGS, preferred_tags.toArrayList());
        outState.putParcelableArrayList(TAG_EXCLUDED_TAGS, excluded_tags.toArrayList());
    }

    private void SetAnimations(){
        good_food = AnimationUtils.loadAnimation(getContext(), R.anim.good_food);
        good_food.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menu.add(foodCardFragment.GetFood(), 0);
                NextFood();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bad_food = AnimationUtils.loadAnimation(getContext(), R.anim.bad_food);
        bad_food.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                NextFood();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        food_card.setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        flip_in = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_in);
        flip_in.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Reset();
                NextFood();
                flip_out.setTarget(food_card);
                flip_out.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        flip_out = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_out);
        flip_out.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(getContext(), getString(R.string.food_pool_reset), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int x_threshold = 100, y_threshold = 100;
                float diffX = e1.getX() - e2.getX(), diffY = e1.getY() - e2.getY(), distX = Math.abs(diffX), distY = Math.abs(diffY);
                if (distX > distY && distX > x_threshold){
                    if (diffX > 0) {
                        // Left swipe
                    }else{
                        // Right swipe
                        ResetFood();
                    }
                }else if (distY > distX && distY > y_threshold){
                    if (diffY > 0) {
                        // up swipe
                        GoodFood();
                    }else{
                        // down swipe
                        BadFood();
                    }
                }else{
                    return false;
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        food_card.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
    }
}