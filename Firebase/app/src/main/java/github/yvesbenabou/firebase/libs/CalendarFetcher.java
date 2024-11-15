package github.yvesbenabou.firebase.libs;
import android.os.AsyncTask;
import android.util.Log;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import github.yvesbenabou.firebase.MainActivity;
import java.util.Calendar;


public class CalendarFetcher extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "CalendarFetcher";

    // Date cible pour filtrer les événements
    private Date targetDate;

    public CalendarFetcher(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        updateRoomStates();
        return null;
    }

    // Fonction statique pour mettre à jour l'état de chaque salle en fonction d'une date et d'une URL
    public static void updateRoomStates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        String urlStr = "https://planif.esiee.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=&projectId=12&calType=ical&nbWeeks=4";

        MainActivity.Liste_Salles rooms = new MainActivity.Liste_Salles();

        try {
            // Téléchargez le fichier .ics depuis l'URL spécifiée
            URL url = new URL(urlStr);
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

                    // Vérifie si la date cible est entre le début et la fin de l'événement
                    if (!date.before(start) && !date.after(end)) {
                        for (String numSalle : rooms.getSallesSet()) {
                            // Vérifiez si l'événement correspond à la salle dans la HashMap
                            if (location.equals(numSalle)) {
                                // Mettre à jour l'état de la salle avec le résumé de l'événement
                                rooms.getSalle(numSalle).setState("Occupée : " + event.getSummary().getValue());
                                Log.d(TAG, "Salle : " + numSalle + " - État : " + rooms.getSalle(numSalle).getState());
                            }
                        }
                    }
                }
            }

            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du téléchargement ou de l'analyse du fichier .ics", e);
        }
    }
}
