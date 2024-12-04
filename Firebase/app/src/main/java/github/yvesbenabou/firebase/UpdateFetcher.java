package github.yvesbenabou.firebase;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;


public class UpdateFetcher extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "UpdateFetcher";
    private static boolean b_ADEDate = false;
    private static boolean b_ADETime = false;
    private static boolean b_LiberationDate = false;
    private static boolean b_LiberationTime = false;
    public static Date date;

    private final DatabaseReference ADEDate = MainActivity.databaseRef.child("ADEDate");
    private final DatabaseReference ADETime = MainActivity.databaseRef.child("ADETime");
    private final DatabaseReference LiberationDate = MainActivity.databaseRef.child("LiberationDate");
    private final DatabaseReference LiberationTime = MainActivity.databaseRef.child("LiberationTime");

    // Date cible pour filtrer les événements
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat hourFormat;

    public UpdateFetcher() {
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        this.hourFormat = new SimpleDateFormat("HH:mm");
        this.hourFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isCancelled()) {
            UpdateFetcher.b_ADEDate = false;
            UpdateFetcher.b_ADETime = false;
            UpdateFetcher.b_LiberationDate = false;
            UpdateFetcher.b_LiberationTime = false;

            /*pour tester
            String date = dateFormat.format(new Date());
            String time = hourFormat.format(new Date());

            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));
            */
            //test
            int hour = TestClock.hrs;
            int minute = 1;
            UpdateFetcher.date = TestClock.refreshTime();
            String date = dateFormat.format(UpdateFetcher.date);

            //On scrute la base de données afin de déterminer si elle a été mise à jour aujourd'hui

            Thread ThreadADEDate = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Mettre la priorité du thread en arrière-plan plus faible
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    try {
                        Tasks.await(
                                MainActivity.databaseRef.child("ADEDate").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().exists()) {
                                        String ADE_date = task.getResult().getValue(String.class);
                                        UpdateFetcher.b_ADEDate = ADE_date.equals(date);
                                    } else {
                                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                                    }
                                })
                        );
                    } catch (Exception e) {
                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                    }
                }
            });

            ThreadADEDate.start();

            Thread ThreadLibDate = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Mettre la priorité du thread en arrière-plan plus faible
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    try {
                        Tasks.await(
                                MainActivity.databaseRef.child("LiberationDate").get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().exists()) {
                                        String Lib_date = task.getResult().getValue(String.class);
                                        UpdateFetcher.b_LiberationDate = Lib_date.equals(date);
                                    } else {
                                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                                    }
                                })
                        );
                    } catch (Exception e) {
                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                    }
                }
            });

            ThreadLibDate.start();

            try {
                ThreadADEDate.join();
                ThreadLibDate.join();
            } catch (InterruptedException e) {
                Log.d("TAG", "Can't join threads");
            }

            //On scrute les dernières heures de mises à jour

            if(UpdateFetcher.b_ADEDate && UpdateFetcher.b_LiberationDate) {
                Thread ThreadADETime = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Mettre la priorité du thread en arrière-plan plus faible
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                        try {
                            Tasks.await(
                                    MainActivity.databaseRef.child("ADETime").get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult().exists()) {
                                            String ADE_time = task.getResult().getValue(String.class);
                                            int update_hour;
                                            int update_minute;
                                            //assert ADE_time != null;
                                            int l = ADE_time.length();
                                            if (l >= 5) {
                                                update_hour = Integer.parseInt(ADE_time.substring(0, 2));
                                                update_minute = Integer.parseInt(ADE_time.substring(3, 5));
                                            }
                                            else{
                                                update_hour = Integer.parseInt(ADE_time.substring(0, 1));
                                                update_minute = Integer.parseInt(ADE_time.substring(2, 4));
                                            }

                                            UpdateFetcher.b_ADETime = (hour > update_hour && minute > 0 || (hour == update_hour && minute > update_minute + 30));
                                        } else {
                                            Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                                        }
                                    })
                            );
                        } catch (Exception e) {
                            Log.d("TAG", "Une erreur est survenue");
                        }
                    }
                });

                ThreadADETime.start();

                Thread ThreadLibTime = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Mettre la priorité du thread en arrière-plan plus faible
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                        try {
                            Tasks.await(
                                    MainActivity.databaseRef.child("LiberationTime").get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult().exists()) {
                                            String Liberation_time = task.getResult().getValue(String.class);

                                            int update_hour = Integer.parseInt(Liberation_time.substring(0, 2));
                                            int update_minute = Integer.parseInt(Liberation_time.substring(3, 5));

                                            UpdateFetcher.b_LiberationTime = (hour > update_hour || (hour == update_hour && minute > update_minute));
                                        } else {
                                            Log.d("TAG", "Une erreur est survenue à l'acquisition de la date de libération des créneaux.");
                                        }
                                    })
                            );
                        } catch (Exception e) {
                            Log.d("TAG", "Une erreur est survenue à l'acquisition de la date de libération des créneaux.");
                        }
                    }
                });

                ThreadLibTime.start();

                try {
                    ThreadADETime.join();
                    ThreadLibTime.join();
                } catch (InterruptedException e) {
                    Log.d("TAG", "Can't join threads");
                }
            }

            //nos variables sont à jour, on peut faire les mises à jour nécessaires

            DatabaseReference reservationsRef = MainActivity.databaseRef.child("Réservations").child("étages");
            DatabaseReference salles = MainActivity.databaseRef.child("étages");

            if (UpdateFetcher.b_ADEDate==false && UpdateFetcher.b_LiberationDate==false) {///n'a pas été mise à jour aujourd'hui
                // Ajouter un listener pour récupérer les données
                try {
                    Tasks.await(reservationsRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Vector<Task> vec = new Vector<>();
                            for (DataSnapshot etageSnapshot : task.getResult().getChildren()) {
                                // Parcourir les réservations sous chaque étage
                                for (DataSnapshot reservationSnapshot : etageSnapshot.getChildren()) {
                                    // Récupérer la clé (id de la réservation)
                                    String reservationKey = reservationSnapshot.getKey();

                                    vec.add(salles.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(github.yvesbenabou.firebase.Status.FREE.ordinal()));
                                    vec.add(reservationsRef.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(" "));

                                }
                            }
                            for(Task tache : vec) {
                                try { Tasks.await(tache); } catch (Exception e) {
                                    Log.d(TAG, "Erreur");
                                }
                            }
                        } else {
                            Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                        }
                    }));
                } catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                }
            }

            if (UpdateFetcher.b_LiberationTime) { //vérifier les créneaux
                // Ajouter un listener pour récupérer les données
                try {
                    Tasks.await(reservationsRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot etageSnapshot : task.getResult().getChildren()) {
                                // Parcourir les réservations sous chaque étage
                                Vector<Task> vec = new Vector<>();
                                for (DataSnapshot reservationSnapshot : etageSnapshot.getChildren()) {
                                    // Récupérer la clé (id de la réservation)
                                    String reservationKey = reservationSnapshot.getKey();
                                    String reservationValue = reservationSnapshot.getValue(String.class);

                                    if (reservationValue.length() > 1) {
                                        try {
                                            int hour_deadline = Integer.parseInt(reservationValue.substring(0, 2));
                                            int minute_deadline = Integer.parseInt(reservationValue.substring(3, 5));

                                            if (hour > hour_deadline || (hour == hour_deadline && minute > minute_deadline)) {
                                                try { Tasks.await(salles.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(github.yvesbenabou.firebase.Status.FREE.ordinal()));} catch (Exception e) {
                                                    Log.d("TAG", "Une erreur est survenue à écrire le statut de la réservation.");
                                                }
                                                try { Tasks.await(reservationsRef.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(" "));} catch (Exception e) {
                                                    Log.d("TAG", "Une erreur est survenue à écrire le statut de la réservation.");
                                                }
                                            }
                                        } catch (ClassCastException e) {
                                            Log.d("TAG", "La valeur de la réservation n'est pas un entier.");
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                        }
                    }));
                } catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                }
            }

            if (UpdateFetcher.b_LiberationTime || !UpdateFetcher.b_LiberationDate) { //raffraichir la base avec une mise à jour d'ADE
                try { Tasks.await(LiberationDate.setValue(date));} catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à écrire la date de libération.");
                }
                //LiberationTime.setValue(time);
                if (hour < 10) try { Tasks.await(LiberationTime.setValue("0" + hour + ":01"));} catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à écrire la date de libération.");
                }
                else try { Tasks.await(LiberationTime.setValue(hour + ":01"));} catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à écrire la date de libération.");
                }
            }

            if (UpdateFetcher.b_ADETime || !UpdateFetcher.b_ADEDate) { //raffraichir la base avec une mise à jour d'ADE
                MainActivity.ADE_refresh();
                try { Tasks.await(ADEDate.setValue(date));} catch (Exception e) {
                    Log.d("TAG", "Une erreur est survenue à écrire la date d'ADE.");
                }
                String zero;
                if (hour< 10) {
                    zero = "0";
                } else
                    zero = "";//3401 3407 2401 2409 5301 5309 4305 3407 2305

                if(minute >= 30){
                    try { Tasks.await(ADETime.setValue(zero + hour + ":30"));} catch (Exception e) {
                        Log.d("TAG", "Une erreur est survenue à écrire la date d'ADE.");
                    }
                }
                else {
                    try { Tasks.await(ADETime.setValue(zero + hour + ":00"));} catch (Exception e) {
                        Log.d("TAG", "Une erreur est survenue à écrire la date d'ADE.");
                    }
                }
            }

            try {
                Thread.sleep(15000);  // Pause de 15 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}