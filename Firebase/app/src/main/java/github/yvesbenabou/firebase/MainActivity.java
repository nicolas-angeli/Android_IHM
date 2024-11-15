package github.yvesbenabou.firebase;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;

import github.yvesbenabou.firebase.libs.*;

public class MainActivity extends AppCompatActivity {
    TextView txt;

    String TAG = "MainActivity";
    String fbKey="salles";
    Integer count;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = 0;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt = (TextView) findViewById(R.id.text);

    /*
	the result depends on your android configuration:
	this works up to android 33 and firebase database 19.2.1 (see ../dependencies.gradle)
    */
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();  // and if you explicit the project name with full URL, you get an exception with forbidden characters!
    /* 
	some students had issues with the syntax above, they had to explicit the name of the project again
    */
        //final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("https://projet1-38f7d.firebaseio.com"); // explicitly adding the project url avoids hoping for project to be implicitly hosted on server us-central1
        rootRef.child(fbKey).child("1007").setValue(0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // On Click
                // Write data to Firebase Database
                rootRef.child(fbKey).setValue("Yes! We're sending message " + count.toString() +" from App to Firebase.");
                count++;
            }
        });

        // Read data from Firebase Database
        rootRef.child(fbKey).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG,
                        dataSnapshot.getValue().toString());  //reads back data with key fbKey
                txt.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    public static class Liste_Salles {

        HashMap<String, Salle> List;

        public Liste_Salles() {
            List = new HashMap<>();
        }

        public Collection<Salle> getList() {
            return List.values();
        }

        public Salle getSalle(String num) {
            return List.get(num);
        }
        public void setSalle(Salle newSalle) {
            List.put(newSalle.getNum(), newSalle);
        }

        public Collection<String> getSallesSet() {
            return List.keySet();
        }

}
