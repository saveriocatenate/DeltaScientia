# Trial API

All trial endpoints are nested under the experiment resource.

## Endpoints

### Create Trial

Creates a new trial with an optional list of measured variable values. If `trialNumber` is omitted, the next sequential number is auto-assigned.

- **HTTP Method**: `POST`
- **Path**: `/api/experiments/{experimentId}/trials`
- **Path Variable**: `experimentId` (Long)

**Request Body**:
```json
{
  "trialNumber": 1,
  "label": "Run A",
  "notes": "Normal operating conditions",
  "executionDate": "2025-04-01T09:00:00Z",
  "values": [
    { "variableId": 1, "valueNumeric": 22.5 },
    { "variableId": 2, "valueText": "7.4" },
    { "variableId": 3, "valueLongText": "Slight cloudiness observed" }
  ]
}
```

**Response — 201 Created**:
```json
{
  "id": 1,
  "experimentId": 1,
  "trialNumber": 1,
  "label": "Run A",
  "notes": "Normal operating conditions",
  "executionDate": "2025-04-01T09:00:00Z",
  "createdAt": "2025-04-01T10:00:00Z",
  "values": [
    { "id": 1, "variableId": 1, "variableName": "Temperature", "valueText": null, "valueNumeric": 22.5, "valueLongText": null },
    { "id": 2, "variableId": 2, "variableName": "pH Level", "valueText": "7.4", "valueNumeric": null, "valueLongText": null },
    { "id": 3, "variableId": 3, "variableName": "Observation", "valueText": null, "valueNumeric": null, "valueLongText": "Slight cloudiness observed" }
  ]
}
```

**Response — 400 Bad Request**: Invalid variable ID or value type mismatch.

**Response — 404 Not Found**: Experiment does not exist.

---

### Get Trial by ID

Retrieves a single trial with its measured values.

- **HTTP Method**: `GET`
- **Path**: `/api/experiments/{experimentId}/trials/{trialId}`

**Response — 200 OK**: Same structure as Create Trial response.

**Response — 404 Not Found**: Trial does not exist or does not belong to the experiment.

---

### Delete Trial

Deletes a trial by its ID. All measured values are also removed via cascade.

- **HTTP Method**: `DELETE`
- **Path**: `/api/experiments/{experimentId}/trials/{trialId}`

**Response — 204 No Content**: Trial deleted successfully.

**Response — 404 Not Found**: Trial does not exist or does not belong to the experiment.

---

### Search Trials

Finds trials within an experiment with composite filtering on metadata, execution date, and multiple variable values. Uses **POST with JSON body** because filters are structured and may contain nested arrays.

- **HTTP Method**: `POST`
- **Path**: `/api/experiments/{experimentId}/trials/search`
- **Path Variable**: `experimentId` (Long)
- **Query Parameters**: `page`, `size`, `sort` (standard pagination, optional)
- **Request Body** (optional, empty or null retrieves all trials):

```json
{
  "label": "Run",
  "fromDate": "2025-01-01T00:00:00Z",
  "toDate": "2025-04-01T23:59:59Z",
  "values": [
    { "variableTypeId": 1, "minValue": 20.0, "maxValue": 25.0 },
    { "variableTypeId": 2, "exactValue": 7.4 }
  ]
}
```

This query finds trials where:
- `label` contains "Run" (case-insensitive)
- `executionDate` is within the date range
- Variable type 1 (e.g. Temperature) has a value between 20.0 and 25.0
- Variable type 2 (e.g. pH) has an exact value of 7.4
- All conditions are AND'ed together

**Response — 200 OK**:
```json
{
  "content": [
    {
      "id": 1,
      "experimentId": 1,
      "trialNumber": 1,
      "label": "Run A",
      "notes": "Normal operating conditions",
      "executionDate": "2025-04-01T09:00:00Z",
      "createdAt": "2025-04-01T10:00:00Z",
      "values": [
        { "id": 1, "variableId": 1, "variableName": "Temperature", "valueText": null, "valueNumeric": 22.5, "valueLongText": null }
      ]
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

## Request Schema: TrialCreateRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `trialNumber` | `int` | No | Sequential trial number (auto-assigned when null) |
| `label` | `string` | No | Human-readable label |
| `notes` | `string` | No | Free-form observations |
| `executionDate` | `timestamp` | No | When the trial was executed |
| `values` | `TrialValueRequest[]` | No | Measured values per variable |

### TrialValueRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `variableId` | `long` | Yes | The experiment variable to record a value for |
| `valueText` | `string` | No | Short textual value |
| `valueNumeric` | `double` | No | Numeric measurement value |
| `valueLongText` | `string` | No | Extended observations or notes |

## Request Schema: TrialSearchRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `label` | `string` | No | Partial label match (case-insensitive, contains) |
| `fromDate` | `timestamp` | No | Inclusive start of execution date range |
| `toDate` | `timestamp` | No | Inclusive end of execution date range |
| `values` | `VariableValueFilter[]` | No | Composite value filters on multiple variables |

### VariableValueFilter

Each filter targets a specific VariableType and joins against `trial_value`. Multiple filters are AND'ed, requiring the trial to satisfy all.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `variableTypeId` | `long` | Yes | The variable type to filter by |
| `exactValue` | `double` | Conditional | Exact numeric match (when set, min/max are ignored) |
| `minValue` | `double` | Conditional | Inclusive lower bound on `value_numeric` |
| `maxValue` | `double` | Conditional | Inclusive upper bound on `value_numeric` |

## Response Schema: TrialResponse

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Trial database identifier |
| `experimentId` | `long` | Parent experiment ID |
| `trialNumber` | `int` | Sequential trial number |
| `label` | `string` | Trial display label |
| `notes` | `string` | Free-form observations |
| `executionDate` | `timestamp` | When the trial was executed |
| `createdAt` | `timestamp` | Creation timestamp |
| `values` | `TrialValueSummary[]` | Array of measured values |

### TrialValueSummary

| Field | Type | Description |
|-------|------|-------------|
| `id` | `long` | Value database identifier |
| `variableId` | `long` | The variable this value belongs to |
| `variableName` | `string` | Variable type name (for display) |
| `valueText` | `string` | Short textual value |
| `valueNumeric` | `double` | Numeric measurement value |
| `valueLongText` | `string` | Extended observations |

---

### List Trials

Returns a paginated list of all trials for an experiment, ordered by ID.

- **HTTP Method**: `GET`
- **Path**: `/api/experiments/{experimentId}/trials`

**Query Parameters**:

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `page` | `int` | `0` | Page number (0-based) |
| `size` | `int` | `20` | Items per page |
| `sort` | `string` | `id` | Sort field |

**Response — 200 OK**: Same paginated structure as the Search Trials response.
