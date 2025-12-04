// crud_ocupacao/OcupacaoController.java
package crud_ocupacao;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import model.Cliente;
import model.Quarto;

public class OcupacaoController {

    @FXML private ComboBox<Quarto> comboQuarto;
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private DatePicker dateCheckIn;
    @FXML private DatePicker dateCheckOut;
    @FXML private TextField txtNumeroHospedes;
    @FXML private TextArea txtObservacoes;

    @FXML
    public void initialize() {
        configurarCombos();
        dateCheckIn.setValue(LocalDate.now());
    }

    private void configurarCombos() {
        // Carrega quartos disponíveis
        List<Quarto> quartos = new DAO<>(Quarto.class).obterTodos(100, 0);
        comboQuarto.getItems().addAll(quartos);
        
        comboQuarto.setConverter(new StringConverter<Quarto>() {
            @Override
            public String toString(Quarto quarto) {
                if (quarto == null) return "Selecione um quarto";
                String ocupante = quarto.getCliente() != null ? " (Ocupado)" : " (Livre)";
                return quarto.getIdentificacao() + " - " + quarto.getTipo() + ocupante;
            }
            
            @Override
            public Quarto fromString(String string) {
                return null;
            }
        });

        // Carrega clientes
        List<Cliente> clientes = new DAO<>(Cliente.class).obterTodos(100, 0);
        comboCliente.getItems().addAll(clientes);
        
        comboCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                if (cliente == null) return "Selecione um cliente";
                return cliente.getNome() + " - " + cliente.getCpf();
            }
            
            @Override
            public Cliente fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void salvarOcupacao() {
        try {
            Quarto quarto = comboQuarto.getValue();
            Cliente cliente = comboCliente.getValue();
            
            if (quarto == null || cliente == null) {
                mostrarAlerta("Selecione um quarto e um cliente");
                return;
            }

            // Verifica se o quarto já está ocupado
            if (quarto.getCliente() != null) {
                mostrarAlerta("Este quarto já está ocupado por: " + quarto.getCliente().getNome());
                return;
            }

            // Atualiza o quarto com o cliente ocupante
            quarto.setCliente(cliente);
            quarto.setStatus("Ocupado");
        
            
            // Atualiza no banco
            new DAO<>(Quarto.class).atualizarTransacional(quarto);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sucesso");
            alerta.setHeaderText("Check-in realizado");
            alerta.setContentText("Cliente " + cliente.getNome() + " alocado no quarto " + quarto.getIdentificacao());
            alerta.showAndWait();

            limparCampos();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao realizar check-in: " + e.getMessage());
        }
    }

    @FXML
    private void realizarCheckout() {
        try {
            Quarto quarto = comboQuarto.getValue();
            
            if (quarto == null || quarto.getCliente() == null) {
                mostrarAlerta("Selecione um quarto ocupado");
                return;
            }

            // Libera o quarto
            String clienteNome = quarto.getCliente().getNome();
            quarto.setCliente(null);
            quarto.setStatus("Livre");
            quarto.setObservacoes("Check-out: " + LocalDate.now() + "\n" + txtObservacoes.getText());
            
            new DAO<>(Quarto.class).atualizarTransacional(quarto);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sucesso");
            alerta.setHeaderText("Check-out realizado");
            alerta.setContentText("Quarto " + quarto.getIdentificacao() + " liberado. Cliente: " + clienteNome);
            alerta.showAndWait();

            limparCampos();
            configurarCombos(); // Recarrega os combos
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao realizar check-out: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Atenção");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    private void limparCampos() {
        comboQuarto.setValue(null);
        comboCliente.setValue(null);
        dateCheckIn.setValue(LocalDate.now());
        dateCheckOut.setValue(null);
        txtNumeroHospedes.clear();
        txtObservacoes.clear();
    }

    @FXML
    private void voltar() {
        try {
            Node tela = FXMLLoader.load(getClass().getResource("/telas/view/TelaDashboard.fxml"));
            StackPane painel = (StackPane) comboQuarto.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}