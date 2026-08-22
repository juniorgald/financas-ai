package com.projeto.financas.infraestrutura.ia;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AssistenteServico {

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel transcricaoModel;
    private final OpenAiAudioSpeechModel falaModel;

    public AssistenteServico(
            ChatClient.Builder chatClientBuilder,
            AssistenteFerramentas assistenteFerramentas,
            OpenAiAudioTranscriptionModel transcricaoModel,
            OpenAiAudioSpeechModel falaModel
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem("Voce e um assistente financeiro pessoal. Use as ferramentas disponiveis para registrar e consultar transacoes financeiras. Responda sempre em portugues do Brasil, de forma clara e objetiva.")
                .defaultTools(assistenteFerramentas)
                .build();
        this.transcricaoModel = transcricaoModel;
        this.falaModel = falaModel;
    }

    public String transcrever(MultipartFile audio) throws IOException {
        var recurso = new ByteArrayResource(audio.getBytes()) {
            @Override
            public String getFilename() {
                return audio.getOriginalFilename();
            }
        };
        var opcoes = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();
        var prompt = new AudioTranscriptionPrompt(recurso, opcoes);
        AudioTranscriptionResponse resposta = transcricaoModel.call(prompt);
        return resposta.getResult().getOutput();
    }

    public String processarComando(String texto) {
        return chatClient.prompt().user(texto).call().content();
    }

    public byte[] gerarAudio(String texto) {
        var prompt = new SpeechPrompt(texto);
        SpeechResponse resposta = falaModel.call(prompt);
        return resposta.getResult().getOutput();
    }
}
