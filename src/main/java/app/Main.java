package app;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Importações CORRETAS do Spark Java
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Configurar porta do Railway
            port(getPort());
            
            System.out.println("🚀 Iniciando Backend API no Railway...");
            System.out.println("📊 Conectando ao Banco Neon...");
            
            // Inicializar JPA com SEU persistence.xml original
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Newroyale");
            System.out.println("✅ Conexão com Neon estabelecida!");
            
            // Configurar CORS
            enableCORS();
            
            // Health Check
            get("/", (req, res) -> "🚀 Backend Hotel New Royale - Online!");
            
            get("/health", (req, res) -> {
                res.type("application/json");
                return "{\"status\": \"online\", \"database\": \"neon.tech\"}";
            });
            
            // Exemplo de rotas para suas entidades
            get("/quartos", (req, res) -> {
                res.type("application/json");
                // Aqui você implementa a lógica para retornar quartos
                return "{\"message\": \"Lista de quartos em desenvolvimento\"}";
            });
            
            get("/clientes", (req, res) -> {
                res.type("application/json");
                // Aqui você implementa a lógica para retornar clientes
                return "{\"message\": \"Lista de clientes em desenvolvimento\"}";
            });
            
            System.out.println("🌐 Backend API rodando na porta: " + getPort());
            System.out.println("✅ Rotas configuradas: /, /health, /quartos, /clientes");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar API: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static int getPort() {
        String portEnv = System.getenv("PORT");
        return portEnv != null ? Integer.parseInt(portEnv) : 4567;
    }
    
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });
    }
}