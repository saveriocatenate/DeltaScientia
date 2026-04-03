# REST API Documentation

## Experiment Controller

### Get Experiment by ID

Retrieves all information for a specific experiment, including its variables and trials.

- **HTTP Method**: `GET`
- **Path**: `/api/experiments/{id}`
- **Path Variable**: `id` (Long) — experiment ID

**Query Parameters**: None

**Response — 200 OK**:
```json
{
  "id": 1,
  "name": "Temperature Study Q1",
  "description": "Analysis of temperature effects on catalyst A",
  "category": "thermodynamics",
  "status": "ACTIVE",
  "createdBy": "john.doe",
  "notes": "First batch of trials completed",
  "createdAt": "2025-04-01T10:00:00Z",
  "updatedAt": "2025-04-02T14:30:00Z"
}
```

**Response — 404 Not Found**:
```json
{
  "error": "Experiment not found",
  "id": 999
}
```

---

### Create Experiment

Creates a new experiment with an optional list of variables. Trials can be added separately after creation.

- **HTTP Method**: `POST`
- **Path**: `/api/experiments`
- **Request Body**:
```json
{
  "name": "Temperature Study Q1",
  "description": "Analysis of temperature effects on catalyst A",
  "category": "thermodynamics",
  "status": "ACTIVE",
  "createdBy": "john.doe",
  "notes": "Initial experiment setup",
  "variables": [
    {
      "name": "Temperature",
      "unitOfMeasure": "°C",
      "dataType": "NUMERIC",
      "description": "Ambient temperature"
    },
    {
      "name": "pH Level",
      "unitOfMeasure": "pH",
      "dataType": "NUMERIC",
      "description": "Solution acidity"
    }
  ]
}
```

**Response — 201 Created**:
```json
{
  "id": 1,
  "name": "Temperature Study Q1",
  "description": "Analysis of temperature effects on catalyst A",
  "category": "thermodynamics",
  "status": "ACTIVE",
  "createdBy": "john.doe",
  "notes": "Initial experiment setup",
  "createdAt": "2025-04-01T10:00:00Z",
  "updatedAt": "2025-04-01T10:00:00Z",
  "variables": [
    {
      "id": 1,
      "name": "Temperature",
      "unitOfMeasure": "°C",
      "dataType": "NUMERIC",
      "description": "Ambient temperature"
    },
    {
      "id": 2,
      "name": "pH Level",
      "unitOfMeasure": "pH",
      "dataType": "NUMERIC",
      "description": "Solution acidity"
    }
  ],
  "trials": []
}
```

**Response — 400 Bad Request** (missing required fields):
```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

---