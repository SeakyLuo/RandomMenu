package personalprojects.seakyluo.randommenu.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.lang3.RandomUtils;

import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.FilterDialog;
import personalprojects.seakyluo.randommenu.dialogs.MenuDialog;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.SelfFood;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.services.SelfFoodService;

public class RandomFragment extends Fragment {
    public static final String TAG = "RandomFragment", TAG_MENU = "menu", TAG_PREFERRED_TAGS = "preferred_tags", TAG_EXCLUDED_TAGS = "excluded_tags";
    private AList<SelfFood> foodPool = new AList<>();
    private FoodCardFragment foodCardFragment;
    private AList<SelfFood> menu = new AList<>();
    private MenuDialog menuDialog = new MenuDialog();
    private FilterDialog filterDialog = new FilterDialog();
    private AList<Tag> preferredTags = new AList<>(), excludedTags = new AList<>();
    private View foodCard;
    private Animation goodFoodAnim, badFoodAnim;
    private Animator flipInAnim, flip_out;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        foodCard = view.findViewById(R.id.food_card_view);
        if (savedInstanceState == null)
            getChildFragmentManager().beginTransaction().add(R.id.food_card_frame, foodCardFragment = new FoodCardFragment()).commit();
        else{
            foodCardFragment = (FoodCardFragment) getChildFragmentManager().getFragment(savedInstanceState, FoodCardFragment.TAG);
            menu.copyFrom(savedInstanceState.getParcelableArrayList(TAG_MENU));
            preferredTags.copyFrom(savedInstanceState.getParcelableArrayList(TAG_PREFERRED_TAGS));
            excludedTags.copyFrom(savedInstanceState.getParcelableArrayList(TAG_EXCLUDED_TAGS));
        }
        setAnimations();
        view.findViewById(R.id.check_button).setOnClickListener(v -> showGoodFoodAnim());
        view.findViewById(R.id.cross_button).setOnClickListener(v -> showBadFoodAnim());
        view.findViewById(R.id.refresh_button).setOnClickListener(v -> resetFood());
        filterDialog.setTagFilterListener((preferred, excluded) -> {
            preferredTags.copyFrom(preferred);
            excludedTags.copyFrom(excluded);
            filterDialog.dismiss();
            reset();
            nextFood();
        });
        filterDialog.setOnResetListener(v -> {
            filterDialog.setData(preferredTags.copy(), excludedTags.copy());
            preferredTags.clear();
            excludedTags.clear();
            reset();
            nextFood();
        });
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            filterDialog.setData(preferredTags, excludedTags);
            filterDialog.showNow(getChildFragmentManager(), FilterDialog.TAG);
        });
        ImageButton menuButton = view.findViewById(R.id.menu_button);
        menuDialog.setFoodAddedListener(data -> {
            foodPool.removeAll(data);
            menu.copyFrom(data);
            setMenuHeader();
            if (menu.contains(foodCardFragment.getFood())) nextFood();
        });
        menuDialog.setFoodRemovedListener(data -> {
            menu.remove(data);
            setMenuHeader();
            foodPool.with(data, RandomUtils.nextInt(0, foodPool.size()));
        });
        menuDialog.setClearListener(button -> {
            foodPool.with(menu).shuffle();
            menu.clear();
            menuDialog.setHeaderText(String.format(getString(R.string.food_count), 0));
            menuDialog.clear();
        });
        menuButton.setOnClickListener(v -> {
            menuDialog.setHeaderText(String.format(getString(R.string.food_count), menu.size()));
            menuDialog.setData(menu);
            menuDialog.showNow(getChildFragmentManager(), MenuDialog.TAG);
        });
        setData();
        nextFood();
        return view;
    }

    private void setMenuHeader() {
        menuDialog.setHeaderText(String.format(getString(R.string.food_count), menu.size()));
    }
    private void showGoodFoodAnim() {
        foodCard.startAnimation(goodFoodAnim);
    }
    private void showBadFoodAnim() {
        foodCard.startAnimation(badFoodAnim);
    }
    private void resetFood() {
        flipInAnim.setTarget(foodCard);
        flipInAnim.start();
    }

    private SelfFood nextFood(){
        if (foodPool.isEmpty()){
            if (foodCardFragment.getFood() != null){
                reset();
                Toast.makeText(getContext(), getString(R.string.reshuffle), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        SelfFood food = SelfFoodService.selectById(foodPool.pop(0).getId());
        if (food == null) return nextFood();
        return foodCardFragment.fillFood(food);
    }

    private AList<SelfFood> reset(){
        SelfFoodDaoService.decrementHideCount();
        return setData();
    }

    private AList<SelfFood> setData(){
        AList<SelfFood> source = SelfFoodDaoService.selectNonHidden().stream()
                .collect(Collectors.toCollection(AList::new));
        if (!preferredTags.isEmpty()) source.removeIf(f -> !preferredTags.any(f::hasTag));
        if (!excludedTags.isEmpty()) source.removeIf(f -> excludedTags.any(f::hasTag));
        do source.shuffle();
        while (!foodPool.isEmpty() && foodPool.get(0).equals(source.get(0)));
        return foodPool.copyFrom(source);
    }

    public void refresh() { foodCardFragment.refresh(); }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getChildFragmentManager().putFragment(outState, FoodCardFragment.TAG, foodCardFragment);
        outState.putParcelableArrayList(TAG_MENU, menu);
        outState.putParcelableArrayList(TAG_PREFERRED_TAGS, preferredTags);
        outState.putParcelableArrayList(TAG_EXCLUDED_TAGS, excludedTags);
    }

    private void setAnimations(){
        goodFoodAnim = AnimationUtils.loadAnimation(getContext(), R.anim.good_food);
        goodFoodAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menu.with(foodCardFragment.getFood(), 0);
                nextFood();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        badFoodAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bad_food);
        badFoodAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextFood();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        foodCard.setCameraDistance(getResources().getDisplayMetrics().density * 8000);
        flipInAnim = AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_in);
        flipInAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                reset();
                nextFood();
                flip_out.setTarget(foodCard);
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
                    } else {
                        // Right swipe
                        resetFood();
                    }
                } else if (distY > distX && distY > y_threshold){
                    if (diffY > 0) {
                        // up swipe
                        showGoodFoodAnim();
                    } else {
                        // down swipe
                        showBadFoodAnim();
                    }
                } else {
                    return false;
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
        foodCard.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
    }
}