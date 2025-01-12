package github.yvesbenabou.firebase;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;


public class UpdateFetcher extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "UpdateFetcher";
    private static boolean b_ADEDate = false;
    private static boolean b_ADETime = false;
    private static boolean b_LiberationDate = false;
    private static boolean b_LiberationTime = false;

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

            String date = dateFormat.format(new Date());
            String time = hourFormat.format(new Date());

            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));

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
                    } catch (Exception e) { }
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
                    } catch (Exception e) { }
                }
            });

            ThreadLibDate.start();

            try {
                ThreadADEDate.join();
                ThreadLibDate.join();
            } catch (InterruptedException e) {  Log.d("TAG", "Can't join threads"); }

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

                                        int update_hour = Integer.parseInt(ADE_time.substring(0, 2));
                                        int update_minute = Integer.parseInt(ADE_time.substring(3, 5));

                                        UpdateFetcher.b_ADETime = (hour > update_hour && minute > 0 || (hour == update_hour && minute > update_minute + 30));
                                    } else {
                                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date ADE.");
                                    }
                                })
                            );
                        } catch (Exception e) { }
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

                                        UpdateFetcher.b_LiberationTime = (hour > update_hour && minute > 0 || (hour == update_hour && minute > update_minute + 30));
                                    } else {
                                        Log.d("TAG", "Une erreur est survenue à l'acquisition de la date de libération des créneaux.");
                                    }
                                })
                            );
                        } catch (Exception e) { }
                    }
                });

                ThreadLibTime.start();

                try {
                    ThreadADETime.join();
                    ThreadLibTime.join();
                } catch (InterruptedException e) {  Log.d("TAG", "Can't join threads"); }
            }

            //nos variables sont à jour, on peut faire les mises à jour nécessaires

            DatabaseReference reservationsRef = MainActivity.databaseRef.child("Réservations").child("étages");
            DatabaseReference salles = MainActivity.databaseRef.child("étages");

            if (UpdateFetcher.b_ADEDate==false && UpdateFetcher.b_LiberationDate==false) {///n'a pas été mise à jour aujourd'hui
                // Ajouter un listener pour récupérer les données
                try {
                    Tasks.await(reservationsRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot etageSnapshot : task.getResult().getChildren()) {
                                // Parcourir les réservations sous chaque étage
                                for (DataSnapshot reservationSnapshot : etageSnapshot.getChildren()) {
                                    // Récupérer la clé (id de la réservation)
                                    String reservationKey = reservationSnapshot.getKey();

                                    salles.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(github.yvesbenabou.firebase.Status.FREE.ordinal());
                                    reservationsRef.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(" ");
                                }
                            }
                        } else {
                            Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                        }
                    }));
                } catch (Exception e) { }
            }

            if (UpdateFetcher.b_LiberationTime) { //vérifier les créneaux
                // Ajouter un listener pour récupérer les données
                try {
                    Tasks.await(reservationsRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot etageSnapshot : task.getResult().getChildren()) {
                                // Parcourir les réservations sous chaque étage
                                for (DataSnapshot reservationSnapshot : etageSnapshot.getChildren()) {
                                    // Récupérer la clé (id de la réservation)
                                    String reservationKey = reservationSnapshot.getKey();
                                    String reservationValue = reservationSnapshot.getValue(String.class);

                                    if (reservationValue.length() > 1) {
                                        try {
                                            int hour_deadline = Integer.parseInt(reservationValue.substring(0, 2));
                                            int minute_deadline = Integer.parseInt(reservationValue.substring(3, 5));

                                            if (hour > hour_deadline || (hour == hour_deadline && minute > minute_deadline)) {
                                                salles.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(github.yvesbenabou.firebase.Status.FREE.ordinal());
                                                reservationsRef.child(String.valueOf(reservationKey.charAt(1))).child(reservationKey).setValue(" ");
                                            }
                                        } catch (ClassCastException e) {}
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "Une erreur est survenue à l'acquisition des données.");
                        }
                    }));
                } catch (Exception e) { }
            }

            if (UpdateFetcher.b_LiberationTime || !UpdateFetcher.b_LiberationDate) { //raffraichir la base avec une mise à jour d'ADE
                LiberationDate.setValue(date);
                LiberationTime.setValue(time);
            }

            if (UpdateFetcher.b_ADETime || !UpdateFetcher.b_ADEDate) { //raffraichir la base avec une mise à jour d'ADE
                MainActivity.ADE_refresh();
                ADEDate.setValue(date);
                if(minute >= 30) ADETime.setValue(hour + ":30");
                else ADETime.setValue(hour + ":00");
            }

            try {
                Thread.sleep(15000);  // Pause de 15 secondes
            } catch (InterruptedException e) {}

        }
        return null;
    }
}



