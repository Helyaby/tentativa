package model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cpf;
    
    @Column(nullable = false)
    private String nome;
    
    private String email;
    private String rg;
    private String sexo;
    private String cep;
    
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    
    private String telefone;
    private String cidade;
    
    // Construtores
    public Cliente() {}
    
    public Cliente(String cpf, String nome, String email, String rg, String sexo, 
                  String cep, LocalDate dataNascimento, String telefone, String cidade) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.rg = rg;
        this.sexo = sexo;
        this.cep = cep;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.cidade = cidade;
    }
    
    // Getters e Setters
    // (gerar todos os getters e setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
}