# FinaObra — frontend integrado ao contrato OpenAPI atualizado

Frontend estático para ser servido pelo Spring Boot ou pelo servidor HTTP do Python.

## Arquivos

- `index.html`: interface.
- `styles.css`: estilos.
- `app.js`: integração com a API REST.
- `config.js`: configuração opcional da origem da API.
- `manifest.webmanifest` e `sw.js`: suporte básico à instalação PWA.

## API esperada

Origem padrão: `http://localhost:8080`

- `POST /auth/register`: `{ nome, email, username, password }`
- `POST /auth/login`: `{ userName, password }`, resposta contendo `token`
- `GET /api/v1/categorias?page=0&size=50`: página com `content`, itens `{ id, nome, valor_planejado }`
- `POST /api/v1/categorias`: `{ nome, valor_planejado }`
- `DELETE /api/v1/categorias/{id}`: `id` inteiro
- `GET /api/v1/despesas?page=0&size=50`: esperado `content` com itens `{ id, descricao, idCategoria, fornecedor, valor, data }`
- `POST /api/v1/despesas`: `{ descricao, categoria_id, fornecedor, valor, data }`
- `DELETE /api/v1/despesas/{id}`: `id` UUID
- `GET /api/v1/dashboard/resumo`: `{ totalPlanejado, totalRealizado, saldoRestante, progressoCategorias }`

O contrato OpenAPI fornecido não descreve o schema de resposta `200` de `GET /api/v1/despesas`; o frontend aceita `content` (Page do Spring) ou um array JSON. Recomenda-se documentar esse response no OpenAPI.

## Executar

### Com Spring Boot
Copie todos os arquivos para `src/main/resources/static/` e abra `http://localhost:8080/`.

### Testar frontend separado com Python
Dentro desta pasta, execute:

```bash
python3 -m http.server 5500
```

Abra `http://localhost:5500/`. Nesse modo, a API continua em `http://localhost:8080`. O backend precisa permitir CORS para a origem `http://localhost:5500`.

## Observações de integração

- O token JWT é guardado em `sessionStorage` e enviado como `Authorization: Bearer <token>` às rotas protegidas.
- O cadastro não presume que a resposta de registro contenha token: conforme o contrato, após cadastrar, a tela solicita login.
- Os valores de despesa/orçamento respeitam a faixa `2.00` a `999999.99`.
- Não há dados fictícios usados como fallback para simular persistência.
