-- Drop the old legacy column-based table
DROP TABLE IF EXISTS global_metrics CASCADE;

-- Create the new dynamic row-based table
CREATE TABLE global_metrics (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(255) NOT NULL,
    metric_value  VARCHAR(255) NOT NULL,
    icon          VARCHAR(255),
    display_order INTEGER DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
