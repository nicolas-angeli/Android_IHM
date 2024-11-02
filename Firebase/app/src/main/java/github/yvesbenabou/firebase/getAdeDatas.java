package github.yvesbenabou.firebase;
import github.yvesbenabou.firebase.libs.*;

public class getAdeDatas extends MainActivity{
    public MainActivity.Liste_Salles getAdeData(String time) {
        /*
        TODO : time format
        time = "JJMMAAAA,hh:mm"
         */
        // Faire une requête sur ADE our récupérer les données des salles
        MainActivity.Liste_Salles adeDatas = new MainActivity.Liste_Salles();



        // Exemple d'utilisation de la méthode
        String url = "https://planif.esiee.fr/direct/"; // Remplacez par l'URL réelle
        String username = "lecteur1"; // Remplacez par votre nom d'utilisateur
        String password = ""; // Remplacez par votre mot de passe
        String year = "2024-2025-ESIEE PARIS"; // Remplacez par l'année que vous souhaitez sélectionner


        // Récupérer le code source de la page
        String pageSource = WebPageSourceFetcher.fetchHtmlSource(url, username, password, year);

        // Afficher le code source
        System.out.println("Code source de la page :");
        System.out.println(pageSource);

        for (Salle s : adeDatas.getList()) {
            System.out.println("Salle : " + s.getNum()+ " state : "+ s.getState() );
        }

        return adeDatas;

    }
}
