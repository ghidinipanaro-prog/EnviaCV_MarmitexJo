package com.enviacv.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidatura")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(name = "vaga_pretendida", nullable = false, length = 120)
    private String vagaPretendida;

    @Column(length = 255)
    private String linkedin;

    @Column(columnDefinition = "TEXT")
    private String apresentacao;

    @Column(name = "curriculo_nome", nullable = false, length = 255)
    private String curriculoNome;

    @Column(name = "curriculo_caminho", nullable = false, length = 500)
    private String curriculoCaminho;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @PrePersist
    void aoCriar() {
        this.dataEnvio = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getVagaPretendida() {
		return vagaPretendida;
	}

	public void setVagaPretendida(String vagaPretendida) {
		this.vagaPretendida = vagaPretendida;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public String getCurriculoNome() {
		return curriculoNome;
	}

	public void setCurriculoNome(String curriculoNome) {
		this.curriculoNome = curriculoNome;
	}

	public String getCurriculoCaminho() {
		return curriculoCaminho;
	}

	public void setCurriculoCaminho(String curriculoCaminho) {
		this.curriculoCaminho = curriculoCaminho;
	}

	public LocalDateTime getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(LocalDateTime dataEnvio) {
		this.dataEnvio = dataEnvio;
	}
}