# financas-ai

Projeto feito pra o desafio de código da trilha de Spring Boot da DIO (módulo de Spring AI). A ideia é uma API de orçamento pessoal que registra e consulta transações financeiras, e que também tem um assistente com IA que entende comandos de voz ou texto e chama essas funções sozinho (Tool Calling).

## O que o projeto faz

- Cadastra transações financeiras (receita ou despesa), com descrição, valor, categoria e data.
- Lista as transações por período.
- Calcula o total gasto/recebido em uma categoria.
- Tem um assistente de IA (usando Spring AI + OpenAI) que recebe um comando em texto ou um áudio, entende o que a pessoa quer e chama as funções acima sozinho, sem precisar mapear manualmente qual endpoint chamar.
- O assistente também transforma o áudio da pessoa em texto (Transcription API) e devolve a resposta dele em áudio (Text-to-Speech), fechando o ciclo de voz.

## Tecnologias usadas

- Java 21
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Bean Validation
- Spring AI 1.0.8 (ChatClient, Tool Calling, Transcription API, Speech API)
- H2 (banco em memória, pra rodar e testar sem precisar instalar nada)
- PostgreSQL (driver incluso, pra quem quiser usar em produção)
- JUnit 5 + Mockito + AssertJ

## Estrutura do projeto

```
com.projeto.financas
├── dominio
│   ├── modelo         -> Transacao, TipoTransacao
│   └── repositorio     -> TransacaoRepositorio
├── aplicacao
│   ├── dto             -> DTOs de entrada e saída
│   └── servico          -> TransacaoServico (regra de negócio)
├── web
│   └── controlador       -> TransacaoControlador, AssistenteControlador
└── infraestrutura
    ├── ia               -> AssistenteFerramentas (Tool Calling), AssistenteServico (ChatClient, transcrição e fala)
    └── excecao          -> tratamento global de erros
```

Segui a mesma separação em camadas (domínio, aplicação, web, infraestrutura) que a gente viu no curso, só que com os nomes das classes em português mesmo, pra ficar mais natural de ler.

## Como rodar

Não precisa de Docker nem de Postgres pra rodar localmente: o `application.yml` já está configurado com H2 em memória por padrão. Você só precisa do JDK 21 instalado.

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

> No Windows, troca `./gradlew` por `gradlew.bat`.

### Sobre a chave da OpenAI

O projeto tem um valor padrão (`chave-nao-configurada`) pra propriedade `spring.ai.openai.api-key`, só pra aplicação conseguir subir sem quebrar mesmo sem uma chave de verdade configurada. Isso é suficiente pra testar toda a parte de transações (cadastro, listagem, total por categoria), que não depende de IA nenhuma.

Se você tiver uma chave da OpenAI e quiser testar o assistente de verdade, exporta a variável de ambiente antes de subir a aplicação:

```bash
export OPENAI_API_KEY=sua-chave-aqui
./gradlew bootRun
```

Sem uma chave real, os endpoints de `/api/assistente/*` vão retornar erro (401 da OpenAI), mas isso é esperado e não indica que o projeto está com problema. Já os endpoints de `/api/transacoes/*` funcionam 100% sem chave nenhuma.

## Como testar

### Rodando os testes automatizados

```bash
./gradlew test
```

Tem um teste de contexto (confirma que a aplicação sobe com todas as configurações) e testes unitários da camada de serviço, testando o cadastro de uma transação e o cálculo do total por categoria.

### Testando a API manualmente (sem precisar de chave da OpenAI)

Com a aplicação rodando, cadastra uma transação:

```bash
curl -X POST http://localhost:8080/api/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Almoco no restaurante",
    "valor": 45.90,
    "tipo": "DESPESA",
    "categoria": "alimentacao",
    "data": "2026-08-22"
  }'
```

Lista as transações do período:

```bash
curl http://localhost:8080/api/transacoes
```

Consulta o total gasto em uma categoria:

```bash
curl http://localhost:8080/api/transacoes/categoria/alimentacao/total
```

Se essas três chamadas responderem sem erro, a parte principal da API já está funcionando certinho, mesmo sem a chave da OpenAI.

Dá pra acompanhar os dados direto pelo console do H2, em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:financas_ai`, usuário `sa`, senha em branco).

### Testando o assistente (precisa de chave da OpenAI)

```bash
curl -X POST http://localhost:8080/api/assistente/comando-de-texto \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "registra uma despesa de 30 reais com uber hoje na categoria transporte"}'
```

E pra comando de voz (envia um arquivo de áudio):

```bash
curl -X POST http://localhost:8080/api/assistente/comando-de-voz \
  -F "audio=@caminho/do/audio.mp3" \
  --output resposta.mp3
```

## O que eu aprendi fazendo esse desafio

- Como configurar o Spring AI num projeto Gradle, incluindo o BOM de versões e o starter certo pra cada versão (o nome do artefato mudou de `spring-ai-openai-spring-boot-starter` pra `spring-ai-starter-model-openai` nas versões mais recentes, o que me pegou de surpresa no começo).
- Como funciona o Tool Calling: em vez de eu escrever um monte de `if` pra entender o que o usuário quer, eu descrevo as funções com `@Tool` e `@ToolParam`, e o próprio modelo decide qual chamar e com quais parâmetros.
- A diferença entre a Transcription API (áudio vira texto) e a Speech API (texto vira áudio), e como encadear as duas com o ChatClient pra fechar um fluxo de comando de voz completo.
- Que dá pra manter a aplicação inteira testável sem depender de uma chave de API paga, isolando bem o que é lógica de negócio (transações) do que depende de infraestrutura externa (IA).

## Evolução implementada

Adicionei um resumo financeiro do período, mostrando total de receitas, total de despesas e o saldo (receitas menos despesas).

Essa lógica ficou centralizada no `TransacaoServico`, no método `gerarResumo`, e é reaproveitada em dois lugares diferentes sem duplicar código:

- Endpoint REST: `GET /api/transacoes/resumo` (aceita os parâmetros opcionais `inicio` e `fim`, no formato `AAAA-MM-DD`).
- Ferramenta do assistente de IA: agora dá pra perguntar algo como "como está meu saldo esse mês?" ou "faz um resumo das minhas finanças" que o assistente chama a função sozinho e responde com os valores.

Exemplo de teste do endpoint:

\`\`\`powershell
Invoke-RestMethod -Uri http://localhost:8080/api/transacoes/resumo
\`\`\`

Escolhi essa evolução porque ela mostra bem o ponto central do desafio: uma mesma regra de negócio pode ser exposta tanto por um endpoint REST tradicional quanto por uma ferramenta de IA, sem duplicar nada.