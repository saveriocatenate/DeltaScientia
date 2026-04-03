# Experiment API

## Endpoints

### Get Experiment by ID

Retrieves all information for a specific experiment, including its variables and trials.

- **HTTP Method**: `GET`
- **Path**: `/api/experiments/{id}`
- **Path Variable**: `id` (Long) — experiment ID

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
  "updatedAt": "2025-04-02T14:30:00Z",
  "variables": [
    {
      "id": 1,
      "name": "Temperature",
      "unitOfMeasure": "°C",
      "dataType": "NUMERIC",
      "description": "Ambient temperature of the reaction chamber"
    }
  ],
  "trials": []
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

Creates a new experiment with an optional list of variable type references. Each variable entry must provide exactly one of `typeName` (reference existing type) or `name` (create a new custom type). When `name` is provided, optional `unitOfMeasure`, `dataType`, and `description` fields can define the new type's metadata.

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
      "typeName": "Temperature"
    },
    {
      "name": "Viscosity",
      "unitOfMeasure": "Pa·s",
      "dataType": "NUMERIC",
      "description": "Fluid viscosity measured with Viscometer X"
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
      "name": "Viscosity",
      "unitOfMeasure": "Pa·s",
      "dataType": "NUMERIC",
      "description": "Fluid viscosity measured with Viscometer X"
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

## Request Schema: ExperimentCreateRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | `string` | Yes | Experiment name |
| `description` | `string` | No | Purpose and scope |
| `category` | `string` | No | Categorisation tag |
| `status` | `string` | No | Lifecycle state |
| `createdBy` | `string` | No | Creator username |
| `notes` | `string` | No | Free-form notes |
| `variables` | `VariableRequest[]` | No | Variable type definitions |

### VariableRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `typeName` | `string` | Conditional | Name of an existing catalog type (case-insensitive). Mutually exclusive with `name` |
| `name` | `string` | Conditional | Name for a new custom type. Mutually exclusive with `typeName`. When absent, `name` is used as type reference |
| `unitOfMeasure` | `string` | No | Unit of measurement (only used when creating a new type) |
| `dataType` | `string` | No | Data type, defaults to `TEXT` if not provided (only used when creating a new type) |
| `description` | `string` | No | Type description (only used when creating a new type) |

## Response Schema: ExperimentResponse

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Database identifier |
| `name` | `string` | Experiment name |
| `description` | `string` | Purpose and scope |
| `category` | `string` | Categorisation tag |
| `status` | `string` | Lifecycle state |
| `createdBy` | `string` | Creator username |
| `notes` | `string` | Free-form notes |
| `createdAt` | `timestamp` | Creation time |
| `updatedAt` | `timestamp` | Last update time |
| `variables` | `VariableSummary[]` | Array of variable summaries |
| `trials` | `TrialSummary[]` | Array of trial summaries |

### VariableSummary

All fields derive from the linked VariableType:

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Variable database identifier |
| `name` | `string` | Type name |
| `unitOfMeasure` | `string` | Unit of measurement |
| `dataType` | `string` | Data type (e.g. `"NUMERIC"`) |
| `description` | `string` | Variable description |
