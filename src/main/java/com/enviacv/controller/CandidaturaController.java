package com.enviacv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enviacv.dto.CandidaturaDetalheResponse;
import com.enviacv.dto.CandidaturaRequest;
import com.enviacv.dto.CandidaturaResponse;
import com.enviacv.service.CandidaturaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {

    private final CandidaturaService service;

    public CandidaturaController(CandidaturaService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidaturaResponse> enviar(
            @Valid CandidaturaRequest dados,
            @RequestParam(value = "curriculo", required = false) MultipartFile curriculo) {

        CandidaturaResponse resposta = service.receber(dados, curriculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public List<CandidaturaDetalheResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CandidaturaDetalheResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}