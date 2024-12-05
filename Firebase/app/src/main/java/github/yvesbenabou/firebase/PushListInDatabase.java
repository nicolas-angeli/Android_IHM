package github.yvesbenabou.firebase;

import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class PushListInDatabase {
    private static final String floors = "étages";
    private static final int MAX_LENGTH = 7;//Numéro de salle max : xx0xV++
    public PushListInDatabase(MainActivity.Liste_Salles rooms) {
        int i = 0;

        for (Salle salle : rooms.getList()) {
            //Verification de la salle:
            if ((salle.getNum().length() <= MAX_LENGTH) && (salle.getState().ordinal() > Status.FREE.ordinal()))
                push_room(salle);
        }
    }

    public void push_room(@NonNull Salle salle) {
        String room = salle.getNum();

        if (salle.getState() == Status.CLASS) {
            FirebaseDatabase.getInstance().getReference().child("Réservations").child(floors).child(String.valueOf(salle.getEtage())).child(room).setValue(salle.getEnd());
            FirebaseDatabase.getInstance().getReference().child(floors).child(String.valueOf(salle.getEtage())).child(room).setValue(salle.getState().ordinal());
        }
    }
}
