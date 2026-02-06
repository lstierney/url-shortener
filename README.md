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
* REST API defined by OpenAPI ([openapi.yaml](https://github.com/lstierney/url-shortener/blob/main/backend/openapi.yaml))
* Lightweight decoupled React TypeScript frontend
* Automated tests
* Dockerised for easy local running

---

## Tech Stack
### Backend
* Java 17 (Spring Boot)
* REST API (following openapi.yaml as above)
* Persistence via embedded database (H2)
* JUnit, Mockito and Spring Tests for testing

### Frontend
* React (Vite)
* Single page form-based UI
* API error handling surfaced to the user
* Vitest used for testing.

### Infrastructure
* Docker
* Docker compose

---

## API Overview

The API allows clients (including the frontend) to:

* Create a shortened URL (with random or custom alias)
* Retrieve a List of shortened URLs
* Delete a shortened URL

All endpoints, request/response models, and error cases are defined in
[openapi.yaml](https://github.com/lstierney/url-shortener/blob/main/backend/openapi.yaml) and implemented accordingly.

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

### Accessing the UI
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

## Notes
* Alias uniqueness is enforced at the database level
* URLs are validated before persistence
* Data is persisted using an embedded H2 database stored on disk
* Frontend is intentionally minimal
* If a unique, random shortened URL cannot be created after 5 attempts (unlikely) an error is returned.

## Out of Scope
* Authentication

