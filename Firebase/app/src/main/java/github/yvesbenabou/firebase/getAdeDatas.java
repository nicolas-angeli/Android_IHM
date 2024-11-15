package github.yvesbenabou.firebase;
import android.util.Log;
import github.yvesbenabou.firebase.libs.*;

public class getAdeDatas extends MainActivity{
    String pageSource;

    public MainActivity.Liste_Salles getAdeData(String time) {

        /*
        TODO : time format
        time = "JJMMAAAA,hh:mm"
         */
        MainActivity.Liste_Salles adeDatas = new MainActivity.Liste_Salles();

        String username = "lecteur1"; // Remplacez par votre nom d'utilisateur
        String password = ""; // Remplacez par votre mot de passe
        String year = "2024-2025-ESIEE PARIS"; // Remplacez par l'année que vous souhaitez sélectionner

        //CalendarFetcher.updateRoomStates();

        // Récupérer le code source de la page
        /*
        WebPageSourceFetcherWebView webViewFetcher = new WebPageSourceFetcherWebView();
        webViewFetcher.setHtmlFetchListener(new WebPageSourceFetcherWebView.HtmlFetchListener() {
            @Override
            public void onHtmlFetched(String html) {
                // html contient le code source de la page
                // Log.d("HTML Content", html);
                pageSource = html;
            }
        });
        */

        //Traitement des données html :

        //adeDatas = parseHtmlDatas.parsing(pageSource, time);

        // Afficher le code source
        //System.out.println("Code source de la page :");
        //System.out.println(pageSource);

        for (Salle s : adeDatas.getList()) {
            System.out.println("Salle : " + s.getNum()+ " state : "+ s.getState() );
        }

        return adeDatas;
    }
}
