package com.enviacv.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.enviacv.dto.CandidaturaDetalheResponse;
import com.enviacv.dto.CandidaturaRequest;
import com.enviacv.dto.CandidaturaResponse;
import com.enviacv.exception.CandidaturaNaoEncontradaException;
import com.enviacv.model.Candidatura;
import com.enviacv.repository.CandidaturaRepository;

@Service
public class CandidaturaService {

    private static final Logger log = LoggerFactory.getLogger(CandidaturaService.class);

    private final CandidaturaRepository repository;
    private final ArquivoService arquivoService;
    private final FilaEmailService filaEmailService;

    public CandidaturaService(CandidaturaRepository repository,
            ArquivoService arquivoService,
            FilaEmailService filaEmailService) {
        this.repository = repository;
        this.arquivoService = arquivoService;
        this.filaEmailService = filaEmailService;
    }

    public CandidaturaResponse receber(CandidaturaRequest dados, MultipartFile curriculo) {
        // 1. valida e grava o PDF (lança exceção se for inválido)
        String nomeSalvo = arquivoService.salvar(curriculo);

        // 2. monta a entidade a partir do formulário
        Candidatura c = new Candidatura();
        c.setNomeCompleto(dados.getNomeCompleto());
        c.setEmail(dados.getEmail());
        c.setTelefone(dados.getTelefone());
        c.setVagaPretendida(dados.getVagaPretendida());
        c.setLinkedin(dados.getLinkedin());
        c.setApresentacao(dados.getApresentacao());
        c.setCurriculoNome(nomeSeguro(curriculo.getOriginalFilename()));
        c.setCurriculoCaminho(nomeSalvo);

        // 3. grava no MySQL (se falhar, apaga o PDF para não deixar arquivo órfão)
        Candidatura salva;
        try {
            salva = repository.save(c);
        } catch (RuntimeException e) {
            arquivoService.apagar(nomeSalvo);
            throw e;
        }

        // 4. envia os e-mails; se falharem, vão para a fila de reenvio.
        //    Um problema aqui nunca pode derrubar uma candidatura já gravada.
        try {
            filaEmailService.notificar(salva);
        } catch (RuntimeException e) {
            log.error("Erro ao processar e-mails da candidatura {}.", salva.getId(), e);
        }

        return new CandidaturaResponse(
                salva.getId(),
                "Candidatura recebida com sucesso.",
                salva.getDataEnvio());
    }

    public List<CandidaturaDetalheResponse> listar() {
        return repository.findAllByOrderByDataEnvioDesc().stream()
                .map(CandidaturaDetalheResponse::de)
                .toList();
    }

    public CandidaturaDetalheResponse buscar(Long id) {
        return repository.findById(id)
                .map(CandidaturaDetalheResponse::de)
                .orElseThrow(() -> new CandidaturaNaoEncontradaException(id));
    }

    // Fica só com o nome do arquivo (sem pastas), sem quebras de linha e com até 255 caracteres
    private String nomeSeguro(String original) {
        String nome = StringUtils.getFilename(StringUtils.cleanPath(original == null ? "" : original));
        if (nome == null || nome.isBlank()) {
            return "curriculo.pdf";
        }
        nome = nome.replaceAll("[\\r\\n]+", " ");
        return nome.length() > 255 ? nome.substring(nome.length() - 255) : nome;
    }
}