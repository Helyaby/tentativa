package dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // Labels para as métricas
    @FXML private Label labelTotalQuartos;
    @FXML private Label labelQuartosOcupados;
    @FXML private Label labelQuartosDisponiveis;
    @FXML private Label labelTotalClientes;
    @FXML private Label labelFaturamentoMensal;
    @FXML private Label labelTaxaOcupacao;
    @FXML private Label labelCheckinsHoje;
    @FXML private Label labelCheckoutsHoje;

    // Apenas o gráfico de pizza
    @FXML private PieChart pieChartOcupacao;

    private DashboardService dashboardService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dashboardService = new DashboardService();
        carregarDadosDashboard();
    }

    private void carregarDadosDashboard() {
        // Carrega em uma thread separada para não travar a interface
        new Thread(() -> {
            try {
                // Verifica se o banco está disponível
                if (dashboardService.isDatabaseAvailable()) {
                    carregarDadosReais();
                } else {
                    carregarDadosExemplo();
                }
            } catch (Exception e) {
                System.err.println("❌ Erro ao carregar dashboard: " + e.getMessage());
                Platform.runLater(this::carregarDadosExemplo);
            }
        }).start();
    }

    private void carregarDadosReais() {
        try {
            // Busca dados do banco
            long totalQuartos = dashboardService.getTotalQuartos();
            long quartosOcupados = dashboardService.getQuartosOcupados();
            long quartosDisponiveis = dashboardService.getQuartosDisponiveis();
            long totalClientes = dashboardService.getTotalClientes();
            double faturamentoMensal = dashboardService.getFaturamentoMensal();
            long checkinsHoje = dashboardService.getCheckinsHoje();
            long checkoutsHoje = dashboardService.getCheckoutsHoje();
            
            // Calcula taxa de ocupação
            double taxaOcupacao = totalQuartos > 0 ? 
                (quartosOcupados * 100.0) / totalQuartos : 0;
            
            // Busca dados apenas para o gráfico de pizza
            Object[][] dadosPizza = dashboardService.getDadosGraficoPizza();
            
            // Atualiza a interface na thread do JavaFX
            Platform.runLater(() -> {
                atualizarLabels(
                    totalQuartos, quartosOcupados, quartosDisponiveis,
                    totalClientes, faturamentoMensal, taxaOcupacao,
                    checkinsHoje, checkoutsHoje
                );
                
                atualizarGraficoPizza(dadosPizza);
            });
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar dados reais: " + e.getMessage());
            Platform.runLater(this::carregarDadosExemplo);
        }
    }

    private void atualizarLabels(
        long totalQuartos, long quartosOcupados, long quartosDisponiveis,
        long totalClientes, double faturamentoMensal, double taxaOcupacao,
        long checkinsHoje, long checkoutsHoje
    ) {
        labelTotalQuartos.setText(String.valueOf(totalQuartos));
        labelQuartosOcupados.setText(String.valueOf(quartosOcupados));
        labelQuartosDisponiveis.setText(String.valueOf(quartosDisponiveis));
        labelTotalClientes.setText(String.valueOf(totalClientes));
        labelFaturamentoMensal.setText(String.format("R$ %.2f", faturamentoMensal));
        labelTaxaOcupacao.setText(String.format("%.1f%%", taxaOcupacao));
        labelCheckinsHoje.setText(String.valueOf(checkinsHoje));
        labelCheckoutsHoje.setText(String.valueOf(checkoutsHoje));
    }

    private void atualizarGraficoPizza(Object[][] dados) {
        pieChartOcupacao.getData().clear();
        
        if (dados != null && dados.length > 0) {
            boolean hasValidData = false;
            for (Object[] linha : dados) {
                if (linha.length >= 2 && linha[0] != null && linha[1] != null) {
                    String status = traduzirStatus(linha[0].toString());
                    Number quantidade = 0;
                    if (linha[1] instanceof Number) {
                        quantidade = (Number) linha[1];
                    } else {
                        try {
                            quantidade = Long.parseLong(linha[1].toString());
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                    
                    if (quantidade.doubleValue() > 0) {
                        hasValidData = true;
                        pieChartOcupacao.getData().add(
                            new PieChart.Data(status + " (" + quantidade + ")", quantidade.doubleValue())
                        );
                    }
                }
            }
            
            if (!hasValidData) {
                carregarDadosExemplo();
                return;
            }
        } else {
            // Se não houver dados, carrega exemplo
            carregarDadosExemplo();
            return;
        }
        
        // Aplica cores ao gráfico de pizza
        aplicarCoresPizza();
    }

    private String traduzirStatus(String status) {
        if (status == null) return "Desconhecido";
        
        String statusUpper = status.toUpperCase();
        if (statusUpper.contains("OCUPADO")) return "Ocupados";
        if (statusUpper.contains("DISPONIVEL") || statusUpper.contains("DISPONÍVEL")) return "Disponíveis";
        if (statusUpper.contains("MANUTENCAO") || statusUpper.contains("MANUTENÇÃO")) return "Manutenção";
        if (statusUpper.contains("RESERVADO")) return "Reservados";
        if (statusUpper.contains("LIMPEZA")) return "Limpeza";
        
        return status;
    }

    private void carregarDadosExemplo() {
        // Dados de exemplo para quando não há conexão
        labelTotalQuartos.setText("50");
        labelQuartosOcupados.setText("32");
        labelQuartosDisponiveis.setText("18");
        labelTotalClientes.setText("128");
        labelFaturamentoMensal.setText("R$ 45.280,75");
        labelTaxaOcupacao.setText("64,0%");
        labelCheckinsHoje.setText("5");
        labelCheckoutsHoje.setText("3");
        
        // Gráfico de Pizza
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Ocupados (32)", 32),
            new PieChart.Data("Disponíveis (18)", 18)
        );
        pieChartOcupacao.setData(pieData);
        
        // Aplica cores
        aplicarCoresPizza();
    }

    private void aplicarCoresPizza() {
        // Cores personalizadas para o gráfico de pizza
        if (!pieChartOcupacao.getData().isEmpty()) {
            String[] cores = {"#e74c3c", "#2ecc71", "#3498db", "#f39c12", "#9b59b6"};
            
            for (int i = 0; i < pieChartOcupacao.getData().size(); i++) {
                if (i < cores.length) {
                    // Aplica estilo via CSS
                    pieChartOcupacao.getData().get(i).getNode().setStyle(
                        "-fx-pie-color: " + cores[i] + ";"
                    );
                }
            }
        }
    }
}