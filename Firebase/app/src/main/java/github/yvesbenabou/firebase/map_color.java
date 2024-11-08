package github.yvesbenabou.firebase;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class map_color extends AppCompatActivity {

    private ImageView backgroundImage;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Créer le FrameLayout et l'ImageView pour afficher la carte
        frameLayout = new FrameLayout(this);
        backgroundImage = new ImageView(this);

        // Charger l'image de la carte depuis les ressources
        backgroundImage.setImageResource(R.drawable.school_map1); // Remplace par ton ID de carte
        backgroundImage.setScaleType(ImageView.ScaleType.MATRIX); // Nécessaire pour les transformations de zoom

        // Ajouter l'image de fond au FrameLayout
        frameLayout.addView(backgroundImage);

        // Initialiser ScaleGestureDetector pour détecter les gestes de zoom
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Définir le FrameLayout comme contenu de la vue de l'activité
        setContentView(frameLayout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Passer les événements au ScaleGestureDetector pour le zoom
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    // Classe interne pour gérer les événements de zoom
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Modifier le facteur de zoom en fonction du ScaleGestureDetector
            scaleFactor *= detector.getScaleFactor();

            // Limiter le zoom entre un minimum et un maximum
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f));

            // Appliquer le facteur de zoom à l'image de fond
            backgroundImage.setScaleX(scaleFactor);
            backgroundImage.setScaleY(scaleFactor);

            return true;
        }
    }
}


/*
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class map_color extends AppCompatActivity {

    private ImageView backgroundImage;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle Etat_instant){

        super.onCreate(Etat_instant);

        // Créer le FrameLayout et l'ImageView
        frameLayout = new FrameLayout(this);
        backgroundImage = new ImageView(this);
        backgroundImage.setImageResource(R.drawable.school_map); // Image dans res/drawable
        backgroundImage.setScaleType(ImageView.ScaleType.MATRIX);


        // Ajouter l'image de fond et le FrameLayout
        frameLayout.addView(backgroundImage);
        setContentView(frameLayout);

        // Ajouter des salles avec des carrés de couleur initiale
        addRoomSquare(200, 300, 100, 100, "Salle A", true);
        addRoomSquare(500, 600, 120, 120, "Salle B", false);

        // Boutton de réservation (Nico)
    }

    // Touché d'écran
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    // Ajoute un carré pour une salle
    private void addRoomSquare(int x, int y, int width, int height, String roomName, boolean isAvailable) {
        View roomSquare = new View(this);
        roomSquare.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        roomSquare.setBackgroundColor(isAvailable ? Color.GREEN : Color.RED);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) roomSquare.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        roomSquare.setLayoutParams(params);

        // Définir le tag pour identifier la salle et ajouter au layout
        roomSquare.setTag(roomName);
        roomSquare.setOnClickListener(v -> openReservationDialog((View) v));
        frameLayout.addView(roomSquare);
    }

    // Ouvre une boîte de dialogue pour réserver une salle
    private void openReservationDialog() {
        openReservationDialog(null);
    }

    private void openReservationDialog(View roomSquare) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Réserver une salle");

        // Interface pour entrer les détails de la réservation
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText roomNameInput = new EditText(this);
        roomNameInput.setHint("Nom de la salle");
        layout.addView(roomNameInput);

        final EditText startTimeInput = new EditText(this);
        startTimeInput.setHint("De (ex: 14:00)");
        layout.addView(startTimeInput);

        final EditText endTimeInput = new EditText(this);
        endTimeInput.setHint("À (ex: 15:00)");
        layout.addView(endTimeInput);

        builder.setView(layout);

        // Charger la salle si elle est connue
        if (roomSquare != null) {
            roomNameInput.setText((String) roomSquare.getTag());
        }

        // Boutons de confirmation et d'annulation
        builder.setPositiveButton("Réserver", (dialog, which) -> {
            String roomName = roomNameInput.getText().toString();
            String startTime = startTimeInput.getText().toString();
            String endTime = endTimeInput.getText().toString();

            // Afficher la salle comme réservée (orange)
            if (roomSquare != null) {
                roomSquare.setBackgroundColor(Color.parseColor("#FFA500")); // Orange
            }
        });


        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        // Afficher la boîte de dialogue
        builder.show();
    }

    // Classe interne pour gérer les événements de zoom
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f));
            backgroundImage.setScaleX(scaleFactor);
            backgroundImage.setScaleY(scaleFactor);

            // Ajuster la taille et la position de chaque salle en fonction du zoom
            for (int i = 1; i < frameLayout.getChildCount(); i++) {
                View roomSquare = frameLayout.getChildAt(i);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) roomSquare.getLayoutParams();

                params.width = (int) (params.width * detector.getScaleFactor());
                params.height = (int) (params.height * detector.getScaleFactor());
                params.leftMargin = (int) (params.leftMargin * detector.getScaleFactor());
                params.topMargin = (int) (params.topMargin * detector.getScaleFactor());
                roomSquare.setLayoutParams(params);
            }
            return true;
        }
    }

}
*/
