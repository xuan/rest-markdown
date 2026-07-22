# REST-MARKDOWN

Natural-language prompt → REST API markdown docs. Spring Boot backend + Next.js frontend.

Working conventions live here; the spec (API contract, request flow, config, gaps)
is [docs/SPEC.md](docs/SPEC.md) — keep it current when behavior changes.

## Backend (Spring Boot 4.1, Java 25, Maven)

- Package root `com.xuan.restmarkdown`; layer `controller/` → `service/`. Keep
  controllers thin (request/response mapping); logic goes in `service/`.
- Request/response models are generated from `openapi.yaml`. Never hand-edit
  generated sources or anything under `target/`; to change the contract, edit
  `openapi.yaml` and regenerate (`mvn generate-sources` or a full build) before
  touching controller/service code.
- Prefer Lombok over boilerplate: `@Data`/`@Builder` on generated models;
  `@RequiredArgsConstructor`, `@Slf4j` in hand-written classes.
- Constructor injection via `@RequiredArgsConstructor` + `final` fields — no field
  `@Autowired`.
- Validate request bodies with `@Valid` + `jakarta.validation` constraints.
- Reactive return types (`Mono<T>`) on the generation path; don't mix reactive and
  blocking arbitrarily within a class.
- Static settings in `application.yaml`. No secret/external config currently — if
  added, read via `@Value` with a safe default and never commit it.
- Log via `@Slf4j` with parameterized messages (`log.info("...{}", x)`), not
  concatenation.

### Build & test

```bash
cd backend && ./mvnw spring-boot:run   # run
cd backend && ./mvnw test              # test (MockMvc + @SpringBootTest)
```

Add controller tests under `src/test/java/...` following `MarkdownControllerTest`.

## Frontend (Next.js 14, App Router, Tailwind, TS)

- Client components (`'use client'`) with local `useState` — no global state or
  data-fetching library. Talks to the backend via plain `fetch`. Style with Tailwind
  utilities; no component library.

```bash
cd frontend && npm run dev
```

## General

- Match existing patterns (Mono, Lombok, MockMvc) rather than introducing a new
  library or paradigm for a one-off change.
