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
        //updateRoomStates();
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

        String urlStr = "https://planif.esiee.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=746,747,748,2781,2782,3286,682,683,684,685,674,680,727,733,744,786,790,2841,7183,7343,428,659,731,734,736,739,740,741,742,743,782,1852,2555,2584,2743,665,681,735,738,780,785,787,788,1295,2270,2275,2277,2278,2282,4350,5321,5688,704,745,773,728,2117,772,183,185,196,4051,4679,1858,2072,2074,163,167,701,775,776,2272,2276,4794,4937,725,726,759,1908,2594,154,700,705,707,708,712,713,714,715,716,724,737,749,758,1057,2090,2108,2281,6061,299,717,720,721,722,2265,2274,2279,5818,5820&projectId=12&calType=ical&nbWeeks=4";

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

                MainActivity.Liste_Salles rooms = new MainActivity.Liste_Salles();

                // Parcourir les événements et vérifier si la date cible est entre la date de début et de fin
                for (VEvent event : events) {
                    Log.d(TAG, "Evenement ");
                    Date start = event.getDateStart().getValue();
                    Date end = event.getDateEnd().getValue();
                    String location = event.getLocation().getValue();

                    if (!rooms.containsSalle(location))
                        rooms.setSalle(new Salle(location, 0, new int[] {0, 0}));//libre par défaut

                    // Vérifie si la date cible est entre le début et la fin de l'événement
                    if (!date.before(start) && !date.after(end)) {
                        for (String numSalle : rooms.getSallesSet()) {
                            // Vérifiez si l'événement correspond à la salle dans la HashMap
                            if (location.equals(numSalle)) {
                                // Mettre à jour l'état de la salle avec le résumé de l'événement
                                rooms.getSalle(numSalle).setState(1);
                                Log.d(TAG, "Salle : " + numSalle + " - État : " + rooms.getSalle(numSalle).getState());
                            }
                        }
                    }
                }
                //Ici rooms contient la liste des salles (Occupées et libres)
                // TODO fonction pour MAJ la bdd

                new PushListInDatabase(rooms);

                // Essai n°2 : parcourir les salles et vérifier si il y a un event
//                Log.d(TAG, "TEST");
//                for (String numSalle : rooms.getSallesSet()) {
//                    Salle salle = rooms.getSalle(numSalle);
//                    for (VEvent event : events) {
//                        Date start = event.getDateStart().getValue();
//                        Date end = event.getDateEnd().getValue();
//                        String location = event.getLocation().getValue();
//                        if (!date.before(start) && !date.after(end) ) {
//                            if (location.equals(numSalle)) {
//                                salle.setState("Occupée");
//                                Log.d(TAG, "Salle : " + numSalle + " - État : " + salle.getState());
//                            } else {
//                                salle.setState("Libre");
//                                Log.d(TAG, "Salle : " + numSalle + " - État : " + salle.getState());
//                            }
//                        }
//                    }
//                }
            } else {
                Log.d(TAG, "ical = null");
            }

            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du téléchargement ou de l'analyse du fichier .ics", e);
        }
    }
}
