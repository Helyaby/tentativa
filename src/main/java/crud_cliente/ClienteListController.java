package crud_cliente;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import dao.DAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Cliente;

public class ClienteListController {

    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colTelefone;
    @FXML private TableColumn<Cliente, String> colCidade;

    private final ObservableList<Cliente> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colCpf.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCpf()));
        colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colTelefone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefone()));
        colCidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCidade()));

        carregarClientes();
    }

    private void carregarClientes() {
        List<Cliente> lista = new DAO<>(Cliente.class).obterTodos(100, 0);
        dados.setAll(lista);
        tableClientes.setItems(dados);
    }

    @FXML
    private void abrirCadastro() {
        abrirModalCadastro(null);
    }

    @FXML
    private void editarCliente() {
        Cliente selecionado = tableClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um cliente para editar.").showAndWait();
            return;
        }
        abrirModalCadastro(selecionado);
    }

    private void abrirModalCadastro(Cliente cliente) {
        try {
            // Carrega o FXML do cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroCliente.fxml"));
            Parent root = loader.load();

            // Obtém o controller
            ClienteCreateController controller = loader.getController();
            if (cliente != null) {
                controller.carregarClienteParaEdicao(cliente);
            }

            // Cria o stage do dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle(cliente != null ? "Editar Cliente" : "Novo Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableClientes.getScene().getWindow());
            
            // Define o stage no controller
            controller.setDialogStage(dialogStage);
            controller.setClienteListController(this);

            // Cria a cena
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Mostra o dialog e espera
            dialogStage.showAndWait();

            // Recarrega a lista se foi salvo com sucesso
            if (controller.isSalvoComSucesso()) {
                carregarClientes();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir cadastro: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void excluirCliente() {
        Cliente selecionado = tableClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Selecione um cliente para excluir.").showAndWait();
        } else {
            Alert confirmacao = new Alert(AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação de Exclusão");
            confirmacao.setHeaderText("Excluir Cliente");
            confirmacao.setContentText("Tem certeza que deseja excluir este cliente?");
            ButtonType botaoSim = new ButtonType("Sim");
            ButtonType botaoNao = new ButtonType("Não");
            confirmacao.getButtonTypes().setAll(botaoSim, botaoNao);
            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == botaoSim) {
                new DAO<>(Cliente.class).removerPorIdTransacional(selecionado.getId());
                carregarClientes();
                new Alert(AlertType.INFORMATION, "Cliente excluído com sucesso!").showAndWait();
            }
        }
    }
    
    // Método para atualizar a lista após cadastro/edição
    public void atualizarLista() {
        carregarClientes();
    }
}