package tela.main;

// Importa a classe base do JavaFX para iniciar a aplicação
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 🚀 App.java
 * --------------------------------------------------------
 * Classe principal do sistema "Hotel New Royale".
 *
 * Esta é a porta de entrada da aplicação JavaFX.
 * Agora inicia pela tela de login antes do sistema principal.
 */
public class App extends Application {

    /**
     * Este método é automaticamente chamado ao iniciar a aplicação.
     * Ele configura a janela principal (Stage), a cena (Scene),
     * carrega a interface FXML e aplica a folha de estilo CSS.
     *
     * @param primaryStage A janela principal da aplicação
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 🔐 AGORA CARREGA A TELA DE LOGIN PRIMEIRO
        Parent root = FXMLLoader.load(getClass().getResource("/telas/view/TelaLogin.fxml"));

        // Cria uma cena com o layout carregado
        Scene scene = new Scene(root);

        // Adiciona o arquivo CSS para estilizar os componentes da interface
        scene.getStylesheets().add(getClass().getResource("/Css/main.css").toExternalForm());

        // Define o título da janela de login
        primaryStage.setTitle("Hotel New Royale - Login");

        // Define a cena que será exibida dentro da janela
        primaryStage.setScene(scene);

        // Remove o maximizado para a tela de login
        // primaryStage.setMaximized(true); // ❌ COMENTADO para a tela de login

        // Exibe a janela de login na tela
        primaryStage.show();
    }

    /**
     * Método main, chamado quando o programa é executado.
     * Ele chama o método launch(), que inicializa o JavaFX.
     *
     * @param args Argumentos passados por linha de comando (se houver)
     */
    public static void main(String[] args) {
        launch(args); // Inicia a aplicação JavaFX (chama o start())
    }
}