package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.Settings;
import personalprojects.seakyluo.randommenu.Models.Tag;

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
        fragment.SetData(Settings.settings.Favorites);
        fragment.SetShowLikeImage(false);
        fragment.SetFoodClickedListener((viewHolder, food) -> {
            FoodCardDialog dialog = new FoodCardDialog();
            dialog.SetFood(food);
            dialog.SetFoodEditedListener((before, after) -> {
                fragment.RemoveFood(before);
                dialog.SetFood(after);
                setTitle();
                setResult(RESULT_OK);
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        fragment.SetFoodRemovedListener((viewHolder, data) -> {
            int index = fragment.RemoveFood(data);
            Settings.settings.SetFavorite(data, false);
            setTitle();
            setResult(RESULT_OK);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mf_toolbar), String.format(getString(R.string.item_removed), data.Name), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), view -> {
                fragment.CancelRemoval();
                Settings.settings.SetFavorite(data, true);
                Settings.settings.Favorites.Move(0, index);
                setTitle();
            });
            snackbar.show();
        });
    }

    private void setTitle() { title.setText(Tag.Format(this, R.string.my_favorites, Settings.settings.Favorites.Count())); }
}
