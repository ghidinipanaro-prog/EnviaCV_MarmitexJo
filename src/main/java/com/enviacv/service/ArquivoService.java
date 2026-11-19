package com.enviacv.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.enviacv.exception.ArmazenamentoException;
import com.enviacv.exception.ArquivoInvalidoException;

@Service
public class ArquivoService {

    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024; // 5 MB

    private final Path pastaUpload;

    public ArquivoService(@Value("${app.upload-dir}") String pasta) {
        this.pastaUpload = Paths.get(pasta).toAbsolutePath().normalize();
    }

    /** Valida o PDF, grava no disco e devolve o nome com que foi salvo. */
    public String salvar(MultipartFile arquivo) {
        validar(arquivo);

        try {
            Files.createDirectories(pastaUpload);
            String nomeSalvo = UUID.randomUUID() + ".pdf";
            Path destino = pastaUpload.resolve(nomeSalvo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nomeSalvo;
        } catch (IOException e) {
            throw new ArmazenamentoException("Não foi possível gravar o currículo.", e);
        }
    }

    private void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ArquivoInvalidoException("O currículo é obrigatório.");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ArquivoInvalidoException("O currículo deve ter no máximo 5 MB.");
        }

        String nome = arquivo.getOriginalFilename();
        if (nome == null || !nome.toLowerCase().endsWith(".pdf")) {
            throw new ArquivoInvalidoException("O currículo deve estar no formato PDF.");
        }

        // Todo PDF verdadeiro começa com "%PDF-". Isso impede que alguém
        // renomeie um arquivo qualquer para .pdf e o envie.
        try (InputStream in = arquivo.getInputStream()) {
            String cabecalho = new String(in.readNBytes(5), StandardCharsets.US_ASCII);
            if (!"%PDF-".equals(cabecalho)) {
                throw new ArquivoInvalidoException("O arquivo enviado não é um PDF válido.");
            }
        } catch (IOException e) {
            throw new ArmazenamentoException("Não foi possível ler o currículo enviado.", e);
        }
    
    }
    /** Remove um arquivo já gravado (usado se o banco falhar depois do upload). */
    public void apagar(String nomeSalvo) {
        try {
            Files.deleteIfExists(pastaUpload.resolve(nomeSalvo));
        } catch (IOException e) {
            // se não conseguir apagar, não há mais o que fazer
        }
    }
}