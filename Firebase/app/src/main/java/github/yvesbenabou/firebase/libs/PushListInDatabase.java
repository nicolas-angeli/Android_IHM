package github.yvesbenabou.firebase.libs;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import github.yvesbenabou.firebase.MainActivity;
import github.yvesbenabou.firebase.Database_Input;
import github.yvesbenabou.firebase.Status;

public class PushListInDatabase implements Database_Input{

    public PushListInDatabase(MainActivity.Liste_Salles rooms) {

        for (Salle salle : rooms.getList()) {
            // TODO : Insertion de la salle dans la base de données Firebase
            // Exemple : FirebaseDatabase.getInstance().getReference("salles").child(room.getNum()).setValue(room);
            // Vous pouvez utiliser des classes FirebaseDatabase ou FirebaseFirestore pour faciliter la communication avec Firebase
            // N'oubliez pas de mettre à jour les listeners pour les nouvelles données ou modifications
            take_room(salle.getNum());
        }
    }

    @Override
    public void take_room(String room) {
        //FirebaseApp.initializeApp(this.getContext());
        //FirebaseDatabase.getInstance().getReference().child(floors).child(String.valueOf(room.charAt(1))).child(room).setValue(Status.OCCUPIED.ordinal());
    }
}
