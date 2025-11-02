# Survey App

Aplicacao Java basica para criacao e votacao de pesquisas utilizando uma arquitetura desacoplada.

## Estrutura

- `com.example.survey.config` – leitura de configuracoes.
- `com.example.survey.database` – fabricas de DataSource para SQLite ou PostgreSQL e migracao de schema.
- `com.example.survey.domain` – entidades de dominio.
- `com.example.survey.repository` – contrato de persistencia e implementacao JDBC.
- `com.example.survey.service` – regras de negocio para criar pesquisas, votar e exibir dashboard.
- `com.example.survey.ui` – camada de interface (inclui a versao desktop em Swing e permite extensao para outras UIs).

## Requisitos

- Java 17+
- Maven 3.9+

## Configuracao do banco de dados

Por padrao a aplicacao utiliza SQLite e cria o arquivo `data/surveys.db`. As configuracoes ficam em `src/main/resources/application.properties`:

```properties
db.type=sqlite
sqlite.url=jdbc:sqlite:data/surveys.db
```

Para alternar para PostgreSQL basta atualizar o arquivo:

```properties
db.type=postgres
postgres.url=jdbc:postgresql://localhost:5432/surveys
postgres.user=postgres
postgres.password=postgres
```

A troca de banco e encapsulada por `DataSourceProvider`, portanto a camada de repositorio continua a mesma.

## Executando

```bash
mvn package
java -jar target/survey-app-1.0.0-SNAPSHOT.jar
```

Ao iniciar, uma janela Swing com abas permite criar novas pesquisas, listar/votar e acompanhar o dashboard consolidado de votos.

## Expandindo a interface

A implementacao `SwingSurveyUI` utiliza `JPanel`/`JTabbedPane` para entregar uma interface grafica basica. A aplicacao continua isolando a camada de apresentacao via `SurveyUI`, portanto e possivel substituir ou adicionar outras interfaces (por exemplo, web ou JavaFX) apenas fornecendo uma nova implementacao e atualizando a inicializacao no `Main`.
