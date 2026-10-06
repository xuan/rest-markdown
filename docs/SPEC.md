# REST-Markdown — Spec

The contract as actually shipped — a derived reference. The source of truth for *what*
the product must do is [RULES.md](RULES.md); the architecture and its rationale are in
[DESIGN.md](DESIGN.md).

## Purpose

Turn a natural-language prompt describing an HTTP endpoint into a markdown document
(request/response examples, description) suitable for API documentation.

## Architecture

Two independently run apps, no shared build; no database, auth, or persistence:

- `backend/` — Spring Boot 4.1 (Java 25) REST API; a single endpoint that renders a
  markdown document from a prompt via a built-in template.
- `frontend/` — Next.js 14 (App Router) + Tailwind single-page UI; calls the backend
  via client-side `fetch`.

## API contract

Defined in [backend/openapi.yaml](../backend/openapi.yaml) and compiled to Java models
at build time by `openapi-generator-maven-plugin` (output
`backend/target/generated-sources/openapi`, package `com.xuan.restmarkdown.generated`).
Do not hand-edit generated sources — edit `openapi.yaml` and rebuild.

### `POST /api/markdown`

```json
// request
{ "prompt": "string, required, minLength 1" }
// response
{ "markdown": "string, required, minLength 1" }
```

## Request flow

[MarkdownController](../backend/src/main/java/com/xuan/restmarkdown/controller/MarkdownController.java)
delegates to
[MarkdownService](../backend/src/main/java/com/xuan/restmarkdown/service/MarkdownService.java),
which renders a static markdown template from the prompt — no network calls. The
response is always valid markdown; the endpoint does not return error statuses.

## Configuration

[application.yaml](../backend/src/main/resources/application.yaml) sets only the server
port (`8080`) and logging. No secret or environment-specific configuration.

## Known gaps / not yet implemented

- No CORS restriction — `@CrossOrigin(origins = "*")` on the controller.
- Frontend backend URL hardcoded to `http://localhost:8080` in
  [page.tsx](../frontend/src/app/page.tsx), not environment-driven.
- Markdown comes from a fixed template only — no live/LLM-backed generation. Covered
  by [MarkdownControllerTest](../backend/src/test/java/com/xuan/restmarkdown/MarkdownControllerTest.java).
- No rate limiting or request size limits on the prompt.

Keep this in sync when the API contract, request flow, or configuration changes.
