# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DeltaScientia is an AI-powered research assistant designed to track, compare, and analyze scientific trials. It leverages Gemini AI to identify patterns and provide actionable insights across experimental variables.

## Tech Stack

- Backend: Java 21, Spring Boot 3.4.1 (Maven)
- Frontend: Python 3.14.3, Streamlit
- Database: H2 Database (Embedded mode, file-based persistence)
- Migrations: Flyway (schema management via `src/main/resources/db/migration/`)
- AI: Google Gemini API tramite WebClient/Spring AI
- Tunnel/Deploy: Pinggy (for remote access)

## Environment Setup

Configuration is stored in `openrouter.env`:
- `ANTHROPIC_BASE_URL` — points to `https://openrouter.ai/api`
- `ANTHROPIC_API_KEY` — OpenRouter API key
- `ANTHROPIC_MODEL` — model identifier

Source `openrouter.env` before running the application to load environment variables.

## Development Conventions
- Naming: - Java: PascalCase for classes (ExperimentService), camelCase for variables and methods.
- Python: snake_case for scripts and functions.
- Database: snake_case for table and column naming (managed via Flyway migrations).
- Architecture: Standard Spring Boot Layered Architecture: Controller → Service → Repository → Entity.
- Data Transfer: Strict use of DTOs (Data Transfer Objects) for communication between Backend and Frontend.
- Boilerplate: Use Lombok (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) to keep code clean.
- Commit Messages: Follow Conventional Commits (e.g., feat: implement excel parsing, fix: h2 connection string).
- Language: Code, Javadoc comments, and technical documentation must be in English. User-facing AI insights and logs may be in Italian if requested by the end-user.
- Javadoc: Every new or modified class, method, and field must include Javadoc. Update existing Javadoc when changing signatures or behavior. Public and protected members are mandatory; package-private and private are encouraged for non-trivial logic.
- Pre-commit: Before every commit, scan all untracked files against `.gitignore` for credentials, generated artifacts, IDE configs, and build output. Add missing patterns if any are found.

## Critical Paths
- Documentation: `documentation/` (per-entity API contracts, ER diagrams)
- Database Migrations: `src/main/resources/db/migration/`
- JPA Entities: `src/main/java/it/deltascientia/model/`
- Business Logic: `src/main/java/it/deltascientia/service/`
- REST API Endpoints: `src/main/java/it/deltascientia/controller/`
- DTOs: `src/main/java/it/deltascientia/dto/`
- Unit/Integration Tests: `src/test/java/` (JUnit 5 + Mockito)

## Architecture Notes

### Variable Type Catalog
- `Variable` is a normalized join table linking an Experiment to a `VariableType`. It contains no metadata fields (name, unit, dataType, description) — all derive from the linked `VariableType`.
- `VariableTypeService` handles catalog operations: type resolution during experiment creation and paginated listing.
- `VariableTypeCatalogService` bootstraps the seed catalog from `variable-types.json` on first startup.
- Custom variable types can be created inline during experiment creation via the `name` field in `VariableRequest`.

### API Documentation
- One markdown file per entity under `documentation/` (e.g., `API-experiment.md`, `API-variable-type.md`).
- `API.md` is an index linking to the per-entity docs.
- `database-er-diagram.md` contains the Mermaid ER diagram.

## Guidelines & Constraints (What NOT to do)
- No In-Memory DB: Never use H2 in-memory mode. Data must persist in a local file (e.g., ./data/deltascientia_db).
- No Logic in Controllers: Business logic must reside in the Service layer.
- No Hardcoded Secrets: Never commit API keys. Use environment variables.
- No Manual SQL Outside Flyway: Schema changes must go through numbered Flyway migrations. Use Spring Data JPA Query Methods for data access. Use @Query only for complex analytical queries.
- Test Coverage: Every new Service method must have a corresponding JUnit test case.
- No Commit configuration: Never commit openrouter.env and settings.json file

## Project Status

Active development phase. Core infrastructure in place:
- Experiment CRUD with VariableType resolution
- VariableType catalog with paginated listing API
- Flyway-manated schema (baseline + normalization migration)
- H2 file-based persistence with HikariCP connection pooling
