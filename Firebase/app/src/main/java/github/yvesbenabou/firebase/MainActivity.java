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
          R.drawable.school_map0,
          R.drawable.school_map1,
          R.drawable.school_map2,
          R.drawable.school_map3,
          R.drawable.school_map4
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
        }
    });

    cb = findViewById(R.id.cancelbutton);
    cb.setup(db);  // setup the cancel button so that it can call the hide function of the DoorButton when clicked
    cb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // On Click
        cb.hide();
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
              currentIndex = (currentIndex - 1 + imageResources.length) % imageResources.length;
            }
            // Mettre à jour l'image et le texte du bouton
            backgroundImage.setImageResource(imageResources[currentIndex]);
            slideButton.setText(String.valueOf(currentIndex));
            updateMap();
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
      }
    });

    // Flo images svg zoom
    backgroundImage = findViewById(R.id.backgroundImage);
    backgroundImage.setImageResource(R.drawable.school_map1);

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
        if (salle.length() < 4) {
          Log.d("Firebase", "Nom de salle trop court.");
          return; // La salle doit être au moins de 4 caractères (comme 1003)
        }

        // Extraire l'étage à partir du premier caractère
        String etage = "0"; // Étages comme dans votre exemple
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
        }

        takeroombubble.getLocationInWindow(infoCoords);

        imageLeft = infoCoords[0];
        imageTop = infoCoords[1];
        imageRight = imageLeft + takeroombubble.getWidth();
        imageBottom = imageTop + takeroombubble.getHeight();

        if (touchX < imageLeft || touchX > imageRight ||
                touchY < imageTop || touchY > imageBottom) {
          cb.hide();
        }
      }
    }
  }

  public static void ADE_refresh() {
      new CalendarFetcher().execute();
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

  private void updateMap() {
    // Mettre à jour l'image de fond
    backgroundImage.setImageResource(imageResources[currentIndex]);

  }


}