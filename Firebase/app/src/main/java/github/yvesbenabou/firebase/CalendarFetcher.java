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

        if (!rooms.containsSalle(location) && location.length() >= 4) {
            try {
                rooms.setSalle(new Salle(location, github.yvesbenabou.firebase.Status.FREE, " ", null));//libre par défaut
            } catch (Exception e) {
                Log.e(TAG, "ERREUR SALLE: " + location, e);
            }
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
                            checkNumeroSalleEtSetRooms(newLocation, end);
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
/*
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

        CalendarFetcher.rooms.getSalle("0115").show();
        CalendarFetcher.rooms.getSalle("0160").show();
       // CalendarFetcher.rooms.getSalle("0115").show();
        CalendarFetcher.rooms.getSalle("0112").show();
        CalendarFetcher.rooms.getSalle("0113").show();
        CalendarFetcher.rooms.getSalle("0114").show();
        CalendarFetcher.rooms.getSalle("0110").show();

        CalendarFetcher.rooms.getSalle("0160").show();
//        CalendarFetcher.rooms.getSalle("0161").show();
        CalendarFetcher.rooms.getSalle("0162").show();
        CalendarFetcher.rooms.getSalle("0163").show();
        CalendarFetcher.rooms.getSalle("0164").show();
        CalendarFetcher.rooms.getSalle("0165").show();
*/
    }

    public static void show_2() {
        CalendarFetcher.rooms.getSalle("1201").show();
        CalendarFetcher.rooms.getSalle("1203").show();
        CalendarFetcher.rooms.getSalle("1205").show();
        CalendarFetcher.rooms.getSalle("1207").show();

        CalendarFetcher.rooms.getSalle("2201").show();
        CalendarFetcher.rooms.getSalle("2203").show();
        CalendarFetcher.rooms.getSalle("2205").show();
        CalendarFetcher.rooms.getSalle("2207").show();

        CalendarFetcher.rooms.getSalle("3201").show();
        CalendarFetcher.rooms.getSalle("3203").show();
        CalendarFetcher.rooms.getSalle("3205").show();
        CalendarFetcher.rooms.getSalle("3207").show();
//        CalendarFetcher.rooms.getSalle("3209").show();

//        CalendarFetcher.rooms.getSalle("4205").show();
//        CalendarFetcher.rooms.getSalle("4209").show();
        CalendarFetcher.rooms.getSalle("4201").show();
        CalendarFetcher.rooms.getSalle("5201").show();
//        CalendarFetcher.rooms.getSalle("5203").show();
 //       CalendarFetcher.rooms.getSalle("5205").show();
        CalendarFetcher.rooms.getSalle("5207").show();
        CalendarFetcher.rooms.getSalle("5201").show();
//        CalendarFetcher.rooms.getSalle("5209").show();

        CalendarFetcher.rooms.getSalle("0210").show();
        CalendarFetcher.rooms.getSalle("0260").show();

    }

    public static void show_3() {
        CalendarFetcher.rooms.getSalle("1301").show();
//        CalendarFetcher.rooms.getSalle("1303").show();
        CalendarFetcher.rooms.getSalle("1305").show();
        CalendarFetcher.rooms.getSalle("1307").show();

//        CalendarFetcher.rooms.getSalle("2301").show();
 //       CalendarFetcher.rooms.getSalle("2303").show();
        CalendarFetcher.rooms.getSalle("2305").show();
 //       CalendarFetcher.rooms.getSalle("2307").show();
//        CalendarFetcher.rooms.getSalle("2309").show(); EXISTE

        CalendarFetcher.rooms.getSalle("3301").show();
  //      CalendarFetcher.rooms.getSalle("3303").show();
        CalendarFetcher.rooms.getSalle("3305").show();
        CalendarFetcher.rooms.getSalle("3307").show();
//        CalendarFetcher.rooms.getSalle("3309").show();

        CalendarFetcher.rooms.getSalle("4305").show();
//        CalendarFetcher.rooms.getSalle("4307").show(); EXISTE
        CalendarFetcher.rooms.getSalle("5301").show();
  //      CalendarFetcher.rooms.getSalle("5303").show();
  //      CalendarFetcher.rooms.getSalle("5305").show();
//        CalendarFetcher.rooms.getSalle("5309").show(); EXISTE
    }

    public static void show_4() {
        CalendarFetcher.rooms.getSalle("1401").show();
        CalendarFetcher.rooms.getSalle("1403").show();
        CalendarFetcher.rooms.getSalle("1405").show();
        CalendarFetcher.rooms.getSalle("1407").show();

        CalendarFetcher.rooms.getSalle("2401").show();
//        CalendarFetcher.rooms.getSalle("2403").show();
 //       CalendarFetcher.rooms.getSalle("2405").show();
//        CalendarFetcher.rooms.getSalle("2409").show(); EXISTE

        CalendarFetcher.rooms.getSalle("3401").show();
  //      CalendarFetcher.rooms.getSalle("3403").show();
//        CalendarFetcher.rooms.getSalle("3405").show();
        CalendarFetcher.rooms.getSalle("3407").show();
  //      CalendarFetcher.rooms.getSalle("3409").show();

        CalendarFetcher.rooms.getSalle("4401").show();
        CalendarFetcher.rooms.getSalle("4405").show();
//        CalendarFetcher.rooms.getSalle("4403").show(); EXISTE

   //     CalendarFetcher.rooms.getSalle("5401").show();
   //     CalendarFetcher.rooms.getSalle("5403").show();
        CalendarFetcher.rooms.getSalle("5405").show();
        CalendarFetcher.rooms.getSalle("5407").show();
    }

    public static void show_0() {
        CalendarFetcher.rooms.getSalle("1001").show();
        CalendarFetcher.rooms.getSalle("1003").show();
        //CalendarFetcher.rooms.getSalle("1005").show();
        CalendarFetcher.rooms.getSalle("1007").show();

        CalendarFetcher.rooms.getSalle("2001").show();
//        CalendarFetcher.rooms.getSalle("2003").show();
//        CalendarFetcher.rooms.getSalle("2005").show();
//        CalendarFetcher.rooms.getSalle("2007").show();

        CalendarFetcher.rooms.getSalle("3001").show();
 //       CalendarFetcher.rooms.getSalle("3003").show();
   //     CalendarFetcher.rooms.getSalle("3005").show();
        CalendarFetcher.rooms.getSalle("3007").show();
    //    CalendarFetcher.rooms.getSalle("3009").show();

        CalendarFetcher.rooms.getSalle("4003").show();
        CalendarFetcher.rooms.getSalle("4007").show();

    //    CalendarFetcher.rooms.getSalle("4009").show();

//        CalendarFetcher.rooms.getSalle("5001").show();
//        CalendarFetcher.rooms.getSalle("5003").show();
  //      CalendarFetcher.rooms.getSalle("5005").show();
    //    CalendarFetcher.rooms.getSalle("5007").show();
    }


    public static void hide_1(String etage) {

        for (Salle s : CalendarFetcher.rooms.getList())
            try {
                if (s != null && s.getEtage().equals(etage)) s.hide();
            } catch (Exception e) {
                Log.d(TAG, "hide : Salle " + s.getNum() + " pas trouvée");
            }/*
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

        CalendarFetcher.rooms.getSalle("0110").hide();
        CalendarFetcher.rooms.getSalle("0160").hide();
        CalendarFetcher.rooms.getSalle("0115").hide();
        CalendarFetcher.rooms.getSalle("0112").hide();
        CalendarFetcher.rooms.getSalle("0113").hide();
        CalendarFetcher.rooms.getSalle("0114").hide();
//        CalendarFetcher.rooms.getSalle("0115").hide();

        CalendarFetcher.rooms.getSalle("0160").hide();
        CalendarFetcher.rooms.getSalle("0162").hide();
        CalendarFetcher.rooms.getSalle("0163").hide();
        CalendarFetcher.rooms.getSalle("0164").hide();
        CalendarFetcher.rooms.getSalle("0165").hide();
        */
    }

    public static void hide_2() {
        CalendarFetcher.rooms.getSalle("1201").hide();
        CalendarFetcher.rooms.getSalle("1203").hide();
        CalendarFetcher.rooms.getSalle("1205").hide();
        CalendarFetcher.rooms.getSalle("1207").hide();

        CalendarFetcher.rooms.getSalle("2201").hide();
        CalendarFetcher.rooms.getSalle("2203").hide();
        CalendarFetcher.rooms.getSalle("2205").hide();
        CalendarFetcher.rooms.getSalle("2207").hide();

        CalendarFetcher.rooms.getSalle("3201").hide();
        CalendarFetcher.rooms.getSalle("3203").hide();
        CalendarFetcher.rooms.getSalle("3205").hide();
        CalendarFetcher.rooms.getSalle("3207").hide();
//        CalendarFetcher.rooms.getSalle("3209").hide();

//        CalendarFetcher.rooms.getSalle("4205").hide();
 //       CalendarFetcher.rooms.getSalle("4209").hide();

        CalendarFetcher.rooms.getSalle("5201").hide();
//        CalendarFetcher.rooms.getSalle("5203").hide();
 //       CalendarFetcher.rooms.getSalle("5205").hide();
        CalendarFetcher.rooms.getSalle("5207").hide();

        CalendarFetcher.rooms.getSalle("0210").hide();
        CalendarFetcher.rooms.getSalle("0260").hide();
    }

    public static void hide_3() {
        CalendarFetcher.rooms.getSalle("1301").hide();
//        CalendarFetcher.rooms.getSalle("1303").hide();
        CalendarFetcher.rooms.getSalle("1305").hide();
        CalendarFetcher.rooms.getSalle("1307").hide();

//        CalendarFetcher.rooms.getSalle("2301").hide();
//        CalendarFetcher.rooms.getSalle("2303").hide();
        CalendarFetcher.rooms.getSalle("2305").hide();
  //      CalendarFetcher.rooms.getSalle("2307").hide();

        CalendarFetcher.rooms.getSalle("3301").hide();
//        CalendarFetcher.rooms.getSalle("3303").hide();
        CalendarFetcher.rooms.getSalle("3305").hide();
        CalendarFetcher.rooms.getSalle("3307").hide();
  //      CalendarFetcher.rooms.getSalle("3309").hide();

        CalendarFetcher.rooms.getSalle("4305").hide();
//        CalendarFetcher.rooms.getSalle("4309").hide();

        CalendarFetcher.rooms.getSalle("5301").hide();
//        CalendarFetcher.rooms.getSalle("5303").hide();
     //   CalendarFetcher.rooms.getSalle("5305").hide();
//        CalendarFetcher.rooms.getSalle("5307").hide();
    }

    public static void hide_4() {
        CalendarFetcher.rooms.getSalle("1401").hide();
        CalendarFetcher.rooms.getSalle("1403").hide();
        CalendarFetcher.rooms.getSalle("1405").hide();
        CalendarFetcher.rooms.getSalle("1407").hide();

        CalendarFetcher.rooms.getSalle("2401").hide();
        //CalendarFetcher.rooms.getSalle("2403").hide();
//        CalendarFetcher.rooms.getSalle("2405").hide();
  //      CalendarFetcher.rooms.getSalle("2407").hide();

        CalendarFetcher.rooms.getSalle("3401").hide();
//        CalendarFetcher.rooms.getSalle("3403").hide();
//        CalendarFetcher.rooms.getSalle("3405").hide();
        CalendarFetcher.rooms.getSalle("3407").hide();
 //       CalendarFetcher.rooms.getSalle("3409").hide();

        CalendarFetcher.rooms.getSalle("4405").hide();
  //      CalendarFetcher.rooms.getSalle("4409").hide();

        CalendarFetcher.rooms.getSalle("5401").hide();
        CalendarFetcher.rooms.getSalle("5403").hide();
        CalendarFetcher.rooms.getSalle("5405").hide();
        CalendarFetcher.rooms.getSalle("5407").hide();
    }

    public static void hide_0() {
        CalendarFetcher.rooms.getSalle("1001").hide();
        CalendarFetcher.rooms.getSalle("1003").hide();
       // CalendarFetcher.rooms.getSalle("1005").hide();
        CalendarFetcher.rooms.getSalle("1007").hide();

        CalendarFetcher.rooms.getSalle("2001").hide();
//        CalendarFetcher.rooms.getSalle("2003").hide();
//        CalendarFetcher.rooms.getSalle("2005").hide();
//        CalendarFetcher.rooms.getSalle("2007").hide();

        CalendarFetcher.rooms.getSalle("3001").hide();
 //       CalendarFetcher.rooms.getSalle("3003").hide();
//        CalendarFetcher.rooms.getSalle("3005").hide();
        CalendarFetcher.rooms.getSalle("3007").hide();
 //       CalendarFetcher.rooms.getSalle("3009").hide();

//        CalendarFetcher.rooms.getSalle("4005").hide();
        CalendarFetcher.rooms.getSalle("4003").hide();
        CalendarFetcher.rooms.getSalle("4007").hide();
 //       CalendarFetcher.rooms.getSalle("4009").hide();

//        CalendarFetcher.rooms.getSalle("5001").hide();
//        CalendarFetcher.rooms.getSalle("5003").hide();
//        CalendarFetcher.rooms.getSalle("5005").hide();
     //   CalendarFetcher.rooms.getSalle("5007").hide();


    }
}