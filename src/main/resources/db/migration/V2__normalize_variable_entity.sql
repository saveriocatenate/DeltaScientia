-- Normalize Variable entity: remove columns that are redundant with VariableType.
-- All metadata (name, unit_of_measure, data_type, description) now derives from variable_type.

-- Make variable_type_id mandatory (was previously nullable)
UPDATE variable SET variable_type_id = 0 WHERE variable_type_id IS NULL;
ALTER TABLE variable ALTER COLUMN variable_type_id SET NOT NULL;

-- Dead columns
ALTER TABLE variable DROP COLUMN IF EXISTS name;
ALTER TABLE variable DROP COLUMN IF EXISTS unit_of_measure;
ALTER TABLE variable DROP COLUMN IF EXISTS data_type;
ALTER TABLE variable DROP COLUMN IF EXISTS description;

-- Index on the now-removed column
DROP INDEX IF EXISTS idx_variable_experiment_data_type;
