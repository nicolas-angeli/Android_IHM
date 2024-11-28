package github.yvesbenabou.firebase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//Flo boutton etage
import android.view.MotionEvent;
import android.widget.Button;

//Nico synchro de la base de données avec ADE
import java.util.Calendar;
import java.util.Collection;

//Nico horloge
import android.app.TimePickerDialog;
import android.widget.TimePicker;

import java.util.HashMap;
import java.util.TimeZone;

import android.text.TextWatcher;

//Flo bouton couleurs
import android.widget.RelativeLayout;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
  private final String floors = "étages";
  private TextView selectedTimeTextView;
  private DoorButton db;
  private CancelButton cb;
  private ImageView takeroombubble;
  private final String reservation = "Réservations";
  public static DatabaseReference databaseRef;
  public static String url = "";

  String TAG = "MainActivity";

  //Flo boutton etage
  private ImageView backgroundImage;
  private int[] imageResources = {
          //R.drawable.school_map0,
          R.drawable.school_map1//,
          //R.drawable.school_map2,
          //R.drawable.school_map3,
          //R.drawable.school_map4
  };

  private int currentIndex = 1;
  private float initialY;

  // Flo bouton info
  private ImageView infoImage;

  // Flo boutons couleurs
  private RelativeLayout relativeLayout; // Le conteneur principal pour placer les boutons
  private HashMap<Integer, ArrayList<Button>> mapButtons; // Associe les maps avec leurs boutons


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    databaseRef = FirebaseDatabase.getInstance().getReference();
    databaseRef.child("ICalendarUrl").get().addOnCompleteListener(task -> {
      if (task.isSuccessful() && task.getResult().exists()) {
        MainActivity.this.url = task.getResult().getValue(String.class);
        Log.d("TAG", "URL récupérée : " + url);
        // Update et rafraichit les données de la base avec ADE si nécessaire
        new UpdateFetcher().execute();
      } else {
        Log.d("TAG", "La clé ICalendarUrl n'existe pas ou une erreur est survenue.");
      }
    });

    this.db = findViewById(R.id.doorbutton);
    this.db.setup((ImageView) findViewById(R.id.takeroombubble),
            findViewById(R.id.confirmroombutton),
            findViewById(R.id.cancelbutton),
            findViewById(R.id.modifybutton),
            findViewById(R.id.selected_time_textview),
            findViewById(R.id.salle_input));
    this.db.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // On Click
        // Write data to Firebase Database
        MainActivity.this.db.show();
        CalendarFetcher.hide_1();
      }
    });

    ConfirmRoomButton crb = findViewById(R.id.confirmroombutton);
    crb.setup(this.db);  // setup the confirm button so that it can call the hide function of the DoorButton when clicked
    crb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // On Click
        // Write data to Firebase Database
        String room = crb.getRoom();
        crb.take_room();
        MainActivity.databaseRef.child(reservation).child(floors).child(String.valueOf(room.charAt(1))).child(room).setValue(selectedTimeTextView.getText());
        crb.hide();
        CalendarFetcher.show_1();
      }
    });

    cb = findViewById(R.id.cancelbutton);
    cb.setup(db);  // setup the cancel button so that it can call the hide function of the DoorButton when clicked
    cb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // On Click
        cb.hide();
        CalendarFetcher.show_1();
      }
    });

    // Read data from Firebase Database
    databaseRef.child(floors).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        //txt.setText(dataSnapshot.getValue().toString());

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w(TAG, "onCancelled", databaseError.toException());
      }
    });


    //Flo boutton etage
    backgroundImage = findViewById(R.id.backgroundImage);
    Button slideButton = findViewById(R.id.slideButton);

    // Initialiser l'affichage du texte du bouton avec le numéro d'image
    slideButton.setText(String.valueOf(currentIndex));

    // Flo bouton couleur
    // Initialiser le layout principal
    relativeLayout = findViewById(R.id.relativeLayout);

    slideButton.setOnTouchListener(new View.OnTouchListener() {
      private float initialY;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            initialY = event.getY(); // Enregistre la position initiale du toucher
            return true;
          case MotionEvent.ACTION_UP:
            float finalY = event.getY(); // Position finale du toucher
            if (initialY - finalY > 100) {
              // Glissement vers le haut : image suivante
              currentIndex = (currentIndex + 1) % imageResources.length;
            } else if (finalY - initialY > 100) {
              // Glissement vers le bas : image précédente
              //currentIndex = (currentIndex + 1 + imageResources.length) % imageResources.length;
              currentIndex = (currentIndex + 1 ) % 5;

              switch (currentIndex) {
                case 0:
                  // Actions pour l'index 0
                  CalendarFetcher.hide_1();
                  CalendarFetcher.hide_3();
                  CalendarFetcher.hide_2();
                  CalendarFetcher.hide_4();
                  CalendarFetcher.show_0();
                  break;

                case 1:
                  // Actions pour l'index 1
                  CalendarFetcher.hide_0();
                  CalendarFetcher.hide_2();
                  CalendarFetcher.hide_3();
                  CalendarFetcher.hide_4();
                  CalendarFetcher.show_1();
                  break;

                case 2:
                  // Actions pour l'index 2
                  CalendarFetcher.hide_1();
                  CalendarFetcher.hide_3();
                  CalendarFetcher.hide_4();
                  CalendarFetcher.hide_0();
                  CalendarFetcher.show_2();
                  break;

                case 3:
                  // Actions pour l'index 3
                  CalendarFetcher.hide_2();
                  CalendarFetcher.hide_4();
                  CalendarFetcher.hide_1();
                  CalendarFetcher.hide_0();
                  CalendarFetcher.show_3();
                  break;

                case 4:
                  // Actions pour l'index 4
                  CalendarFetcher.hide_3();
                  CalendarFetcher.hide_1();
                  CalendarFetcher.hide_2();
                  CalendarFetcher.hide_0();
                  CalendarFetcher.show_4();
                  break;

                default:
                  break;
              }
            }
            // Mettre à jour l'image et le texte du bouton
            //backgroundImage.setImageResource(imageResources[currentIndex]);
            slideButton.setText(String.valueOf(currentIndex));
            //updateMap();
            return true;
        }
        return false;
      }
    });

    //Flo boutton info
    Button helpButton = findViewById(R.id.helpButton);
    infoImage = findViewById(R.id.infoImage);

    helpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Afficher l'image
        infoImage.setVisibility(View.VISIBLE);
        CalendarFetcher.hide_1();
      }
    });

    // Rooms par etage
    // Etage 1
    backgroundImage = findViewById(R.id.backgroundImage);
    backgroundImage.setImageResource(R.drawable.school_map1);

    CalendarFetcher.rooms.setSalle(new Salle("1101", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1101)));
    CalendarFetcher.rooms.setSalle(new Salle("1103", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1103)));
    CalendarFetcher.rooms.setSalle(new Salle("1105", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1105)));
    CalendarFetcher.rooms.setSalle(new Salle("1107", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1107)));

    CalendarFetcher.rooms.setSalle(new Salle("2101", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2101)));
    CalendarFetcher.rooms.setSalle(new Salle("2103", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2103)));
    CalendarFetcher.rooms.setSalle(new Salle("2105", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2105)));
    CalendarFetcher.rooms.setSalle(new Salle("2107", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2107)));

    CalendarFetcher.rooms.setSalle(new Salle("3101", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3101)));
    CalendarFetcher.rooms.setSalle(new Salle("3103", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3103)));
    CalendarFetcher.rooms.setSalle(new Salle("3105", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3105)));
    CalendarFetcher.rooms.setSalle(new Salle("3107", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3107)));
    CalendarFetcher.rooms.setSalle(new Salle("3109", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3109)));

    CalendarFetcher.rooms.setSalle(new Salle("4105", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4105)));
    CalendarFetcher.rooms.setSalle(new Salle("4109", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4109)));

    CalendarFetcher.rooms.setSalle(new Salle("5101", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5101)));
    CalendarFetcher.rooms.setSalle(new Salle("5103", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5103)));
    CalendarFetcher.rooms.setSalle(new Salle("5105", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5105)));
    CalendarFetcher.rooms.setSalle(new Salle("5107", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5107)));

    CalendarFetcher.rooms.setSalle(new Salle("0112", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0112)));
    CalendarFetcher.rooms.setSalle(new Salle("0110", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0110)));
    CalendarFetcher.rooms.setSalle(new Salle("0113", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0113)));
    CalendarFetcher.rooms.setSalle(new Salle("0114", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0114)));
    CalendarFetcher.rooms.setSalle(new Salle("0115", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0115)));
    CalendarFetcher.rooms.setSalle(new Salle("0160", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0160)));

    CalendarFetcher.rooms.setSalle(new Salle("0162", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0162)));
    CalendarFetcher.rooms.setSalle(new Salle("0163", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0163)));
    CalendarFetcher.rooms.setSalle(new Salle("0164", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0164)));
    CalendarFetcher.rooms.setSalle(new Salle("0165", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0165)));

    // Etage 2
    CalendarFetcher.rooms.setSalle(new Salle("1201", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1201)));
    CalendarFetcher.rooms.setSalle(new Salle("1203", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1203)));
    CalendarFetcher.rooms.setSalle(new Salle("1205", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1205)));
    CalendarFetcher.rooms.setSalle(new Salle("1207", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1207)));

    CalendarFetcher.rooms.setSalle(new Salle("2201", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2201)));
    CalendarFetcher.rooms.setSalle(new Salle("2203", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2203)));
    CalendarFetcher.rooms.setSalle(new Salle("2205", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2205)));
    CalendarFetcher.rooms.setSalle(new Salle("2207", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2207)));

    CalendarFetcher.rooms.setSalle(new Salle("3201", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3201)));
    CalendarFetcher.rooms.setSalle(new Salle("3203", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3203)));
    CalendarFetcher.rooms.setSalle(new Salle("3205", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3205)));
    CalendarFetcher.rooms.setSalle(new Salle("3207", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3207)));
    //CalendarFetcher.rooms.setSalle(new Salle("3209", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3209)));

    CalendarFetcher.rooms.setSalle(new Salle("4201", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4201)));
    //CalendarFetcher.rooms.setSalle(new Salle("4209", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4209)));

    CalendarFetcher.rooms.setSalle(new Salle("5201", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5201)));
    //CalendarFetcher.rooms.setSalle(new Salle("5203", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5203)));
    //CalendarFetcher.rooms.setSalle(new Salle("5205", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5205)));
    CalendarFetcher.rooms.setSalle(new Salle("5207", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5207)));

    CalendarFetcher.rooms.setSalle(new Salle("0210", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0210)));
    CalendarFetcher.rooms.setSalle(new Salle("0260", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s0260)));

    // Etage 3
    CalendarFetcher.rooms.setSalle(new Salle("1301", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1301)));
    //CalendarFetcher.rooms.setSalle(new Salle("1303", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1303)));
    CalendarFetcher.rooms.setSalle(new Salle("1305", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1305)));
    CalendarFetcher.rooms.setSalle(new Salle("1307", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1307)));

    //CalendarFetcher.rooms.setSalle(new Salle("2301", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2301)));
    //CalendarFetcher.rooms.setSalle(new Salle("2303", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2303)));
    CalendarFetcher.rooms.setSalle(new Salle("2305", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2305)));
    //CalendarFetcher.rooms.setSalle(new Salle("2307", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2307)));

    CalendarFetcher.rooms.setSalle(new Salle("3301", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3301)));
    //CalendarFetcher.rooms.setSalle(new Salle("3303", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3303)));
    CalendarFetcher.rooms.setSalle(new Salle("3305", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3305)));
    CalendarFetcher.rooms.setSalle(new Salle("3307", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3307)));
    //CalendarFetcher.rooms.setSalle(new Salle("3309", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3309)));

    CalendarFetcher.rooms.setSalle(new Salle("4305", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4305)));
    //CalendarFetcher.rooms.setSalle(new Salle("4309", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4309)));
    CalendarFetcher.rooms.setSalle(new Salle("5301", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5301)));
    //CalendarFetcher.rooms.setSalle(new Salle("5303", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5303)));
    //CalendarFetcher.rooms.setSalle(new Salle("5305", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5305)));
    //CalendarFetcher.rooms.setSalle(new Salle("5307", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5307)));

    // Etage 4
    CalendarFetcher.rooms.setSalle(new Salle("1401", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1401)));
    CalendarFetcher.rooms.setSalle(new Salle("1403", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1403)));
    CalendarFetcher.rooms.setSalle(new Salle("1405", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1405)));
    CalendarFetcher.rooms.setSalle(new Salle("1407", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1407)));

    CalendarFetcher.rooms.setSalle(new Salle("2401", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2401)));
    //CalendarFetcher.rooms.setSalle(new Salle("2403", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2403)));
    //CalendarFetcher.rooms.setSalle(new Salle("2405", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2405)));
    //CalendarFetcher.rooms.setSalle(new Salle("2407", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2407)));

    CalendarFetcher.rooms.setSalle(new Salle("3401", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3401)));
    //CalendarFetcher.rooms.setSalle(new Salle("3403", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3403)));
    //CalendarFetcher.rooms.setSalle(new Salle("3405", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3405)));
    CalendarFetcher.rooms.setSalle(new Salle("3407", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3407)));
    //CalendarFetcher.rooms.setSalle(new Salle("3409", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3409)));
    CalendarFetcher.rooms.setSalle(new Salle("4405", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4405)));
    CalendarFetcher.rooms.setSalle(new Salle("4401", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4401)));
    CalendarFetcher.rooms.setSalle(new Salle("5401", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5401)));
    CalendarFetcher.rooms.setSalle(new Salle("5403", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5403)));
    CalendarFetcher.rooms.setSalle(new Salle("5405", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5405)));
    CalendarFetcher.rooms.setSalle(new Salle("5407", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5407)));

    // Etage 0
    CalendarFetcher.rooms.setSalle(new Salle("1001", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1001)));
    CalendarFetcher.rooms.setSalle(new Salle("1003", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1003)));
    //CalendarFetcher.rooms.setSalle(new Salle("1005", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1005)));
    CalendarFetcher.rooms.setSalle(new Salle("1007", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s1007)));

    CalendarFetcher.rooms.setSalle(new Salle("2001", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2001)));
    //CalendarFetcher.rooms.setSalle(new Salle("2003", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2003)));
    //CalendarFetcher.rooms.setSalle(new Salle("2005", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2005)));
    //CalendarFetcher.rooms.setSalle(new Salle("2007", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s2007)));

    CalendarFetcher.rooms.setSalle(new Salle("3001", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3001)));
    //CalendarFetcher.rooms.setSalle(new Salle("3003", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3003)));
    //CalendarFetcher.rooms.setSalle(new Salle("3005", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3005)));
    CalendarFetcher.rooms.setSalle(new Salle("3007", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3007)));
    //CalendarFetcher.rooms.setSalle(new Salle("3009", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s3009)));
    //CalendarFetcher.rooms.setSalle(new Salle("4005", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4005)));
    CalendarFetcher.rooms.setSalle(new Salle("4007", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4007)));
    CalendarFetcher.rooms.setSalle(new Salle("4003", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4003)));
    //CalendarFetcher.rooms.setSalle(new Salle("4009", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s4009)));
    //CalendarFetcher.rooms.setSalle(new Salle("5001", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5001)));
    //CalendarFetcher.rooms.setSalle(new Salle("5003", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5003)));
    //CalendarFetcher.rooms.setSalle(new Salle("5005", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5005)));
    //CalendarFetcher.rooms.setSalle(new Salle("5007", github.yvesbenabou.firebase.Status.FREE, " ", findViewById(R.id.s5007)));


    ModifyButton modifyTimeButton = findViewById(R.id.modifybutton);
    selectedTimeTextView = findViewById(R.id.selected_time_textview);

    // Définir un listener pour ouvrir le TimePickerDialog
    modifyTimeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog();
      }
    });

    EditText salleInput = findViewById(R.id.salle_input);

    salleInput.addTextChangedListener(new TextWatcher() {
      private boolean isUpdating = false;

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Rien à faire ici
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isUpdating) return; // Empêche les boucles infinies

        // Supprime les espaces et vérifie si la chaîne est valide
        String salle = s.toString().trim().replace(" ", "");

        if (salle.isEmpty()) {
          Log.d("Firebase", "Chaîne vide, aucune vérification effectuée.");
          return; // Pas de vérification si l'entrée est vide
        }

        // Vérifie que la chaîne est suffisamment longue
        if (salle.length() < 3) {
          Log.d("Firebase", "Nom de salle trop court.");
          return; // La salle doit être au moins de 4 caractères (comme 1003)
        } else if (salle.length() == 3) salle = "0" + salle;

        // Extraire l'étage à partir du premier caractère
        String etage = String.valueOf(salle.charAt(1));
        String salleComplete = salle;

        // Référence Firebase
        DatabaseReference salleRef = databaseRef.child("étages").child(etage).child(salleComplete);

        // Ajout du Listener
        salleRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              // Salle trouvée
              Log.d("Firebase", "Salle " + salleComplete + " trouvée dans l'étage " + etage);
              MainActivity.this.db.salleFound(); // Appel d'une méthode personnalisée
              crb.setRoom(salleComplete);
            } else {
              // Salle non trouvée
              Log.d("Firebase", "Salle " + salleComplete + " introuvable dans l'étage " + etage);
              db.notFound(); // Appel d'une méthode personnalisée
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            Log.e("Firebase", "Erreur : " + databaseError.getMessage());
          }
        });
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Rien à faire ici
      }
    });

    ImageView backgroundImage = findViewById(R.id.backgroundImage);
    RelativeLayout background = findViewById(R.id.relativeLayout);
    takeroombubble = findViewById(R.id.takeroombubble);

    backgroundImage.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        verifyClick(event);
        return true;
      }
    });

    background.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        verifyClick(event);
        return true;
      }
    });

    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    Log.d(TAG, "Date actuelle : " + dateFormat.format(new Date()));
  }

  private void verifyClick(MotionEvent event) {
    boolean info = (infoImage.getVisibility() == View.VISIBLE);
    boolean reservation = (takeroombubble.getVisibility() == View.VISIBLE);
    if (info || reservation) {

      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        // Coordonnées du touch
        float touchX = event.getX();
        float touchY = event.getY();

        // Récupérer les dimensions et position de l'ImageView
        int[] infoCoords = new int[2];
        infoImage.getLocationInWindow(infoCoords);

        int imageLeft = infoCoords[0];
        int imageTop = infoCoords[1];
        int imageRight = imageLeft + infoImage.getWidth();
        int imageBottom = imageTop + infoImage.getHeight();

        // Vérifier si le touch est en dehors
        if (touchX < imageLeft || touchX > imageRight ||
                touchY < imageTop || touchY > imageBottom) {
          infoImage.setVisibility(View.GONE);
          if(takeroombubble.getVisibility() == View.GONE) CalendarFetcher.show_1();
        }

        takeroombubble.getLocationInWindow(infoCoords);

        imageLeft = infoCoords[0];
        imageTop = infoCoords[1];
        imageRight = imageLeft + takeroombubble.getWidth();
        imageBottom = imageTop + takeroombubble.getHeight();

        if (touchX < imageLeft || touchX > imageRight ||
                touchY < imageTop || touchY > imageBottom) {
          cb.hide();
          CalendarFetcher.show_1();
        }
      }
    }
  }

  public static void ADE_refresh() {
    CalendarFetcher.updateRoomStates();
  }

  private void showTimePickerDialog() {
    // Obtenez l'heure actuelle pour l'initialiser dans le TimePicker
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    // Créez et affichez le TimePickerDialog
    TimePickerDialog timePickerDialog = new TimePickerDialog(
            MainActivity.this,
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Récupérez l'heure sélectionnée et l'affichez
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                if(MainActivity.this.db.verify(selectedTime))  selectedTimeTextView.setText(selectedTime);
              }
            },
            hour, minute, true  // Le dernier paramètre `true` signifie un format 24h
    );
    timePickerDialog.show();
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

    public boolean containsSalle(String numSalle) {
      for (Salle salle : this.getList()) {
        if (salle.getNum().equals(numSalle)) return true;
      }
      return false;
    }

  }

  /*private void updateMap() {
    // Mettre à jour l'image de fond
    backgroundImage.setImageResource(imageResources[currentIndex]);

  }*/
  /*Pour push*/

}