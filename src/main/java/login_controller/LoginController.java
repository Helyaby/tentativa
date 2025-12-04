package login_controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    // Credenciais padrão (em um sistema real, isso viria de um banco de dados)
    private final String USUARIO_CORRETO = "admin";
    private final String SENHA_CORRETA = "admin123";

    @FXML
    private void fazerLogin() {
        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Erro de Login", "Por favor, preencha todos os campos.");
            return;
        }

        if (usuario.equals(USUARIO_CORRETO) && senha.equals(SENHA_CORRETA)) {
            abrirSistemaPrincipal();
            fecharTelaLogin();
        } else {
            mostrarAlerta("Erro de Login", "Usuário ou senha incorretos!");
            limparCampos();
        }
    }

    private void abrirSistemaPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/telas/view/MainLayout.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema Hotel New Royale");
            stage.setMaximized(true); // Abre em tela cheia
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o sistema principal.");
        }
    }

    private void fecharTelaLogin() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    private void limparCampos() {
        txtSenha.clear();
        txtUsuario.requestFocus();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}