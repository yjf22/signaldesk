# SignalDesk

> A multi-source content monitoring, change tracking, and summarization platform built with Spring Boot, Vue 3, and MySQL.

SignalDesk is a practical full-stack project for collecting information from multiple sources, archiving content, tracking updates, reviewing fetch history, and generating summaries from captured data.

It is not just a simple crawler demo.  
The project is positioned as an information monitoring product, with crawling as one of its core capabilities.

## Why This Project

Many useful sources are scattered across websites, RSS feeds, PDFs, and personal notes.  
SignalDesk tries to solve three practical problems:

- collect important information into one workspace
- revisit sources automatically instead of checking manually
- keep searchable history and generate summaries from captured content

## Features

### Information source management

- Create and manage website, RSS, PDF, and note sources
- Edit source metadata, fetch interval, and tags
- Pause, resume, and delete sources
- Batch operations for source management

### Content collection and tracking

- Manual fetch and scheduled fetch
- Fetch task history and status display
- Parsed content archiving
- Recent content review
- Change tracking for monitored sources

### Reading and productivity

- Source detail page with latest captured content
- Search page for tracked content
- Summary generation workflow
- Dashboard for source overview and recent activity

## Tech Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Flyway
- MySQL 8
- Redis
- Jsoup
- JWT
- Maven

### Frontend

- Vue 3
- Vite
- TypeScript
- Pinia
- Vue Router
- Tailwind CSS

## Architecture

```text
User
  -> Vue 3 Frontend
  -> Spring Boot Backend
     -> MySQL
     -> Redis
     -> Elasticsearch / OpenSearch
     -> External content sources
```

## Project Structure

```text
.
├─ backend/                      Spring Boot backend
├─ frontend/                     Vue 3 frontend
├─ docs/                         Requirements and design documents
├─ docker/                       Container-related files
├─ create_signaldesk_database.sql
└─ README.md
```

## Core Modules

- `auth`: login, register, JWT authentication
- `source`: source management and source status control
- `crawltask`: manual and scheduled fetching
- `document`: parsed content storage and latest content view
- `dashboard`: overview statistics and recent changes
- `summary`: generated summaries for tracked content
- `search`: content retrieval and filtering
- `infrastructure`: common exception, response, and config handling

## Quick Start

### 1. Prepare dependencies

Make sure the following services are available locally:

- MySQL 8
- Redis
- Elasticsearch or OpenSearch

Default backend configuration is in [backend/src/main/resources/application.yml](C:\Users\28145\Documents\New project 3\backend\src\main\resources\application.yml).

Default local database connection:

- Host: `localhost`
- Port: `3307`
- Database: `signaldesk`
- Username: `root`

### 2. Create database

You can create the database manually:

```sql
CREATE DATABASE IF NOT EXISTS signaldesk
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Or use the provided script:

- [create_signaldesk_database.sql](C:\Users\28145\Documents\New project 3\create_signaldesk_database.sql)

### 3. Start backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on:

- `http://localhost:8080`

### 4. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on:

- `http://localhost:5173`

## Development Notes

- Flyway is enabled for schema migration
- Hibernate uses `ddl-auto: validate`
- MySQL 8 is the default local database
- Local development currently assumes Redis and Elasticsearch are available
- Some design documents in `docs/` may continue to evolve as the project is refined

## Documentation

Project documents are available in [docs/README.md](C:\Users\28145\Documents\New project 3\docs\README.md):

- Requirements analysis
- System design
- Database design
- Frontend design
- Scaffold notes

## Suggested GitHub Additions

You can improve the repository homepage later by adding:

- screenshots of dashboard, source list, and source detail pages
- deployment instructions
- API examples
- roadmap progress
- demo GIFs

## Resume Positioning

A better way to describe this project on a resume:

`SignalDesk: a multi-source content monitoring, change tracking, and summarization platform based on Spring Boot and Vue 3`

This is stronger than describing it only as a crawler, because the project also includes:

- source management
- task scheduling
- content archiving
- search workflow
- summary generation
- front-end management UI

## Roadmap

- Better article-level extraction for complex dynamic websites
- Stronger search experience
- Better summary quality and workflow
- Source grouping and filtering improvements
- Richer dashboard metrics and failure insights

## License

MIT
