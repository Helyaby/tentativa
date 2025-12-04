package crud_quarto;

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
import model.Quarto;

public class QuartoListController {

    @FXML
    private TableView<Quarto> tableQuartos;
    @FXML
    private TableColumn<Quarto, Long> colId;
    @FXML
    private TableColumn<Quarto, String> colIdentificacao;
    @FXML
    private TableColumn<Quarto, String> colLocalizacao;
    @FXML
    private TableColumn<Quarto, String> colTipo;
    @FXML
    private TableColumn<Quarto, String> colStatus;
    @FXML
    private TableColumn<Quarto, Integer> colQuartos;
    @FXML
    private TableColumn<Quarto, String> colCliente;
    @FXML
    private TableColumn<Quarto, String> colValor;

    private final ObservableList<Quarto> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ✅ VERIFICAÇÃO DE COMPONENTES FXML
        if (tableQuartos == null) {
            System.err.println("❌ ERRO CRÍTICO: tableQuartos é null!");
            mostrarErroFatal("Erro de Interface", "A tabela de quartos não foi carregada corretamente.");
            return;
        }

        // Configura colunas da tabela
        configurarColunas();
        
        // Carrega os dados com tratamento de erro
        carregarQuartos();
    }

    private void configurarColunas() {
        try {
            colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
            colIdentificacao.setCellValueFactory(
                    c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIdentificacao()));
            colLocalizacao.setCellValueFactory(
                    c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalizacao()));
            colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipo()));
            colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

            // Configura a coluna de número de quartos
            if (colQuartos != null) {
                colQuartos.setCellValueFactory(
                        c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNumeroQuartos()));
            }

            // Coluna para cliente
            if (colCliente != null) {
                colCliente.setCellValueFactory(c -> {
                    Cliente cliente = c.getValue().getCliente();
                    String nomeCliente = (cliente != null) ? cliente.getNome() : "Livre";
                    return new javafx.beans.property.SimpleStringProperty(nomeCliente);
                });
            }

            // Configura a coluna de valor
            if (colValor != null) {
                colValor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                    String.format("R$ %.2f", c.getValue().getValor())
                ));
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao configurar colunas: " + e.getMessage());
            mostrarErro("Erro de Configuração", "Erro ao configurar colunas da tabela: " + e.getMessage());
        }
    }

    private void carregarQuartos() {
        try {
            System.out.println("🔄 Tentando carregar quartos...");
            
            if (!DAO.isDatabaseAvailable()) {
                throw new IllegalStateException("Banco de dados não disponível");
            }
            
            DAO<Quarto> dao = new DAO<>(Quarto.class);
            List<Quarto> lista = dao.obterTodos(100, 0);
            
            dados.setAll(lista);
            tableQuartos.setItems(dados);
            
            System.out.println("✅ " + lista.size() + " quartos carregados com sucesso!");
            
        } catch (IllegalStateException e) {
            // Erro de conexão com o banco
            System.err.println("❌ Erro de conexão: " + e.getMessage());
            
            String mensagem = "Não foi possível conectar ao banco de dados.\n\n" +
                             "Verifique:\n" +
                             "• Sua conexão com a internet\n" +
                             "• Se o servidor Neon está online\n" +
                             "• Suas credenciais de acesso\n\n" +
                             "A aplicação funcionará em modo offline.";
            
            mostrarErro("Erro de Conexão", mensagem);
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar quartos: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao carregar lista de quartos: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCadastro() {
        try {
            // Carrega o FXML do cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroQuarto.fxml"));
            Parent root = loader.load();

            // Obtém o controller
            QuartoCreateController controller = loader.getController();

            // Cria o stage do dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastro de Quarto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableQuartos.getScene().getWindow());
            
            // Define o stage no controller
            controller.setDialogStage(dialogStage);

            // Cria a cena
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Mostra o dialog e espera
            dialogStage.showAndWait();

            // Recarrega a lista se foi salvo com sucesso
            if (controller.isSalvoComSucesso()) {
                carregarQuartos();
            }

        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir cadastro: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro", "Erro ao abrir tela de cadastro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado ao abrir cadastro: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro Inesperado", "Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void editarQuarto() {
        Quarto selecionado = tableQuartos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um quarto para editar.");
            return;
        }

        try {
            // Carrega o FXML do cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroQuarto.fxml"));
            Parent root = loader.load();

            // Obtém o controller
            QuartoCreateController controller = loader.getController();
            
            // Carrega o quarto para edição
            controller.carregarQuartoParaEdicao(selecionado);

            // Cria o stage do dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Quarto - " + selecionado.getIdentificacao());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableQuartos.getScene().getWindow());
            
            // Define o stage no controller
            controller.setDialogStage(dialogStage);

            // Cria a cena
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Mostra o dialog e espera
            dialogStage.showAndWait();

            // Recarrega a lista se foi salvo com sucesso
            if (controller.isSalvoComSucesso()) {
                carregarQuartos();
            }

        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir edição: " + e.getMessage());
            mostrarErro("Erro", "Erro ao carregar tela de edição: " + e.getMessage());
        }
    }   

    @FXML
    private void excluirQuarto() {
        Quarto selecionado = tableQuartos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um quarto para excluir.");
            return;
        }

        // ✅ CONFIRMAÇÃO MAIS SEGURA
        Alert confirmacao = new Alert(AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Exclusão");
        confirmacao.setHeaderText("Excluir Quarto: " + selecionado.getIdentificacao());
        confirmacao.setContentText("Tem certeza que deseja excluir este quarto?\nEsta ação não pode ser desfeita.");
        
        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                new DAO<>(Quarto.class).removerPorIdTransacional(selecionado.getId());
                carregarQuartos();
                mostrarInfo("Sucesso", "Quarto excluído com sucesso!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao excluir quarto: " + e.getMessage());
                e.printStackTrace();
                mostrarErro("Erro", "Erro ao excluir quarto: " + e.getMessage());
            }
        }
    }

    // ✅ MÉTODOS AUXILIARES PARA FEEDBACK
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErroFatal(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText("Erro Crítico");
        alert.setContentText(mensagem + "\n\nA aplicação pode não funcionar corretamente.");
        alert.showAndWait();
    }
}