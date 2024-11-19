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

//Flo boutton etage
import android.view.MotionEvent;
import android.widget.Button;

//Flo boutton info
import android.os.Handler;

// Flo images svg zoom (reste déjà import)
import android.view.ScaleGestureDetector;

//Nico synchro de la base de données avec ADE
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

//Nico horloge
import android.app.TimePickerDialog;
import android.widget.TimePicker;

import java.util.HashMap;
import java.util.TimeZone;

import android.text.TextWatcher;

import github.yvesbenabou.firebase.libs.CalendarFetcher;
import github.yvesbenabou.firebase.libs.Salle;

public class MainActivity extends AppCompatActivity implements Database_Out {
  private final String floors = "étages";
  private TextView selectedTimeTextView;
  private DoorButton db;
  String TAG = "MainActivity";
  String room = "";

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
  private Handler handler = new Handler();

// Flo images svg zoom
  private ScaleGestureDetector scaleGestureDetector;
  private float scaleFactor = 1.0f;  // Facteur de zoom initial

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    //txt = (TextView) findViewById(R.id.text);

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
        crb.take_room(crb.getRoom());
        crb.hide();
      }
    });

    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();  // and if you explicit the project name with full URL, you get an exception with forbidden characters!

    CancelButton cb = findViewById(R.id.cancelbutton);
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

    slideButton.setOnTouchListener(new View.OnTouchListener() {
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
    backgroundImage.setImageResource(R.drawable.school_map1); // Assurez-vous que c'est un VectorDrawable

    // Initialisation du ScaleGestureDetector
    scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    // Appliquer un OnTouchListener pour détecter le zoom
    backgroundImage.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);  // Passe les événements au ScaleGestureDetector
        return true;
      }
    });
    // ADE
    Calendar calendar = Calendar.getInstance();
    calendar.set(2024, Calendar.NOVEMBER, 25, 8, 30);  // 25 novembre 2023, 8h30
    Date targetDate = calendar.getTime();

    // Lancer le fetcher de calendrier
    new CalendarFetcher(targetDate).execute();

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
    salleInput.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        salleInput.setVisibility(View.VISIBLE);
      }
    });

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
    ImageView takeroombubble = findViewById(R.id.takeroombubble);

    backgroundImage.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        boolean info = (infoImage.getVisibility() == View.VISIBLE);
        boolean reservation = (takeroombubble.getVisibility() == View.VISIBLE);
        if (info || reservation) {
          // Log pour voir si l'événement tactile est détecté
          Log.d("TouchEvent", "Touch event detected");

          if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("TouchEvent", "Action down detected");

            // Coordonnées du touch
            float touchX = event.getX();
            float touchY = event.getY();
            Log.d("TouchEvent", "Touch coordinates: " + touchX + ", " + touchY);

            // Récupérer les dimensions et position de l'ImageView
            int[] infoCoords = new int[2];
            infoImage.getLocationInWindow(infoCoords);

            int imageLeft = infoCoords[0];
            int imageTop = infoCoords[1];
            int imageRight = imageLeft + infoImage.getWidth();
            int imageBottom = imageTop + infoImage.getHeight();

            Log.d("TouchEvent", "Image bounds: " + imageLeft + ", " + imageTop + ", " + imageRight + ", " + imageBottom);

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
        return true;
      }
    });

    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    Log.d(TAG, "Date actuelle : " + dateFormat.format(new Date()));
  }

  // Classe interne pour gérer les événements de zoom
  // Sert pour le ScaleListener du zoom
  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      scaleFactor *= detector.getScaleFactor();  // Augmenter ou réduire le facteur de zoom en fonction du geste
      scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f));  // Limite le zoom entre 0.5x et 3x
      backgroundImage.setScaleX(scaleFactor);  // Applique le zoom en X
      backgroundImage.setScaleY(scaleFactor);  // Applique le zoom en Y
      return true;
    }
  }

   @Override
   public void update() {
     //cette fonction sera appelée toutes les 15 mins pour resynchroniser avec ADE

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
}