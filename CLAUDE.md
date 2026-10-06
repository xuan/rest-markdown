# REST-MARKDOWN

Natural-language prompt → REST API markdown docs. Spring Boot backend + Next.js frontend.

Working conventions live in this file. The rest of the docs split by the question they
answer — see the map below.

## Documentation map

- **This file (CLAUDE.md)** — *how code is written*: conventions, build/test, and the
  index that routes to the others.
- **[docs/RULES.md](docs/RULES.md)** — *what the product must do*: business rules and
  functionality. **Source of truth for behavior** — code is written to satisfy it.
- **[docs/DESIGN.md](docs/DESIGN.md)** — *how it's built*: architecture, layer
  boundaries, key decisions, intended evolution.
- **[docs/SPEC.md](docs/SPEC.md)** — *what's actually shipped now*: exact API contract,
  request flow, config, known gaps. A derived reference, not intent.

**Precedence when docs disagree:** RULES wins on behavior, DESIGN on architecture,
CLAUDE on code style. SPEC records reality — if it contradicts RULES, that's a gap to
close by fixing code + SPEC, not RULES.

**Order for a feature:** read CLAUDE.md → RULES.md (what) → DESIGN.md (where it fits) →
implement per these conventions → update SPEC.md (and the RULES capability status). To
change behavior, edit RULES.md *first*, then implement.

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
