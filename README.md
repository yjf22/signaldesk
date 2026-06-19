# SignalDesk

> A full-stack content monitoring and summarization platform built with Spring Boot 3, Vue 3, and MySQL 8.

SignalDesk is a practical project for collecting information from websites, RSS feeds, PDFs, and notes into one workspace. It supports scheduled fetching, content archiving, search, summary generation, and source lifecycle management.

## Project Highlights

- Multi-source ingestion: URL, RSS, PDF, and note sources
- Scheduled and manual fetching with task history
- Latest content review and fetch result tracking
- Search over captured content
- One-click summary generation for monitored information
- Source pause, resume, delete, and batch management
- Dashboard for recent activity and source overview

## Screenshots

### Dashboard

![Dashboard](./docs/screenshots/dashboard.png)

### Source List

![Source List](./docs/screenshots/source-list.png)

### Source Detail

![Source Detail](./docs/screenshots/source-detail.png)

### Search / Summary

![Search Summary](./docs/screenshots/search-summary.png)

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

## Core Features

### Source Management

- Create and edit monitored sources
- Support URL, RSS, PDF, and note types
- Configure fetch interval and tags
- Pause, resume, and delete sources
- Batch operations in the source list

### Content Monitoring

- Manual fetch and scheduled fetch
- Fetch history and task status display
- Parsed content storage and latest content view
- Change tracking for monitored sources
- Direct jump to original captured content

### Search and Summary

- Search captured content by keyword
- Filter by source type
- Review captured snippets quickly
- Generate summaries from stored content

### Dashboard

- View today's new captured documents
- View active sources and recent fetch activity
- Quickly navigate to key monitored content

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

## Resume Positioning

A strong resume description for this project is:

`SignalDesk: a multi-source content monitoring, change tracking, and summarization platform based on Spring Boot 3 and Vue 3.`

Compared with describing it only as a crawler, this framing better reflects the complete product capability:

- source management
- scheduled tasks
- content archiving
- search workflow
- summary generation
- admin-style frontend interface

## Roadmap

- Better article-level extraction for complex websites
- Stronger search and filtering capability
- Better summary quality and workflow
- Source grouping and tagging improvements
- Richer dashboard metrics and fetch diagnostics

## License

MIT
