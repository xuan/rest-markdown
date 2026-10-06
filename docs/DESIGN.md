# REST-Markdown — Architecture & Design

The standing design document for the whole app: how it's structured, why it's shaped
that way, and the principles to hold to as it grows. This is the "how and why" of the
build; [RULES.md](RULES.md) is the source of truth for *what* the product must do,
[SPEC.md](SPEC.md) is the contract as shipped, and [../CLAUDE.md](../CLAUDE.md) is the
coding conventions. When a design decision here changes, update this file; when
observable behavior changes, update SPEC.md too.

## Design goals

- **Prompt → docs, simply.** Turn a natural-language endpoint description into
  markdown. Everything else is in service of keeping that path clear.
- **Two decoupled apps.** Backend and frontend build, run, and deploy independently;
  the only coupling is the HTTP contract in `openapi.yaml`.
- **Contract-first.** The API shape is authored in `openapi.yaml` and generated into
  Java models — the schema is the source of truth, not hand-written DTOs.
- **Thin edges, logic in the middle.** Controllers map HTTP; services hold logic.
- **No incidental state.** No database, auth, sessions, or persistence. A request is
  a pure function of its input. Keep it that way until a goal genuinely requires state.
- **Small surface, match patterns.** Prefer the patterns already here (Mono, Lombok,
  MockMvc) over adding a library for a one-off.

## System shape

```text
┌─────────────────────────┐         POST /api/markdown          ┌──────────────────────────┐
│  frontend/  (Next.js 14) │  ── { prompt } ──────────────────▶ │  backend/  (Spring Boot)  │
│  App Router, Tailwind    │                                     │                           │
│  client component +      │  ◀────────────────── { markdown } ─ │  Controller → Service     │
│  fetch, local useState   │                                     │  (contract from openapi)  │
└─────────────────────────┘                                     └──────────────────────────┘
     stateless UI                                                     stateless, no I/O
```

Two processes, no shared build, no gateway between them. The frontend calls the
backend directly over HTTP/JSON.

## Backend design

Layering is deliberate and one-directional: `controller/` → `service/`. Generated
models flow through both.

- **Contract (`openapi.yaml` → `com.xuan.restmarkdown.generated`).** Request/response
  types are generated at build time by `openapi-generator-maven-plugin`. Editing the
  API means editing the YAML and regenerating — generated sources and `target/` are
  never hand-edited. This keeps client and server honest about one schema.
- **Controller (`controller/`).** Thin. Maps HTTP to a service call and back:
  validates the body (`@Valid` + `jakarta.validation`), logs the request, returns the
  service's `Mono`. No business logic. See
  [MarkdownController](../backend/src/main/java/com/xuan/restmarkdown/controller/MarkdownController.java).
- **Service (`service/`).** Where generation logic lives. Today
  [MarkdownService](../backend/src/main/java/com/xuan/restmarkdown/service/MarkdownService.java)
  renders a fixed markdown template from the prompt and returns it wrapped in a `Mono`
  — no network or blocking I/O. This is the seam where real (e.g. LLM-backed)
  generation will slot in without the controller changing.

### Reactive model

The generation path returns `Mono<T>` end-to-end. It's a placeholder wrapper now
(`Mono.just(...)`), but committing to it up front means an async generator (an LLM
call, an external service) drops in without reshaping signatures. The rule: don't
mix blocking calls into the reactive path — if a step blocks, schedule it off the
event loop rather than calling it inline.

### Conventions that shape the code

Constructor injection (`@RequiredArgsConstructor` + `final`), Lombok over
boilerplate, `@Slf4j` with parameterized log messages, config in `application.yaml`
(with `@Value` + safe defaults if external/secret config is ever added). These live
in full in [CLAUDE.md](../CLAUDE.md); they exist to keep hand-written classes small
and uniform.

## Frontend design

A single client component ([page.tsx](../frontend/src/app/page.tsx)): a prompt
textarea, a submit that `fetch`es the backend, and a pane that renders the returned
markdown. State is local `useState` (`prompt`, `markdown`, `loading`) — no global
store, no data-fetching library, no component library. Styling is Tailwind utilities.
The design bet is that a single-endpoint tool doesn't warrant that machinery; revisit
only when a second screen or shared state actually appears.

## Cross-cutting concerns

- **Validation.** Enforced at the contract (`minLength: 1` in the schema) and the
  edge (`@Valid`). Invalid input is rejected before it reaches the service.
- **Error handling.** The happy path always produces valid markdown; the endpoint
  doesn't surface error statuses today. When generation can fail (network, upstream),
  add explicit error mapping in the controller layer, not scattered through services.
- **Security posture.** Currently open: `@CrossOrigin(origins = "*")` and no
  auth/rate-limiting. Acceptable for a local starter, and called out as a gap (below)
  rather than hidden.
- **Configuration.** Static in `application.yaml` (port, logging). The frontend's
  backend URL is currently hardcoded. Any secret/external config must be read via
  `@Value` with a safe default and never committed.

## Key decisions & rationale

| Decision | Why | Revisit when |
| -------- | --- | ------------ |
| Two independent apps, no shared build | Simplest deploy story; each side evolves alone | A shared type/build contract is worth the coupling |
| Contract-first via `openapi.yaml` | One source of truth for the API; generated models can't drift | — (keep) |
| `Mono` on the generation path | Async generator slots in later with no signature churn | Never, unless generation stays permanently synchronous |
| Fixed template, not live/LLM generation | Ship the shape first; generation is the pluggable seam | Real generation is prioritized |
| No DB / auth / persistence | Requests are pure functions of input; nothing to store | A feature genuinely needs durable state |
| Local `useState`, no state library | One endpoint, one screen | A second screen or shared client state appears |

## Known gaps & intended evolution

The concrete, current gaps are tracked in [SPEC.md § Known gaps](SPEC.md) (CORS wide
open, hardcoded frontend URL, template-only generation, no rate/size limits). The
design intent for the big one: **real generation replaces the template inside
`MarkdownService` behind the existing `Mono` boundary** — the controller, contract,
and frontend shouldn't need to change for it. Security hardening (CORS origins, rate
limiting, request-size caps) is expected before any non-local deployment.

---

Keep this in sync when the architecture, layer boundaries, or a key decision above
changes. New feature work should fit these boundaries; if it can't, update the design
here first and say why.
