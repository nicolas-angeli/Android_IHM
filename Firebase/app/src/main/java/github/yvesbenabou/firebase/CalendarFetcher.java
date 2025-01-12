package github.yvesbenabou.firebase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
        }
    }

    // Fonction statique pour mettre à jour l'état de chaque salle en fonction d'une date et d'une URL
    public static void updateRoomStates() {
        Date date = Calendar.getInstance().getTime();

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
                    Log.d(TAG, "Evenement ");
                    Date start = event.getDateStart().getValue();
                    Date end = event.getDateEnd().getValue();
                    String location = event.getLocation().getValue();

                    if (location.indexOf(',') > -1){//Si plusieur salles dans un seul cours
                        String[] splitedLocation = location.split(",");
                        for (String newLocation : splitedLocation){
                            checkNumeroSalleEtSetRooms(location, end);
                        }
                    }
                    else checkNumeroSalleEtSetRooms(location, end);

                    // Vérifie si la date cible est entre le début et la fin de l'événement
                    if (!date.before(start) && !date.after(end)) {
                        for (String numSalle : rooms.getSallesSet()) {
                            Log.d(TAG, "Location =  : "+ location);

                            // Vérifiez si l'événement correspond à la salle dans la HashMap
                            if (location.equals(numSalle)) {
                                // Mettre à jour l'état de la salle avec le résumé de l'événement
                                Salle room = rooms.getSalle(numSalle);
                                room.setState(github.yvesbenabou.firebase.Status.CLASS);
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                                String str = format.format(end);
                                room.setEnd(str);
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
            try {
                if (s.getEtage().equals(etage)) s.show();
            } catch (Exception e) {
                Log.d(TAG, "show : Salle " + s.getNum() + " pas trouvée");
            }
        }

    }


    public static void hide_1(String etage) {

        for (Salle s : CalendarFetcher.rooms.getList())
            try {
                if (s != null && s.getEtage().equals(etage)) s.hide();
            } catch (Exception e) {
                Log.d(TAG, "hide : Salle " + s.getNum() + " pas trouvée");
            }
    }
}