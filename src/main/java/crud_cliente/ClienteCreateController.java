package crud_cliente;

import java.time.LocalDate;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Cliente;

public class ClienteCreateController {

    @FXML private TextField txtCpf;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtRg;
    @FXML private TextField txtSexo;
    @FXML private TextField txtCep;
    @FXML private DatePicker dateNascimento;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtCidade;

    private Cliente clienteEmEdicao;
    private boolean modoEdicao = false;
    private Stage dialogStage;
    private ClienteListController clienteListController;
    private boolean salvoComSucesso = false;

    // Método para definir o stage do dialog
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Método para verificar se foi salvo com sucesso
    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }

    public void setClienteListController(ClienteListController controller) {
        this.clienteListController = controller;
    }

    @FXML
    public void initialize() {
        // Configurações iniciais se necessário
    }

    public void carregarClienteParaEdicao(Cliente cliente) {
        this.clienteEmEdicao = cliente;
        this.modoEdicao = true;
        
        txtCpf.setText(cliente.getCpf());
        txtNome.setText(cliente.getNome());
        txtEmail.setText(cliente.getEmail());
        txtRg.setText(cliente.getRg());
        txtSexo.setText(cliente.getSexo());
        txtCep.setText(cliente.getCep());
        dateNascimento.setValue(cliente.getDataNascimento());
        txtTelefone.setText(cliente.getTelefone());
        txtCidade.setText(cliente.getCidade());
    }

    private void limparEstiloErro() {
        Control[] campos = {txtCpf, txtNome, txtEmail, txtRg, txtSexo, txtCep, dateNascimento, txtTelefone, txtCidade};
        for (Control campo : campos) {
            limparBordaVermelha(campo);
        }
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

        if (txtCpf.getText() == null || txtCpf.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtCpf);
            valido = false;
        }
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtNome);
            valido = false;
        }
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            colocarBordaVermelha(txtEmail);
            valido = false;
        }

        return valido;
    }

    @FXML
    private void salvarCliente() {
        try {
            if (!validarCamposComVisual()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Campos Obrigatórios");
                alerta.setHeaderText("Preencha os campos obrigatórios destacados em vermelho.");
                alerta.setContentText("CPF, Nome e Email são obrigatórios.");
                alerta.showAndWait();
                return;
            }

            String cpf = txtCpf.getText();
            String nome = txtNome.getText();
            String email = txtEmail.getText();
            String rg = txtRg.getText();
            String sexo = txtSexo.getText();
            String cep = txtCep.getText();
            LocalDate dataNascimento = dateNascimento.getValue();
            String telefone = txtTelefone.getText();
            String cidade = txtCidade.getText();

            if (modoEdicao && clienteEmEdicao != null) {
                clienteEmEdicao.setCpf(cpf);
                clienteEmEdicao.setNome(nome);
                clienteEmEdicao.setEmail(email);
                clienteEmEdicao.setRg(rg);
                clienteEmEdicao.setSexo(sexo);
                clienteEmEdicao.setCep(cep);
                clienteEmEdicao.setDataNascimento(dataNascimento);
                clienteEmEdicao.setTelefone(telefone);
                clienteEmEdicao.setCidade(cidade);

                new DAO<>(Cliente.class).atualizarTransacional(clienteEmEdicao);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Edição de Cliente");
                alerta.setHeaderText("Sucesso");
                alerta.setContentText("Cliente atualizado com sucesso!");
                alerta.showAndWait();
            } else {
                Cliente novo = new Cliente(cpf, nome, email, rg, sexo, cep, dataNascimento, telefone, cidade);
                new DAO<>(Cliente.class).incluirTransacional(novo);

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Cadastro de Cliente");
                alerta.setHeaderText("Sucesso");
                alerta.setContentText("Cliente salvo com sucesso!");
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
            alerta.setHeaderText("Falha ao salvar cliente");
            alerta.setContentText("Erro: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void limparCampos() {
        txtCpf.clear();
        txtNome.clear();
        txtEmail.clear();
        txtRg.clear();
        txtSexo.clear();
        txtCep.clear();
        dateNascimento.setValue(null);
        txtTelefone.clear();
        txtCidade.clear();
        
        modoEdicao = false;
        clienteEmEdicao = null;
    }

    @FXML
    private void voltar() {
        // Fecha o dialog sem salvar
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}