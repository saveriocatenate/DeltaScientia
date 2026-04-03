# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DeltaScientia is an AI-powered research assistant designed to track, compare, and analyze scientific trials. It leverages Gemini AI to identify patterns and provide actionable insights across experimental variables.

## Tech Stack

- Backend: Java 21, Spring Boot 3.4.1 (Maven)
- Frontend: Python 3.14.3, Streamlit
- Database: H2 Database (Embedded mode, file-based persistence)
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
- Database: snake_case for table and column naming (managed via JPA).
- Architecture: Standard Spring Boot Layered Architecture: Controller → Service → Repository → Entity.
- Data Transfer: Strict use of DTOs (Data Transfer Objects) for communication between Backend and Frontend.
- Boilerplate: Use Lombok (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) to keep code clean.
- Commit Messages: Follow Conventional Commits (e.g., feat: implement excel parsing, fix: h2 connection string).
- Language: Code, Javadoc comments, and technical documentation must be in English. User-facing AI insights and logs may be in Italian if requested by the end-user.
- Javadoc: Every new or modified class, method, and field must include Javadoc. Update existing Javadoc when changing signatures or behavior. Public and protected members are mandatory; package-private and private are encouraged for non-trivial logic.
- Pre-commit: Before every commit, scan all untracked files against `.gitignore` for credentials, generated artifacts, IDE configs, and build output. Add missing patterns if any are found.

## Critical Paths
- Documentation: documentation/ (API contracts, ER diagrams)
- JPA Entities: src/main/java/it/deltascientia/model/
- Business Logic: src/main/java/it/deltascientia/service/
- REST API Endpoints: src/main/java/it/deltascientia/controller/
- Streamlit Dashboard: src/main/python/dashboard.py
- Unit/Integration Tests: src/test/java/ (JUnit 5 + Mockito)

## Guidelines & Constraints (What NOT to do)
- No In-Memory DB: Never use H2 in-memory mode. Data must persist in a local file (e.g., ./data/deltascientia_db).
- No Logic in Controllers: Excel parsing, delta calculations, and AI prompting logic must reside in the Service layer.
- No Hardcoded Secrets: Never commit the Gemini API Key. Use environment variables or an ignored application.properties.
- No Manual SQL: Rely on Spring Data JPA Query Methods. Use @Query only for complex analytical aggregations.
- Test Coverage: Every new Service method must have a corresponding JUnit test case.
- No Commit configuration: Never commit openrouter.env and settings.json file

## Project Status

This repository is in early setup — no source code or build configuration has been committed yet.
