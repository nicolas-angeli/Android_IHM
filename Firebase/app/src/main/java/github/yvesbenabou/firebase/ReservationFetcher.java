package github.yvesbenabou.firebase;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class ReservationFetcher extends AsyncTask<Void, Void, Void> {

    private static final String CALENDAR_URL = "https://planif.esiee.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=260,261,1190,1538&projectId=12&calType=ical&nbWeeks=4";
    private static final String TAG = "CalendarFetcher";

    // Date cible pour filtrer les événements
    private Date targetDate;

    public ReservationFetcher(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Téléchargez le fichier .ics depuis l'URL
            URL url = new URL(CALENDAR_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            // Lire et analyser le fichier iCalendar
            ICalendar ical = Biweekly.parse(inputStream).first();

            if (ical != null) {
                List<VEvent> events = ical.getEvents();
                String[][] tab = new String[events.size()][3];
                int i=0;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String targetDateStr = dateFormat.format(targetDate);

                // Parcourir les événements et les filtrer
                for (VEvent event : events) {
                    Date start = event.getDateStart().getValue();
                    Date end = event.getDateEnd().getValue();
                    String location = event.getLocation().getValue();

                    // Filtrer par date (vous pouvez aussi ajouter une condition d'heure)
                    if (dateFormat.format(start).equals(targetDateStr)) {
                        Log.d(TAG, "Événement : " + event.getSummary().getValue());
                        Log.d(TAG, "Début : " + start.toString());
                        Log.d(TAG, "Fin : " + end.toString());
                        Log.d(TAG, "Salle : " + location);
                        Log.d(TAG, "---------------------");

                        tab[i][0] = start.toString();
                        tab[i][1] = end.toString();
                        tab[i][2] = location;
                        i++;
                    }
                }
                i++;
            }

            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du téléchargement ou de l'analyse du fichier .ics", e);
        }
        return null;
    }
}
