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


        // TODO à modifier lourdement (selenium)
        String url = "https://planif.esiee.fr/direct/";
        String htmlAdeDatas = WebPageSourceFetcher.getHtmlDatas(url);

        for (Salle s : adeDatas.getList()) {
            System.out.println("Salle : " + s.getNum()+ " state : "+ s.getState() );
        }

        return adeDatas;

    }
}
