-- Add language column to media_assets
ALTER TABLE media_assets ADD COLUMN language VARCHAR(100);

-- Create department_timeline_events table
CREATE TABLE department_timeline_events (
    id UUID PRIMARY KEY,
    department_id UUID NOT NULL,
    year VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    order_index INT DEFAULT 0,
    CONSTRAINT fk_dept_timeline_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);
