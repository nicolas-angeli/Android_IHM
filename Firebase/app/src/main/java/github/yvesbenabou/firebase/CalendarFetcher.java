package github.yvesbenabou.firebase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import java.util.Calendar;
import java.util.TimeZone;


public class CalendarFetcher {

    public static MainActivity.Liste_Salles rooms = new MainActivity.Liste_Salles();
    private static final String TAG = "CalendarFetcher";

    private static void checkNumeroSalleEtSetRooms(String location, Date end) {
        location = location.replaceAll("[^0-9]", "");

        if (!rooms.containsSalle(location)) {
            rooms.setSalle(new Salle(location, github.yvesbenabou.firebase.Status.FREE, " ", null));//libre par défaut
            if(location.length() > 4)   Log.d(TAG, "location: " + location + "???");
        }
    }

    // Fonction statique pour mettre à jour l'état de chaque salle en fonction d'une date et d'une URL
    public static void updateRoomStates() {
        /*Pour tester
        Date date = Calendar.getInstance().getTime();
        */
        //test
        Date date = UpdateFetcher.date;
        DatabaseReference reservationsRef = MainActivity.databaseRef.child("Réservations").child("étages");
        DatabaseReference salles = MainActivity.databaseRef.child("étages");
        try {
            // Téléchargez le fichier .ics depuis l'URL spécifiée
            URL url = new URL(MainActivity.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            // Lire et analyser le fichier iCalendar
            ICalendar ical = Biweekly.parse(inputStream).first();

            if (ical != null) {
                List<VEvent> events = ical.getEvents();

                // Parcourir les événements et vérifier si la date cible est entre la date de début et de fin
                for (VEvent event : events) {
                    Date start = event.getDateStart().getValue();
                    Date end = event.getDateEnd().getValue();
                    String location = event.getLocation().getValue();
                    String[] locations = location.split(",");

                    for (String newLocation : locations){
                        checkNumeroSalleEtSetRooms(newLocation, end);
                    }

                    // Vérifie si la date cible est entre le début et la fin de l'événement
                    if (!date.before(start) && !date.after(end)) {
                        for (String numSalle : rooms.getSallesSet()) {
                            Log.d(TAG, "Location = "+ location);

                            for (String newLocation : locations) {
                                if (newLocation.equals(numSalle)) { // Vérifier si l'événement correspond à des salles de la HashMap
                                    // Mettre à jour l'état de la salle avec le résumé de l'événement
                                    Salle room = rooms.getSalle(numSalle);
                                    room.setState(github.yvesbenabou.firebase.Status.CLASS);
                                    Task roomTask = salles.child(String.valueOf(numSalle.charAt(1))).child(numSalle).setValue(github.yvesbenabou.firebase.Status.CLASS.ordinal());
                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                    format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                                    String str = format.format(end);
                                    room.setEnd(str);
                                    Task reservationTask = reservationsRef.child(String.valueOf(numSalle.charAt(1))).child(numSalle).setValue(str);
                                    try { Tasks.await(roomTask); } catch (Exception e) {
                                        Log.d(TAG, "Erreur lors de la modification de l'état de la salle " + numSalle);
                                    }
                                    try { Tasks.await(reservationTask); } catch (Exception e) {
                                        Log.d(TAG, "Erreur lors de la modification de l'état de la salle " + numSalle);
                                    }
                                }
                            }
                        }
                    }
                }
                //Ici rooms contient la liste des salles (Occupées et libres)
                new PushListInDatabase(rooms);

            } else {
                Log.d(TAG, "ical = null");
            }

            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du téléchargement ou de l'analyse du fichier .ics", e);
        }
    }

    public static void show_1(String etage) {

        for (Salle s : CalendarFetcher.rooms.getList()) {
            if (s.getEtage().equals(etage)) s.show();
        }
    }



    public static void hide_1(String etage) {
        for (Salle s : CalendarFetcher.rooms.getList())
            if (s != null && s.getEtage().equals(etage)) s.hide();
    }
}