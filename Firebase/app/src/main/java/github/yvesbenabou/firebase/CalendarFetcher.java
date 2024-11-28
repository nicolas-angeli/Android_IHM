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
                                if(room != null) room.setState(github.yvesbenabou.firebase.Status.CLASS);
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

    public static void show() {
        try {
            CalendarFetcher.rooms.getSalle("1101").show();
            CalendarFetcher.rooms.getSalle("1103").show();
            CalendarFetcher.rooms.getSalle("1105").show();
            CalendarFetcher.rooms.getSalle("1107").show();

            CalendarFetcher.rooms.getSalle("2101").show();
            CalendarFetcher.rooms.getSalle("2103").show();
            CalendarFetcher.rooms.getSalle("2105").show();
            CalendarFetcher.rooms.getSalle("2107").show();

            CalendarFetcher.rooms.getSalle("3101").show();
            CalendarFetcher.rooms.getSalle("3103").show();
            CalendarFetcher.rooms.getSalle("3105").show();
            CalendarFetcher.rooms.getSalle("3107").show();
            CalendarFetcher.rooms.getSalle("3109").show();

            CalendarFetcher.rooms.getSalle("4105").show();
            CalendarFetcher.rooms.getSalle("4109").show();

            CalendarFetcher.rooms.getSalle("5101").show();
            CalendarFetcher.rooms.getSalle("5103").show();
            CalendarFetcher.rooms.getSalle("5105").show();
            CalendarFetcher.rooms.getSalle("5107").show();
        } catch (Exception e){
            Log.e(TAG, "Erreur dans show(): " + e.getMessage());
        }
    }
    
    public static void hide() {
        try {
            CalendarFetcher.rooms.getSalle("1101").hide();
            CalendarFetcher.rooms.getSalle("1103").hide();
            CalendarFetcher.rooms.getSalle("1105").hide();
            CalendarFetcher.rooms.getSalle("1107").hide();

            CalendarFetcher.rooms.getSalle("2101").hide();
            CalendarFetcher.rooms.getSalle("2103").hide();
            CalendarFetcher.rooms.getSalle("2105").hide();
            CalendarFetcher.rooms.getSalle("2107").hide();

            CalendarFetcher.rooms.getSalle("3101").hide();
            CalendarFetcher.rooms.getSalle("3103").hide();
            CalendarFetcher.rooms.getSalle("3105").hide();
            CalendarFetcher.rooms.getSalle("3107").hide();
            CalendarFetcher.rooms.getSalle("3109").hide();

            CalendarFetcher.rooms.getSalle("4105").hide();
            CalendarFetcher.rooms.getSalle("4109").hide();

            CalendarFetcher.rooms.getSalle("5101").hide();
            CalendarFetcher.rooms.getSalle("5103").hide();
            CalendarFetcher.rooms.getSalle("5105").hide();
            CalendarFetcher.rooms.getSalle("5107").hide();
        }
        catch (Exception e){
            Log.e(TAG, "Erreur dans hide(): " + e.getMessage());
        }
    }
}
