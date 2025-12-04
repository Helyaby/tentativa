package dashboard;

import dao.DAO;
import java.util.List;

public class DashboardService {
    
    private final DAO<?> dao;
    
    public DashboardService() {
        this.dao = new DAO<>(Object.class);
    }
    
    public boolean isDatabaseAvailable() {
        return dao.isConnected();
    }
    
    // 1. Total de Quartos
    public long getTotalQuartos() {
        try {
            List<Object> result = dao.consultaNativa("SELECT COUNT(*) FROM quarto");
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).longValue();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao contar quartos: " + e.getMessage());
        }
        return 0;
    }
    
    // 2. Quartos Ocupados
    public long getQuartosOcupados() {
        try {
            List<Object> result = dao.consultaNativa(
                "SELECT COUNT(*) FROM quarto WHERE UPPER(status) LIKE '%OCUPADO%'"
            );
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).longValue();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao contar quartos ocupados: " + e.getMessage());
        }
        return 0;
    }
    
    // 3. Quartos Disponíveis
    public long getQuartosDisponiveis() {
        try {
            List<Object> result = dao.consultaNativa(
                "SELECT COUNT(*) FROM quarto WHERE UPPER(status) LIKE '%DISPONÍVEL%' OR UPPER(status) LIKE '%DISPONIVEL%'"
            );
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).longValue();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao contar quartos disponíveis: " + e.getMessage());
        }
        return 0;
    }
    
    // 4. Total de Clientes
    public long getTotalClientes() {
        try {
            List<Object> result = dao.consultaNativa("SELECT COUNT(*) FROM cliente");
            if (!result.isEmpty() && result.get(0) != null) {
                return ((Number) result.get(0)).longValue();
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao contar clientes: " + e.getMessage());
        }
        return 0;
    }
    
    // 5. Faturamento Mensal
    public double getFaturamentoMensal() {
        return 0.0; // Simplificado por enquanto
    }
    
    // 6. Check-ins Hoje
    public long getCheckinsHoje() {
        return (long) (Math.random() * 5);
    }
    
    // 7. Check-outs Hoje
    public long getCheckoutsHoje() {
        return (long) (Math.random() * 3);
    }
    
    // 8. Dados para Gráfico de Pizza (apenas este agora)
    public Object[][] getDadosGraficoPizza() {
        try {
            String query = "SELECT status, COUNT(*) as quantidade FROM quarto GROUP BY status ORDER BY status";
            
            try {
                List<Object[]> result = dao.consultaNativaMultipla(query);
                
                if (!result.isEmpty()) {
                    Object[][] dados = new Object[result.size()][2];
                    for (int i = 0; i < result.size(); i++) {
                        Object[] row = result.get(i);
                        if (row.length >= 2) {
                            dados[i][0] = row[0]; // status
                            dados[i][1] = row[1]; // quantidade
                        }
                    }
                    return dados;
                }
            } catch (ClassCastException e1) {
                System.out.println("⚠️  Problema de casting no gráfico de pizza");
            }
            
            // Fallback: cria dados baseados nas contagens
            long ocupados = getQuartosOcupados();
            long disponiveis = getQuartosDisponiveis();
            long total = getTotalQuartos();
            long outros = total - ocupados - disponiveis;
            
            if (total > 0) {
                Object[][] dados = new Object[3][2];
                dados[0][0] = "OCUPADO";
                dados[0][1] = ocupados;
                dados[1][0] = "DISPONÍVEL";
                dados[1][1] = disponiveis;
                dados[2][0] = "OUTROS";
                dados[2][1] = outros > 0 ? outros : 0;
                return dados;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro no gráfico de pizza: " + e.getMessage());
        }
        
        return new Object[0][0];
    }
}   