# SignalDesk

> A personal intelligence workspace for tracking websites, RSS feeds, PDFs, and notes in one place, with search and AI summaries built in.

SignalDesk helps individual researchers, analysts, and curious builders keep important information sources under watch. Instead of scattering links across bookmarks, notes, and RSS readers, you can collect sources into one workspace, fetch updates on a schedule, archive captured content, search old material, and generate summaries from what you have stored.

## Why SignalDesk

- Track multiple source types in one product: URL, RSS, PDF, and notes
- Keep a searchable archive instead of only reading the latest update
- Review fetch activity, change history, and source status from one dashboard
- Generate summaries from captured content without leaving the workspace
- Manage source lifecycle with pause, resume, edit, delete, and batch actions

## Typical Workflow

1. Add a source you want to monitor.
2. Fetch content manually or on a schedule.
3. Review archived content and recent changes.
4. Search historical material by keyword.
5. Generate summaries from the stored results.

## Screenshots

### Dashboard

![Dashboard](./docs/screenshots/dashboard.png)

### Source List

![Source List](./docs/screenshots/source-list.png)

### Source Detail

![Source Detail](./docs/screenshots/source-detail.png)

### Search / Summary

![Search Summary](./docs/screenshots/search-summary.png)

## Core Features

### Source Management

- Create and edit monitored sources
- Support URL, RSS, PDF, and note types
- Configure fetch interval and tags
- Pause, resume, and delete sources
- Perform batch actions from the source list

### Content Monitoring

- Manual fetch and scheduled fetch
- Fetch history and task status display
- Parsed content storage and latest content review
- Change tracking for monitored sources
- Direct jump to original captured content

### Search and Summary

- Search captured content by keyword
- Filter by source type
- Review snippets from stored content quickly
- Generate summaries from search results or source content

### Dashboard

- View recent captured documents
- View active sources and recent fetch activity
- Jump quickly to monitored content and source pages

## Tech Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Flyway
- MySQL 8
- Redis
- Elasticsearch
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
     -> Elasticsearch
     -> External content sources
```

## Project Structure

```text
.
|- backend/                        Spring Boot backend
|- frontend/                       Vue 3 frontend
|- docs/                           Requirements and design documents
|- docker/                         Container-related files
|- create_signaldesk_database.sql  Database bootstrap script
`- README.md
```

## Quick Start

### 1. Prepare dependencies

Make sure these services are available locally:

- MySQL 8
- Redis
- Elasticsearch

Default configuration file:

- [backend/src/main/resources/application.yml](./backend/src/main/resources/application.yml)

### 2. Create database

You can either run the SQL below manually:

```sql
CREATE DATABASE IF NOT EXISTS signaldesk
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Or use the provided script:

- [create_signaldesk_database.sql](./create_signaldesk_database.sql)

### 3. Start backend

```bash
cd backend
mvn spring-boot:run
```

Backend default address:

- `http://localhost:8080`

### 4. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend default address:

- `http://localhost:5173`

## Default Local Setup

- MySQL host: `127.0.0.1`
- MySQL port: `3307`
- Database: `signaldesk`
- Username: `root`

## Documentation

Project documents are available in:

- [docs/README.md](./docs/README.md)

They include:

- requirements analysis
- system design
- database design
- frontend design
- project scaffold notes

## Use Cases

SignalDesk is a good fit for scenarios like:

- Tracking specific websites for updates
- Following RSS feeds alongside manually curated sources
- Monitoring saved PDFs and research notes in the same workspace
- Building a personal research archive with search and summaries

## Roadmap

- Better article-level extraction for complex websites
- Stronger search and filtering capability
- Better summary quality and workflow
- Source grouping and tagging improvements
- Richer dashboard metrics and fetch diagnostics
- Easier local setup with containerized dependencies

## License

MIT
