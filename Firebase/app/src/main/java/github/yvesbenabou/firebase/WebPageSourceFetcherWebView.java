package github.yvesbenabou.firebase;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;



public class WebPageSourceFetcherWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser WebView
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // Activer JavaScript
        webView.setWebViewClient(new WebViewClient()); // Pour garder la navigation dans l'application

        // Charger l'URL de la page contenant le formulaire
        webView.loadUrl("https://planif.esiee.fr/direct/");

        // Utiliser un WebViewClient pour exécuter du JavaScript une fois que la page est chargée
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Exécuter du JavaScript pour remplir les champs du formulaire et soumettre
                webView.evaluateJavascript(
                        "document.getElementById('x-auto-13-input').value = 'lecteur1';" + // Remplir le login
                                "document.getElementById('x-auto-14-input').value = '';" + // Remplir le mot de passe
                                "document.querySelector(\"button[type='button']\").click();", // Cliquer sur le bouton "Ok"
                        null
                );
                //TODO + attendre t ms
                webView.evaluateJavascript(
                        "document.getElementById('x-auto-30').click();", // Choisir 2024-2025
                        null
                );
            }
        });
    }
}
