# URL Shortener
A simple URL shortener service with a RESTful API and a decoupled web frontend.

Built as a coding exercise to demonstrate clean API design, persistence, testing, and containerisation.

---

## Features

* Shorten a full URL to a randomly generated alias
* Optional custom alias support
* Redirect from short URL to original URL
* Delete shortened URLs
* Persistent storage across restarts
* REST API defined by OpenAPI ([openapi.yaml](openapi.yaml))
* Lightweight decoupled React TypeScript frontend
* Automated tests
* Dockerised for easy local running

---

## Tech Stack
### Backend
* Java 17 (Spring Boot)
* REST API (OpenAPI‑driven)
* Embedded H2 database
* JUnit, Mockito, Spring Boot Test

### Frontend
* React (Vite)
* TypeScript
* React Testing Library
* Vitest

### Infrastructure
* Docker
* Docker Compose

---

## API Overview

The API allows clients (including the frontend) to:

* Create a shortened URL (with random or custom alias)
* Retrieve a list of shortened URLs
* Delete a shortened URL
* Redirect from a short URL to the original URL

All endpoints, request/response models, and error cases are defined in
[openapi.yaml](openapi.yaml) and implemented accordingly.

---

## Running Locally
### Prerequisites
* Git
* Docker Compose

### Starting the Docker container

```
git clone https://github.com/lstierney/url-shortener
cd url-shortener
docker compose up --build
```

## Accessing the UI
Once the Docker container has started you can access the UI at:
[http://localhost:5173](http://localhost:5173)

### Frontend Usage
#### Creating a shortened URL
* Enter a full URL and optionally enter a custom alias in the "Create a short link" form. If you do not enter a custom alias a random one will be created
    * Validation Rules:
        * Full URL must be a valid URL
        * Custom Alias may only contain letters, numbers, hyphens, and underscores
        * Custom Alias must be 20 characters or fewer
        * Custom Alias must be unique
* Submit to receive a shortened URL
#### Deleting a shortened URL
* Once one or more shortened URL have been created they will be shown in the "Your short links" table.
* Use the delete button to delete
* Click the shortened URL to navigate to the actual webpage.
#### Errors
* Errors will be displayed inline in the UI.

---

## Testing Locally
Backend and frontend tests run independently, so each part of the system can be tested without starting the full stack.

### Backend
#### Prerequisites
* JDK 17 or newer
* Maven 3 or newer

#### Running

```
cd backend
mvn clean test
```

### Frontend
#### Prerequisites
* Node.js 24.13.0 (tested)
* npm 11.6.2 (tested)

Note: Other recent Node versions may work, but the above versions were used during development and testing.

#### Running

```
cd frontend
npm install
npm run test
```
---

## Notes
* Alias uniqueness is enforced at the database level
* URLs are validated before persistence
* Data is persisted using an embedded H2 database stored on disk (in memory for testing)
* Frontend is intentionally minimal
* If a unique, random shortened URL cannot be created after 5 attempts (unlikely) an error is returned.

## Out of Scope
* Authentication and user accounts
* End‑to‑end UI tests (Playwright/Selenium)
* CI/CD pipelines or automated build/deployment workflows
* Rate limiting or abuse protection
* Analytics or click‑tracking for short URLs
* Custom domain support
* URL expiry or time‑limited links
* Horizontal scaling or distributed storage
* Production‑grade security hardening (HTTPS, secrets management, etc.) 

