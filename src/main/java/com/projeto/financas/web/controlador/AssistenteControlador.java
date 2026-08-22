package com.projeto.financas.web.controlador;

import com.projeto.financas.aplicacao.dto.ComandoTextoDTO;
import com.projeto.financas.aplicacao.dto.RespostaAssistenteDTO;
import com.projeto.financas.infraestrutura.ia.AssistenteServico;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/assistente")
public class AssistenteControlador {

    private final AssistenteServico assistenteServico;

    public AssistenteControlador(AssistenteServico assistenteServico) {
        this.assistenteServico = assistenteServico;
    }

    @PostMapping(value = "/comando-de-voz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> processarComandoDeVoz(@RequestParam("audio") MultipartFile audio) throws IOException {
        String textoTranscrito = assistenteServico.transcrever(audio);
        String respostaTexto = assistenteServico.processarComando(textoTranscrito);
        byte[] audioResposta = assistenteServico.gerarAudio(respostaTexto);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audioResposta);
    }

    @PostMapping("/comando-de-texto")
    public RespostaAssistenteDTO processarComandoDeTexto(@RequestBody @Valid ComandoTextoDTO comando) {
        String resposta = assistenteServico.processarComando(comando.mensagem());
        return new RespostaAssistenteDTO(resposta);
    }
}
