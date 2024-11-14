package github.yvesbenabou.firebase.libs;

import android.os.Bundle;
//import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import github.yvesbenabou.firebase.R;


public class WebPageSourceFetcherWebView extends AppCompatActivity {
    public interface HtmlFetchListener {
        void onHtmlFetched(String html);
    }

    private HtmlFetchListener htmlFetchListener;

    public void setHtmlFetchListener(HtmlFetchListener listener) {
        this.htmlFetchListener = listener;
    }


    private static String pageHTML = "";
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

        // Utiliser un **WebViewClient pour exécuter du JavaScript une fois que la page est chargée
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
                //TODO + attendre t1 ms
                webView.evaluateJavascript(
                        "document.querySelector(\"button.x-btn-text\").click();", // Choisir 2024-2025
                        null
                );

                //TODO attendre t2 ms (t2 > t1)
                webView.evaluateJavascript( //Cliquer sur "Salles"
                        "const element = Array.from(document.querySelectorAll(\"div\")).find(div => div.textContent.trim() === \"Salles\");\n" +
                                "if (element) {\n" +
                                "    element.dispatchEvent(new MouseEvent(\"mousedown\", { bubbles: true, cancelable: true }));\n" +
                                "}", // Choisir 2024-2025
                        null
                );

                // Récupérer l'URL

                webView.evaluateJavascript(
                        "document.querySelector('[aria-describedby=\"x-auto-42\"]').click()",
                        null
                );// Appyer sur le bouton d'exportation

                webView.evaluateJavascript(
                        "const element = Array.from(document.querySelectorAll(\"button\")).find(div => div.textContent.trim() === \"Générer URL\");\n" +
                                "if (element) {\n" +
                                "    element.dispatchEvent(new MouseEvent(\"click\", { bubbles: true, cancelable: true }));\n" +
                                "}",
                        null
                ); //Cliquer sur Générer l'URL

                webView.evaluateJavascript(
                "(function() { " +
                        "const linkElement = document.querySelector('#logdetail a');" +
                        "return linkElement ? linkElement.href : null;" +
                        "})()",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            if (value != null && !value.equals("null")) {
                                // value contient l'URL en tant que chaîne de caractères
                                String url = value.replace("\"", ""); // Supprimer les guillemets
                            }
                        }
                    }
                ); //On récupère 'url dans la variable url



                /**
                webView.evaluateJavascript( //Ouvrir l'onglet 01-Enseignement
                        "const element = Array.from(document.querySelectorAll(\"div\")).find(div => div.textContent.trim() === \"01-Enseignement\");\n" +
                                "if (element) {\n" +
                                "    element.dispatchEvent(new MouseEvent(\"dblclick\", { bubbles: true, cancelable: true }));\n" +
                                "}", // Choisir 2024-2025
                        null
                );

                webView.evaluateJavascript( //Ouvrir l'onglet 01-Amphis
                        "const element = Array.from(document.querySelectorAll(\"div\")).find(div => div.textContent.trim() === \"01-Amphis\");\n" +
                                "if (element) {\n" +
                                "    element.dispatchEvent(new MouseEvent(\"dblclick\", { bubbles: true, cancelable: true }));\n" +
                                "}", // Choisir 2024-2025
                        null
                );

                webView.evaluateJavascript( //Cliquer sur 110
                        "const element = Array.from(document.querySelectorAll(\"div\")).find(div => div.textContent.trim() === \"0110\");\n" +
                                "if (element) {\n" +
                                "    element.dispatchEvent(new MouseEvent(\"mousedown\", { bubbles: true, cancelable: true }));\n" +
                                "}", // Choisir 2024-2025
                        null
                );

                //Récupération du code de la page

                webView.evaluateJavascript(
                        "(function() { return document.documentElement.outerHTML; })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                pageHTML = html;
                                if (htmlFetchListener != null) {
                                    htmlFetchListener.onHtmlFetched(pageHTML);
                                }
                            }
                        }
                );
             **/
            }
        });
    }
}
