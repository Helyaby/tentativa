package dao;

import java.util.List;
import jakarta.persistence.*;

public class DAO<E> implements AutoCloseable {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private Class<E> classe;
    private static boolean factoryInitialized = false;

    static {
        initializeEntityManagerFactory();
    }

    private static void initializeEntityManagerFactory() {
        try {
            System.out.println("🔄 Tentando criar EntityManagerFactory...");
            emf = Persistence.createEntityManagerFactory("Newroyale");
            factoryInitialized = true;
            System.out.println("✅ EntityManagerFactory criado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ FALHA CRÍTICA: Não foi possível criar EntityManagerFactory");
            System.err.println("🔧 Causa: " + e.getMessage());
            factoryInitialized = false;
            
            // Não lança exceção para permitir que a aplicação inicie sem banco
            System.err.println("⚠️  Aplicação funcionará em modo offline");
        }
    }

    public DAO(Class<E> classe) {
        this.classe = classe;
        
        if (!factoryInitialized || emf == null) {
            throw new IllegalStateException(
                "Sistema de banco de dados não disponível. " +
                "Verifique:\n" +
                "• Sua conexão com a internet\n" + 
                "• As credenciais do banco de dados\n" +
                "• Se o servidor Neon está online"
            );
        }
        
        if (!emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory está fechado");
        }
        
        em = emf.createEntityManager();
        System.out.println("✅ EntityManager criado para: " + classe.getSimpleName());
    }

    // === VERIFICAÇÃO DE CONEXÃO ===
    public static boolean isDatabaseAvailable() {
        return factoryInitialized && emf != null && emf.isOpen();
    }

        // === NOVOS MÉTODOS PARA DASHBOARD ===
    
    // Método para verificar se esta instância do DAO está conectada
    public boolean isConnected() {
        return em != null && em.isOpen();
    }
    
    // Método para consulta nativa (retorna lista de objetos)
    public List<Object> consultaNativa(String sql, Object... parametros) {
        if (em == null) {
            System.err.println("⚠️  Tentando consulta nativa em modo offline: " + sql);
            return java.util.Collections.emptyList();
        }
        
        try {
            Query query = em.createNativeQuery(sql);
            for (int i = 0; i < parametros.length; i++) {
                query.setParameter(i + 1, parametros[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("⚠️  Erro na consulta nativa: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
    
    // Método para consulta nativa que retorna lista de arrays de objetos (para múltiplas colunas)
    public List<Object[]> consultaNativaMultipla(String sql, Object... parametros) {
        if (em == null) {
            System.err.println("⚠️  Tentando consulta nativa multipla em modo offline: " + sql);
            return java.util.Collections.emptyList();
        }
        
        try {
            Query query = em.createNativeQuery(sql);
            for (int i = 0; i < parametros.length; i++) {
                query.setParameter(i + 1, parametros[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("⚠️  Erro na consulta nativa multipla: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // === TRANSACTION MANAGEMENT ===
    public DAO<E> abrirTransacao() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
        return this;
    }

    public DAO<E> fecharTransacao() {
        if (em.getTransaction().isActive()) {
            try {
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Erro ao commitar transação: " + e.getMessage(), e);
            }
        }
        return this;
    }

    public DAO<E> rollbackTransacao() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        return this;
    }

    // === CRUD OPERATIONS ===
    public DAO<E> incluir(E entidade) {
        try {
            em.persist(entidade);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao incluir entidade: " + e.getMessage(), e);
        }
    }

    public DAO<E> incluirTransacional(E entidade) {
        try {
            return this.abrirTransacao().incluir(entidade).fecharTransacao();
        } catch (Exception e) {
            rollbackTransacao();
            throw new RuntimeException("Erro ao incluir entidade transacional: " + e.getMessage(), e);
        }
    }

    public E obterPorID(Object id) {
        if (classe == null) {
            throw new UnsupportedOperationException("Classe nula");
        }
        try {
            return em.find(classe, id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter por ID: " + e.getMessage(), e);
        }
    }

    public List<E> obterTodos() {
        return obterTodos(0, 0);
    }

    public List<E> obterTodos(int quantidade, int deslocamento) {
        if (classe == null) {
            throw new UnsupportedOperationException("Classe nula");
        }
        
        try {
            String jpql = "SELECT e FROM " + classe.getSimpleName() + " e";
            TypedQuery<E> query = em.createQuery(jpql, classe);
            
            if (quantidade > 0) {
                query.setMaxResults(quantidade);
            }
            if (deslocamento > 0) {
                query.setFirstResult(deslocamento);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter todos os registros: " + e.getMessage(), e);
        }
    }

    // === UPDATE OPERATIONS ===
    public E atualizar(E entidade) {
        try {
            return em.merge(entidade);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar entidade: " + e.getMessage(), e);
        }
    }

    public E atualizarTransacional(E entidade) {
        try {
            abrirTransacao();
            E entidadeAtualizada = atualizar(entidade);
            fecharTransacao();
            return entidadeAtualizada;
        } catch (Exception e) {
            rollbackTransacao();
            throw new RuntimeException("Erro ao atualizar entidade transacional: " + e.getMessage(), e);
        }
    }

    // === DELETE OPERATIONS ===
    public DAO<E> remover(E entidade) {
        if (entidade != null) {
            try {
                em.remove(em.contains(entidade) ? entidade : em.merge(entidade));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao remover entidade: " + e.getMessage(), e);
            }
        }
        return this;
    }

    public DAO<E> removerPorId(Object id) {
        try {
            E entidade = obterPorID(id);
            if (entidade != null) {
                remover(entidade);
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover por ID: " + e.getMessage(), e);
        }
    }

    public DAO<E> removerTransacional(E entidade) {
        try {
            return this.abrirTransacao().remover(entidade).fecharTransacao();
        } catch (Exception e) {
            rollbackTransacao();
            throw new RuntimeException("Erro ao remover entidade transacional: " + e.getMessage(), e);
        }
    }

    public DAO<E> removerPorIdTransacional(Object id) {
        try {
            return this.abrirTransacao().removerPorId(id).fecharTransacao();
        } catch (Exception e) {
            rollbackTransacao();
            throw new RuntimeException("Erro ao remover por ID transacional: " + e.getMessage(), e);
        }
    }

    // === QUERY METHODS ===
    public List<E> consultar(String jpql, Object... parametros) {
        try {
            TypedQuery<E> query = em.createQuery(jpql, classe);
            for (int i = 0; i < parametros.length; i++) {
                query.setParameter(i + 1, parametros[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }
    }

    // === MÉTODOS DE CONTAGEM E PAGINAÇÃO (mantidos do código original) ===
    public long contar() {
        try {
            String jpql = "select count(e) from " + classe.getSimpleName() + " e";
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            return q.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar: " + e.getMessage(), e);
        }
    }

    public List<E> obterPaginaOrdenada(int pagina, int tamanho, String orderBy) {
        try {
            String jpql = "select e from " + classe.getSimpleName() + " e order by e." + orderBy;
            TypedQuery<E> query = em.createQuery(jpql, classe);
            query.setFirstResult(pagina * tamanho);
            query.setMaxResults(tamanho);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro na paginação: " + e.getMessage(), e);
        }
    }

    public List<E> listarTodos() {
        return obterTodos();
    }

    @Override
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
            System.out.println("✅ EntityManager fechado para: " + (classe != null ? classe.getSimpleName() : "null"));
        }
    }

    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("✅ EntityManagerFactory fechado");
            factoryInitialized = false;
        }
    }
}