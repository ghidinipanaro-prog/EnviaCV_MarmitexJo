package com.enviacv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.enviacv.exception.EmailFalhouException;
import com.enviacv.model.Candidatura;
import com.enviacv.model.FilaEmail;
import com.enviacv.repository.FilaEmailRepository;

@Service
public class FilaEmailService {

    private static final Logger log = LoggerFactory.getLogger(FilaEmailService.class);

    private final FilaEmailRepository repository;
    private final EmailService emailService;
    private final int maxTentativas;

    public FilaEmailService(FilaEmailRepository repository,
            EmailService emailService,
            @Value("${app.email.max-tentativas:5}") int maxTentativas) {
        this.repository = repository;
        this.emailService = emailService;
        this.maxTentativas = maxTentativas;
    }

    /** Primeira tentativa de envio. Se algo falhar, entra na fila de reenvio. */
    public void notificar(Candidatura c) {
        String erro = tentarEnviar(c, FilaEmail.Tipo.RH);
        if (erro != null) {
            registrarFalha(c, FilaEmail.Tipo.RH, erro);
            return;
        }
        log.info("E-mail da candidatura {} enviado ao RH.", c.getId());

        erro = tentarEnviar(c, FilaEmail.Tipo.CONFIRMACAO);
        if (erro != null) {
            registrarFalha(c, FilaEmail.Tipo.CONFIRMACAO, erro);
            return;
        }
        log.info("Confirmação da candidatura {} enviada ao candidato.", c.getId());
    }

    /** Roda em segundo plano e tenta reenviar os e-mails pendentes. */
    @Scheduled(fixedDelayString = "${app.email.reenvio-intervalo-ms:60000}", initialDelay = 10000)
    public void reenviarPendentes() {
        for (FilaEmail item : repository.findByStatusOrderByCriadoEmAsc(FilaEmail.Status.PENDENTE)) {
            try {
                processar(item);
            } catch (Exception e) {
                log.error("Erro inesperado ao reprocessar o item {} da fila.", item.getId(), e);
            }
        }
    }

    private void processar(FilaEmail item) {
        Candidatura c = item.getCandidatura();
        String erro = tentarEnviar(c, item.getTipo());

        if (erro == null) {
            item.setStatus(FilaEmail.Status.ENVIADO);
            item.setUltimoErro(null);
            repository.save(item);
            log.info("Reenvio bem-sucedido: candidatura {} ({}).", c.getId(), item.getTipo());

            // O e-mail do RH saiu agora, então falta confirmar ao candidato
            if (item.getTipo() == FilaEmail.Tipo.RH) {
                String erroConfirmacao = tentarEnviar(c, FilaEmail.Tipo.CONFIRMACAO);
                if (erroConfirmacao != null) {
                    registrarFalha(c, FilaEmail.Tipo.CONFIRMACAO, erroConfirmacao);
                }
            }
            return;
        }

        item.setTentativas(item.getTentativas() + 1);
        item.setUltimoErro(cortar(erro));

        if (item.getTentativas() >= maxTentativas) {
            item.setStatus(FilaEmail.Status.FALHOU);
            log.error("Desistindo do envio {} da candidatura {} após {} tentativas: {}",
                    item.getTipo(), c.getId(), item.getTentativas(), erro);
        } else {
            log.warn("Reenvio {} da candidatura {} falhou (tentativa {} de {}): {}",
                    item.getTipo(), c.getId(), item.getTentativas(), maxTentativas, erro);
        }
        repository.save(item);
    }

    private void registrarFalha(Candidatura c, FilaEmail.Tipo tipo, String erro) {
        FilaEmail item = new FilaEmail(c, tipo);
        item.setTentativas(1);
        item.setUltimoErro(cortar(erro));
        repository.save(item);
        log.warn("Candidatura {}: envio {} falhou e foi para a fila de reenvio. Motivo: {}",
                c.getId(), tipo, erro);
    }

    /** Devolve null se enviou, ou a mensagem de erro se falhou. */
    private String tentarEnviar(Candidatura c, FilaEmail.Tipo tipo) {
        try {
            if (tipo == FilaEmail.Tipo.RH) {
                emailService.enviarParaRh(c);
            } else {
                emailService.enviarConfirmacao(c);
            }
            return null;
        } catch (EmailFalhouException e) {
            return e.getMessage() == null ? "Erro desconhecido" : e.getMessage();
        }
    }

    private String cortar(String texto) {
        return texto.length() <= 500 ? texto : texto.substring(0, 500);
    }
}