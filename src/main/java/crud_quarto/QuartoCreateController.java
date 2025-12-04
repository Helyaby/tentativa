package crud_quarto;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Quarto;

public class QuartoCreateController {

    @FXML private TextField txtNumero;
    @FXML private TextField txtValor;
    @FXML private TextField txtLocalizacao;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private ComboBox<String> comboTipo;
    @FXML private Spinner<Integer> spinnerNumeroQuartos;
    @FXML private TextArea txtObservacoes;
    
    private Quarto quartoEmEdicao;
    private boolean modoEdicao = false;
    private Stage dialogStage;
    private boolean salvoComSucesso = false;

    @FXML
    public void initialize() {
        comboSituacao.getItems().addAll("Disponível", "Ocupado", "Em manutenção", "Limpeza");
        comboTipo.getItems().addAll("Luxo", "Premium", "Standard", "Econômico", "Suíte");
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        spinnerNumeroQuartos.setValueFactory(valueFactory);
        
        // Configurar validação para campo de valor (apenas números)
        txtValor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                txtValor.setText(oldValue);
            }
        });
    }

    // Método para definir o stage do dialog
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Método para verificar se foi salvo com sucesso
    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }

    public void carregarQuartoParaEdicao(Quarto quarto) {
        this.quartoEmEdicao = quarto;
        this.modoEdicao = true;
        
        txtNumero.setText(quarto.getIdentificacao());
        txtValor.setText(String.valueOf(quarto.getValor()));
        txtLocalizacao.setText(quarto.getLocalizacao());
        comboTipo.setValue(quarto.getTipo());
        comboSituacao.setValue(quarto.getStatus());
        spinnerNumeroQuartos.getValueFactory().setValue(quarto.getNumeroQuartos());
        txtObservacoes.setText(quarto.getObservacoes());
    }

    private void limparEstiloErro() {
        limparBordaVermelha(txtNumero);
        limparBordaVermelha(txtValor);
        limparBordaVermelha(txtLocalizacao);
        limparBordaVermelha(comboTipo);
        limparBordaVermelha(comboSituacao);
    }

    private void colocarBordaVermelha(Control campo) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void limparBordaVermelha(Control campo) {
        campo.setStyle("");
    }

    private boolean validarCamposComVisual() {
        limparEstiloErro();

        boolean valido = true;

        if (txtNumero.getText() == null || txtNumero.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtNumero);
            valido = false;
        }

        if (txtValor.getText() == null || txtValor.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtValor);
            valido = false;
        }

        if (txtLocalizacao.getText() == null || txtLocalizacao.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtLocalizacao);
            valido = false;
        }

        if (comboTipo.getValue() == null || comboTipo.getValue().trim().isEmpty()) {
            colocarBordaVermelha(comboTipo);
            valido = false;
        }

        if (comboSituacao.getValue() == null || comboSituacao.getValue().trim().isEmpty()) {
            colocarBordaVermelha(comboSituacao);
            valido = false;
        }

        return valido;
    }

    @FXML
    private void salvarQuarto() {
        try {
            if (!validarCamposComVisual()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Campos Obrigatórios");
                alerta.setHeaderText("Preencha os campos obrigatórios destacados em vermelho.");
                alerta.setContentText("Os campos com borda vermelha são obrigatórios e não podem ficar vazios.");
                alerta.showAndWait();
                return;
            }

            String numero = txtNumero.getText();
            double valor = Double.parseDouble(txtValor.getText());
            String local = txtLocalizacao.getText();
            String situacao = comboSituacao.getValue();
            String tipo = comboTipo.getValue();
            int numeroQuartos = spinnerNumeroQuartos.getValue();
            String obs = txtObservacoes.getText();

            if (modoEdicao && quartoEmEdicao != null) {
                quartoEmEdicao.setIdentificacao(numero);
                quartoEmEdicao.setValor(valor);
                quartoEmEdicao.setLocalizacao(local);
                quartoEmEdicao.setStatus(situacao);
                quartoEmEdicao.setTipo(tipo);
                quartoEmEdicao.setNumeroQuartos(numeroQuartos);
                quartoEmEdicao.setObservacoes(obs);

                new DAO<>(Quarto.class).atualizarTransacional(quartoEmEdicao);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Edição de Quarto");
                alerta.setHeaderText("Sucesso");
                alerta.setContentText("Quarto atualizado com sucesso!");
                alerta.showAndWait();

            } else {
                // USANDO O CONSTRUTOR ATUALIZADO
                Quarto novo = new Quarto(numero, local, tipo, situacao, numeroQuartos, obs, valor);
                new DAO<>(Quarto.class).incluirTransacional(novo);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Cadastro de Quarto");
                alerta.setHeaderText("Sucesso");
                alerta.setContentText("Quarto salvo com sucesso!");
                alerta.showAndWait();
            }

            salvoComSucesso = true;
            
            // Fecha o dialog
            if (dialogStage != null) {
                dialogStage.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText("Falha ao salvar quarto");
            alerta.setContentText("Erro: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void limparCampos() {
        txtNumero.clear();
        txtValor.clear();
        txtLocalizacao.clear();
        comboSituacao.setValue(null);
        comboTipo.setValue(null);
        spinnerNumeroQuartos.getValueFactory().setValue(1);
        txtObservacoes.clear();
        
        modoEdicao = false;
        quartoEmEdicao = null;
    }

    @FXML
    private void voltar() {
        // Fecha o dialog sem salvar
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}