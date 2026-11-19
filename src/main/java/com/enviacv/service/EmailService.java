package com.enviacv.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.enviacv.exception.EmailFalhouException;
import com.enviacv.model.Candidatura;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Path pastaUpload;
    private final String remetente;
    private final String emailRh;

    public EmailService(JavaMailSender mailSender,
            @Value("${app.upload-dir}") String pasta,
            @Value("${spring.mail.username:}") String remetente,
            @Value("${app.rh.email:}") String emailRh) {
        this.mailSender = mailSender;
        this.pastaUpload = Paths.get(pasta).toAbsolutePath().normalize();
        this.remetente = remetente;
        this.emailRh = emailRh;
    }

    /** Envia os dados e o currículo (anexo) para o RH. Lança EmailFalhouException se falhar. */
    public void enviarParaRh(Candidatura c) {
        if (emailRh == null || emailRh.isBlank()) {
            throw new EmailFalhouException("E-mail do RH não configurado (variável RH_EMAIL).");
        }

        try {
            var mensagem = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            if (!remetente.isBlank()) {
                helper.setFrom(remetente);
            }
            helper.setTo(emailRh);
            helper.setReplyTo(c.getEmail());
            helper.setSubject(limpar("Nova candidatura: " + c.getVagaPretendida() + " - " + c.getNomeCompleto()));
            helper.setText(corpoParaRh(c), false);

            Path pdf = pastaUpload.resolve(c.getCurriculoCaminho());
            String nomeAnexo = (c.getCurriculoNome() == null || c.getCurriculoNome().isBlank())
                    ? "curriculo.pdf"
                    : limpar(c.getCurriculoNome());
            helper.addAttachment(nomeAnexo, new FileSystemResource(pdf));

            mailSender.send(mensagem);
        } catch (Exception e) {
            throw new EmailFalhouException("Falha ao enviar e-mail ao RH: " + e.getMessage(), e);
        }
    }

    /** Envia a confirmação de recebimento ao candidato. Lança EmailFalhouException se falhar. */
    public void enviarConfirmacao(Candidatura c) {
        try {
            var mensagem = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mensagem, false, "UTF-8");

            if (!remetente.isBlank()) {
                helper.setFrom(remetente);
            }
            helper.setTo(c.getEmail());
            helper.setSubject("Recebemos a sua candidatura");
            helper.setText(corpoConfirmacao(c), false);

            mailSender.send(mensagem);
        } catch (Exception e) {
            throw new EmailFalhouException("Falha ao enviar confirmação ao candidato: " + e.getMessage(), e);
        }
    }

    private String corpoParaRh(Candidatura c) {
        return "Nova candidatura recebida pelo EnviaCV.\n\n"
                + "Protocolo: " + c.getId() + "\n"
                + "Nome: " + c.getNomeCompleto() + "\n"
                + "E-mail: " + c.getEmail() + "\n"
                + "Telefone: " + c.getTelefone() + "\n"
                + "Vaga pretendida: " + c.getVagaPretendida() + "\n"
                + "LinkedIn: " + valor(c.getLinkedin()) + "\n"
                + "Data de envio: " + c.getDataEnvio() + "\n\n"
                + "Apresentação:\n" + valor(c.getApresentacao()) + "\n\n"
                + "O currículo está anexado a este e-mail.";
    }

    private String corpoConfirmacao(Candidatura c) {
        return "Olá, " + c.getNomeCompleto() + "!\n\n"
                + "Recebemos a sua candidatura para a vaga \"" + c.getVagaPretendida() + "\".\n"
                + "Protocolo: " + c.getId() + "\n\n"
                + "Nossa equipe de RH vai analisar o seu currículo e, se o seu perfil "
                + "for compatível, entrará em contato.\n\n"
                + "Este é um e-mail automático, não é necessário respondê-lo.";
    }

    private String valor(String texto) {
        return (texto == null || texto.isBlank()) ? "(não informado)" : texto;
    }

    // Remove quebras de linha para evitar injeção de cabeçalhos no e-mail
    private String limpar(String texto) {
        return texto.replaceAll("[\\r\\n]+", " ");
    }
}