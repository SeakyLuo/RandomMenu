package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchActivity extends AppCompatActivity {
    private EditText search_bar;
    private ImageButton clear_button;
    private TabViewPager viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter tabPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        search_bar = findViewById(R.id.search_bar);
        clear_button = findViewById(R.id.clear_button);
        tabLayout = findViewById(R.id.search_tabs);
        viewPager = findViewById(R.id.search_viewpager);

        search_bar.requestFocus();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clear_button.setVisibility((s.toString().isEmpty()) ? View.GONE : View.VISIBLE);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
//        tabPagerAdapter.AddFragment(commentsFragment, "Comments");
//        tabPagerAdapter.AddFragment(ratingsFragment, "Ratings");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setSwipeable(true);
        viewPager.setAdapter(tabPagerAdapter);

        clear_button.setVisibility(View.GONE);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar.setText("");
                clear_button.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
