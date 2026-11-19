package com.enviacv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CandidaturaRequest {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 30, message = "O telefone deve ter no máximo 30 caracteres.")
    private String telefone;

    @NotBlank(message = "A vaga pretendida é obrigatória.")
    @Size(max = 120, message = "A vaga deve ter no máximo 120 caracteres.")
    private String vagaPretendida;

    @Size(max = 255, message = "O link do LinkedIn deve ter no máximo 255 caracteres.")
    private String linkedin;

    @Size(max = 5000, message = "A apresentação deve ter no máximo 5000 caracteres.")
    private String apresentacao;

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
}