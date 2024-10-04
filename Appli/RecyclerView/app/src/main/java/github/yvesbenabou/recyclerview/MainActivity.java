package github.yvesbenabou.recyclerview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<String> data;
    RVAdapter rvAdapter;
    final String TAG = "MainActivity";

    void initData() {
        data = new ArrayList<>();
        data.add("Apple");
        data.add("Banana");
        data.add("Peach");
        data.add("Pineapple");
        data.add("Orange");
        data.add("Strawberry");
        data.add("Grapes");
        data.add("Apricot");
        data.add("Avocado");
        data.add("Raisin");
        data.add("Guava");
        data.add("Papaya");
        data.add("Pear");
        data.add("Blueberry");
        data.add("Lychee");
        data.add("Date");
        data.add("Fig");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prepare list data
        initData();

        //create RV adapter from data (fruits strings)
        rvAdapter = new RVAdapter(data);

        recyclerView = (RecyclerView) findViewById(R.id.rv_fruits);

        // set adapter to RV
        recyclerView.setAdapter(rvAdapter);

        // set RV layout: vertical list
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // RV size doesn't depend on amount of content
        recyclerView.hasFixedSize();


        // Set the RV item divider decoration.
        // OptionalRVDividerItemDecoration works exactly the same as std DividerItemDecoration
        recyclerView.addItemDecoration(
                new /*OptionalRV*/DividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                )
        );

        // Set RV touch listeners when touching one of its items
        recyclerView.addOnItemTouchListener(
                new RVItemTouchListener(
                        this,
                        new RVItemTouchListener.ItemTouchListener() {

                            @Override
                            public void onItemTouch(View view, int position) { //define custom code when touching an item
                                String value = "Clicked Item " + data.get(position) + " at " + position;
                                Log.d(TAG, value);
                                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                            }
                        }
                )
        );

        // Finally configure RV to handle advanced gestures (Swiping left right / dragging/ swapping) of the items
        // ie use onMoved or onSwiped callbacks from RVItemTouchHelper
        // and implement related code so it can handle the consequences on the rvAdapter:
        // TouchHelper::onSwiped(): removing adapter items,
        // TouchHelper::onMoved(): reordering adapter items
        ItemTouchHelper.Callback touchHelperCallback = new RVItemTouchHelperCallback(rvAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerView);

    }
}
