package github.yvesbenabou.firebase;
import github.yvesbenabou.firebase.libs.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageSourceFetcher { //provisoire
    public static String getHtmlDatas(String url) {
        StringBuilder content = new StringBuilder();

        try {
            // Création de l'objet URL
            URL urlObj = new URL(url);

            // Ouvrir une connexion à l'URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");

            // Vérifier le code de réponse HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                // Lire le contenu de la page
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }

                // Fermer le lecteur
                reader.close();
            } else {
                System.out.println("Erreur : code de réponse HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retourner le contenu de la page
        return content.toString();
    }
}

