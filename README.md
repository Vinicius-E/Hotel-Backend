# 🏨 Hotel Backend API

API desenvolvida como parte de um desafio técnico para a **Senior Sistemas**. Este backend possibilita o **cadastro de hóspedes**, **check-in** e **check-out** em um hotel, com **cálculo automático de valores** baseados em regras de negócio.

---

## 🛠️ Tecnologias Utilizadas

- Java 17  
- Spring Boot 3.5.3  
- Maven 3.6.3  
- PostgreSQL  
- Swagger OpenAPI 3.1  
- JUnit 5  
- Mockito  

---

## 🚀 Como Executar o Projeto

### 1. Criar o banco de dados

No PostgreSQL, crie o banco de dados com o nome e credenciais desejadas:

```sql
CREATE DATABASE senior_challenge_db;
CREATE USER postgres WITH ENCRYPTED PASSWORD 'Vini13lagoa$';
GRANT ALL PRIVILEGES ON DATABASE senior_challenge_db TO postgres;
```

> ⚠️ Caso use outro nome de banco ou senha, atualize o arquivo `application.yml` adequadamente.

---

### 2. Executar a aplicação

```bash
mvn install
```

> 🔁 **Importante:** A execução via `mvn spring-boot:run` pode apresentar problemas com inicialização do schema.  
> ✅ Recomenda-se o uso de `mvn install`, pois além de buildar, executa os testes corretamente.  
> 🖥️ Também é possível rodar a aplicação diretamente pela classe `HotelBackendApplication`.

---

## 📚 Acesso ao Swagger

A documentação da API está disponível após o start da aplicação:

👉 [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)

---

## 🧪 Testes Automatizados

A aplicação conta com **testes unitários** utilizando **JUnit 5** e **Mockito**, cobrindo os principais fluxos de **serviços** e **controllers**.

### Executar testes:

```bash
mvn install
```

### Estrutura dos testes:

```
src/test/java/com.hotel.backend/
├── controller/
├── service/
└── HotelBackendApplicationTests.java
```

---

## 🗃️ Scripts de Banco

Todos os scripts de criação de tabelas e dados estão nos arquivos `schema.sql` e `data.sql`. Estes arquivos:

- Criam o schema `public` e concedem permissões;
- Criam as tabelas `hospede` e `checkin`, normalizadas (1FN, 2FN, 3FN);
- Inserem dados de exemplo para testes.

> 🔁 Esses scripts são executados automaticamente ao rodar a aplicação.

---

## 📁 Estrutura de Pacotes

A estrutura do projeto segue o padrão recomendado pelo Spring Boot:

```
com.hotel.backend
├── config          # Configurações da aplicação
├── controller      # Camada REST: endpoints da API
├── dto             # Objetos de transferência de dados
├── entity          # Entidades JPA (mapeamento do banco)
├── exception       # Tratamento de exceções personalizadas
├── repository      # Interfaces de persistência com Spring Data JPA
├── service         # Regras de negócio da aplicação
├── util            # Utilitários e helpers
└── HotelBackendApplication.java # Classe principal
```

---

## 🔄 Melhorias Futuras

O sistema foi desenvolvido com foco na entrega do desafio e aplicação funcional. Entretanto, alguns pontos de melhoria podem ser considerados:

- ✅ **Paginação**: Ainda não implementada por conta da baixa quantidade de dados simulados;
- ✅ **Autenticação/JWT**: Pode ser adicionada em cenários reais;
- ✅ **Profile de produção**: Atualmente, o ambiente usa `application.yml` padrão, sem separação por perfis (`dev`, `prod` etc);
- ✅ **Mensageria com RabbitMQ**:  
  Pode ser implementada para geração de relatórios automáticos em segundo plano ou notificações assíncronas.  
  Isso ajudaria na escalabilidade e desacoplamento da lógica de negócio.

---

## 👤 Autor

Desenvolvido por **Vinicius Eduardo Da Silva**  
📧 [viniciuseduardo0702@hotmail.com](mailto:viniciuseduardo0702@hotmail.com)  
🔗 [LinkedIn](https://www.linkedin.com/in/vinicius-esilva)  
💻 [GitHub](https://github.com/Vinicius-E)

---

## 📎 Anexos

<img width="1455" height="510" alt="image" src="https://github.com/user-attachments/assets/4eadde92-0650-48b3-8bb8-d64626123f8d" />  
<img width="1519" height="999" alt="image" src="https://github.com/user-attachments/assets/8a0cc59c-8e3e-4092-b5fc-ab223c02f205" />  
<img width="1489" height="571" alt="image" src="https://github.com/user-attachments/assets/9a293084-d9a0-441d-afa0-a48ad81b3e41" />
