package boasvindas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class BoasVindasController implements Initializable {

    @FXML private Label labelUsuario;
    @FXML private Label labelData;
    @FXML private Label labelHora;

    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ✅ VERIFICAÇÃO DE NULL ADICIONADA
        if (labelData == null) {
            System.err.println("❌ AVISO: labelData é null no BoasVindasController");
        }
        if (labelHora == null) {
            System.err.println("❌ AVISO: labelHora é null no BoasVindasController");
        }
        if (labelUsuario == null) {
            System.err.println("❌ AVISO: labelUsuario é null no BoasVindasController");
        }

        iniciarRelogio();
        carregarInformacoesFuncionario();
    }

    private void iniciarRelogio() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                atualizarDataHora();
            }
        }, 0, 1000);
    }

    private void atualizarDataHora() {
        LocalDateTime agora = LocalDateTime.now();
        
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        String dataFormatada = agora.format(formatoData);
        String horaFormatada = agora.format(formatoHora);
        
        javafx.application.Platform.runLater(() -> {
            // ✅ VERIFICAÇÃO ANTES DE ATUALIZAR
            if (labelData != null) {
                labelData.setText(dataFormatada);
            }
            if (labelHora != null) {
                labelHora.setText(horaFormatada);
            }
        });
    }

    private void carregarInformacoesFuncionario() {
        // ✅ DADOS EXEMPLO MAIS CLAROS
        String nomeFuncionario = "Usuário"; // Exemplo
        String cargo = "Colaborador"; // Exemplo
        
        if (labelUsuario != null) {
            labelUsuario.setText(nomeFuncionario + " - " + cargo);
        } else {
            System.err.println("❌ labelUsuario é null - não foi possível definir texto");
        }
        
        // Exemplo de como seria com dados reais:
        // if (Sessao.getFuncionarioLogado() != null) {
        //     labelUsuario.setText(Sessao.getFuncionarioLogado().getNome() + " - " + 
        //                         Sessao.getFuncionarioLogado().getCargo());
        // }
    }

    @FXML
    private void abrirSistemaReservas() {
        carregarTela("/reservas/SistemaReservas.fxml");
    }

    @FXML
    private void abrirGestaoQuartos() {
        carregarTela("/quartos/GestaoQuartos.fxml");
    }

    @FXML
    private void abrirGestaoClientes() {
        carregarTela("/clientes/GestaoClientes.fxml");
    }

    @FXML
    private void abrirDashboard() {
        carregarTela("/dashboard/DashboardPrincipal.fxml");
    }

    private void carregarTela(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) labelUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao carregar tela: " + fxmlPath);
            
            // ✅ FEEDBACK VISUAL PARA O USUÁRIO
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Não foi possível carregar a tela");
                alert.setContentText("Erro ao carregar: " + fxmlPath + "\n\n" + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    public void finalizar() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}