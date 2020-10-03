package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardDialog;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.Tag;

public class MyFavoritesActivity extends SwipeBackActivity {
    public static final int REQUEST_CODE = 10;
    private TextView title;
    private FoodListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        title = findViewById(R.id.title_text_view);
        setTitle();

        fragment = (FoodListFragment) getSupportFragmentManager().findFragmentById(R.id.food_list_fragment);
        fragment.setData(Settings.settings.GetFavoriteFoods());
        fragment.setShowLikeImage(false);
        fragment.setFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.setFood(food);
            dialog.setFoodEditedListener((before, after) -> {
                fragment.removeFood(before);
                dialog.setFood(after);
                setTitle();
                setResult(RESULT_OK);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        fragment.setFoodRemovedListener((viewHolder, data) -> {
            int index = fragment.removeFood(data);
            Settings.settings.SetFavorite(data, false);
            setTitle();
            setResult(RESULT_OK);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mf_toolbar), String.format(getString(R.string.item_removed), data.Name), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), view -> {
                fragment.cancelRemoval();
                Settings.settings.SetFavorite(data, true);
                Settings.settings.MyFavorites.move(0, index);
                setTitle();
            });
            snackbar.show();
        });
    }

    private void setTitle() { title.setText(Tag.Format(this, R.string.my_favorites, Settings.settings.MyFavorites.count())); }
}
