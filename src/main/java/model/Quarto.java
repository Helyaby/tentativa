package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quarto")
public class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String identificacao;
    private String localizacao;
    private String tipo;
    private String status;
    
    @Column(name = "numero_quartos") // ALTERADO: numero_quadros para numero_quartos
    private Integer numeroQuartos; // ALTERADO: numeroQuadros para numeroQuartos
    
    private String observacoes;
    
    // Campo valor
    private Double valor;
    
    // Relação com Cliente - um quarto pode ter um cliente ocupante
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    // Construtores
    public Quarto() {}
    
    // Construtor sem dataInstalacao e com numeroQuartos
    public Quarto(String identificacao, String localizacao, String tipo, String status, 
                  Integer numeroQuartos, String observacoes, Double valor) {
        this.identificacao = identificacao;
        this.localizacao = localizacao;
        this.tipo = tipo;
        this.status = status;
        this.numeroQuartos = numeroQuartos;
        this.observacoes = observacoes;
        this.valor = valor;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
    
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getNumeroQuartos() { return numeroQuartos; } // ALTERADO
    public void setNumeroQuartos(Integer numeroQuartos) { this.numeroQuartos = numeroQuartos; } // ALTERADO
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
}