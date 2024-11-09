package github.yvesbenabou.firebase;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements Database_Out {
  private final String floors = "étages";
  TextView txt;
  String TAG = "MainActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    //txt = (TextView) findViewById(R.id.text);

    DoorButton db = findViewById(R.id.doorbutton);
    db.setup((ImageView)findViewById(R.id.takeroombubble),
            findViewById(R.id.confirmroombutton),
            findViewById(R.id.cancelbutton),
            findViewById(R.id.modifybutton));
    db.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // On Click
        // Write data to Firebase Database
        db.show();
      }
    });

    ConfirmRoomButton crb = findViewById(R.id.confirmroombutton);
    crb.setup(db);  // setup the confirm button so that it can call the hide function of the DoorButton when clicked
    crb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // On Click
        // Write data to Firebase Database
        crb.take_room("2205");
        crb.setColorNormal(crb.red);
        crb.setColorRipple(crb.red);
        crb.hide();
      }
    });

    CancelButton cb = findViewById(R.id.cancelbutton);
    cb.setup(db);  // setup the cancel button so that it can call the hide function of the DoorButton when clicked
    cb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // On Click
        cb.hide();
      }
    });

    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();  // and if you explicit the project name with full URL, you get an exception with forbidden characters!

    // Read data from Firebase Database
    databaseRef.child(floors).addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        //txt.setText(dataSnapshot.getValue().toString());
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w(TAG, "onCancelled", databaseError.toException());
      }
    });

  }

  @Override
  public void update() {
    //cette fonction sera appelée toutes les 15 mins pour resynchroniser avec ADE

  }
}
