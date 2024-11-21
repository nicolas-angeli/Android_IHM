package github.yvesbenabou.firebase.libs;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import github.yvesbenabou.firebase.MainActivity;
import github.yvesbenabou.firebase.Database_Input;
import github.yvesbenabou.firebase.Status;

import android.content.Context;
import android.util.AttributeSet;

public class PushListInDatabase {
    private static final String floors = "étages";
    private static final int MAX_LENGTH = 7;//Numéro de salle max : xx0xV++
    public PushListInDatabase(MainActivity.Liste_Salles rooms) {
        int i = 0;

        for (Salle salle : rooms.getList()) {
            //Limite provisoire pour la bdd :
            if (i++ > 50) return;

            // TODO : Insertion de la salle dans la base de données Firebase
            // Exemple : FirebaseDatabase.getInstance().getReference("salles").child(room.getNum()).setValue(room);
            // Vous pouvez utiliser des classes FirebaseDatabase ou FirebaseFirestore pour faciliter la communication avec Firebase

            //Verification de la salle:
            if (salle.getNum().length() <= MAX_LENGTH && salle.getState().ordinal() >= Status.FREE.ordinal())
                push_room(salle);
        }
    }

    public void push_room(Salle salle) {
        String room = salle.getNum();
        //FirebaseApp.initializeApp(this.getContext());
        if (salle.getState() == Status.CLASS)
            FirebaseDatabase.getInstance().getReference().child(floors).child(String.valueOf(salle.getEtage())).child(room).setValue(salle.getState().ordinal());//.setValue(1);
//        else {
//            FirebaseDatabase.getInstance().getReference().child(floors).child(String.valueOf(salle.getEtage())).child(room).setValue(0);
//        }
    }
}
