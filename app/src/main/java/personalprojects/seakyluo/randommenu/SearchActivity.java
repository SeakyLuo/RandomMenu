package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.AutoCompleteTextView;

public class SearchActivity extends AppCompatActivity {
    private AutoCompleteTextView search_bar;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        search_bar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.search_result);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }
}
