# Variable Type API

## Endpoints

### List Variable Types

Returns a paginated list of all variable types in the catalog (both standard and user-defined), ordered alphabetically by name. Useful for type selection UIs.

- **HTTP Method**: `GET`
- **Path**: `/api/variable-types`

**Query Parameters**:

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `page` | `int` | `0` | Page number (0-based) |
| `size` | `int` | `20` | Items per page |
| `sort` | `string` | `name` | Sort field (use comma-separated `field,direction`, e.g. `name,desc`) |

**Example Request**: `GET /api/variable-types?page=0&size=5&sort=name,asc`

**Response — 200 OK**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "pH Level",
      "unitOfMeasure": "pH",
      "dataType": "NUMERIC",
      "description": "Solution acidity/alkalinity measurement",
      "isCustom": false,
      "createdAt": "2025-04-01T08:00:00Z"
    },
    {
      "id": 3,
      "name": "Viscosity",
      "unitOfMeasure": "Pa·s",
      "dataType": "NUMERIC",
      "description": "Fluid dynamic viscosity",
      "isCustom": true,
      "createdAt": "2025-04-02T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 12,
  "totalPages": 3,
  "last": false
}
```

---

### Delete Variable Type

Deletes a variable type by its database identifier.

- **HTTP Method**: `DELETE`
- **Path**: `/api/variable-types/{id}`
- **Path Variable**: `id` (Long) — variable type ID

**Response — 204 No Content**: Variable type deleted successfully.

**Response — 400 Bad Request**: Variable type does not exist.

## Response Schema: VariableTypeResponse

| Field | Type | Description |
|-------|------|-------------|
| `content` | `VariableTypeSummary[]` | Page of variable types |
| `page` | `int` | Current page number (0-based) |
| `size` | `int` | Page size requested |
| `totalElements` | `long` | Total number of variable types |
| `totalPages` | `int` | Total pages available |
| `last` | `boolean` | Whether this is the last page |

### VariableTypeSummary

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Type database identifier |
| `name` | `string` | Type name |
| `unitOfMeasure` | `string` | Unit of measurement (e.g. "°C", "pH") |
| `dataType` | `string` | Declared data type (e.g. "NUMERIC", "TEXT") |
| `description` | `string` | Type description |
| `isCustom` | `boolean` | `true` if user-defined, `false` if catalog type |
| `createdAt` | `timestamp` | When the type was registered |
