# Estoque – cadastro de itens de bens de consumo

API REST em Java + Spring Boot para cadastrar itens, controlar entrada/saída de estoque e avisar quando algo precisa ser reabastecido.

## Requisitos

- Java 25
- Maven (ou o Maven embutido na sua IDE)

## Como rodar

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O banco é um H2 salvo em arquivo na pasta `estoque-data` dentro da pasta do usuário do Windows/Linux (por exemplo `C:\Users\fulano\estoque-data`), criada automaticamente. As tabelas são geradas pelo próprio Hibernate, então não é preciso instalar nem configurar nada.

Para ver os dados direto no banco: `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:file:<pasta do usuário>/estoque-data/estoque`, usuário `sa`, senha vazia).

## Persistência dos dados (queda ou fechamento da aplicação)

- Os dados ficam no arquivo `estoque.mv.db`, não na memória. Se a aplicação cair ou for fechada, ao abrir de novo tudo continua lá.
- O caminho é fixo, então não importa de qual pasta a aplicação é iniciada: ela sempre usa o mesmo banco. Para mudar a pasta, defina a variável de ambiente `ESTOQUE_BANCO_PASTA`.
- Cada alteração é gravada no disco imediatamente (`WRITE_DELAY=0`).
- Não altere `spring.jpa.hibernate.ddl-auto` para `create` ou `create-drop`, pois apagam os dados ao iniciar.
- **Faça backup** copiando o arquivo `estoque.mv.db` regularmente (com a aplicação parada, se possível) para outro disco ou pasta de rede. Queda de energia ou falha do disco podem corromper qualquer arquivo de banco.

### Se você já rodou uma versão antiga (com validade)

O banco antigo ainda tem a coluna `VALIDADE` como obrigatória, e o Hibernate não remove colunas sozinho. Sem corrigir isso, todo cadastro novo vai falhar. Escolha uma opção:

- **Sem dados importantes:** pare a aplicação e apague a pasta `estoque-data`. Ela é recriada limpa na próxima execução.
- **Com dados para manter:** abra `http://localhost:8080/h2-console`, conecte e execute `ALTER TABLE ITENS DROP COLUMN VALIDADE;`

## Campos de um item

| Campo | Descrição |
|---|---|
| `id` | Gerado automaticamente ao cadastrar |
| `nome` | Nome do item (obrigatório) |
| `quantidade` | Quantidade em estoque (0 ou mais) |
| `quantidadeMinima` | Abaixo desta quantidade o sistema avisa para reabastecer |

## Endpoints

| Método | Rota | O que faz |
|---|---|---|
| POST | `/api/itens` | Cadastra um item |
| GET | `/api/itens` | Lista todos (use `?nome=papel` para filtrar) |
| GET | `/api/itens/{id}` | Busca um item pelo id (404 se não existir) |
| PUT | `/api/itens/{id}` | Edita um item |
| DELETE | `/api/itens/{id}` | Exclui um item |
| POST | `/api/itens/{id}/entrada` | Registra entrada de estoque |
| POST | `/api/itens/{id}/saida` | Registra saída (recusa se faltar estoque) |
| GET | `/api/itens/alertas` | Itens com estoque abaixo do mínimo |

Toda resposta de item traz `estoqueBaixo` e a lista `avisos` com as mensagens prontas para mostrar na tela, por exemplo:
`"Estoque baixo: restam 2 (mínimo 5). É preciso reabastecer."`

O aviso de estoque baixo dispara quando `quantidade < quantidadeMinima`.

## Exemplos

```bash
# cadastrar
curl -X POST http://localhost:8080/api/itens \
  -H "Content-Type: application/json" \
  -d '{"nome":"Papel A4","quantidade":20,"quantidadeMinima":5}'

# buscar o item de id 1
curl http://localhost:8080/api/itens/1

# registrar saída de 18 unidades (vai gerar aviso de estoque baixo)
curl -X POST http://localhost:8080/api/itens/1/saida \
  -H "Content-Type: application/json" \
  -d '{"quantidade":18}'

# ver alertas
curl http://localhost:8080/api/itens/alertas
```

## Estrutura

```
src/main/java/com/empresa/estoque
├── controller   # rotas REST
├── service      # regras de negócio (entrada, saída, alertas)
├── repository   # acesso ao banco (Spring Data JPA)
├── model        # entidade Item
├── dto          # objetos de entrada e saída da API
├── exception    # erros e tratamento global
└── config       # CORS
```

## Próximos passos

- Autenticação e perfis de acesso (Spring Security)
- Histórico de movimentações (quem deu entrada/saída e quando)
- Trocar o H2 por PostgreSQL/MySQL se o uso crescer
