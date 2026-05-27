INSERT INTO employee_stockist_mappings (employee_id, stockist_id)
SELECT p.employee_id, p.id
FROM providers p
WHERE LOWER(p.type) = 'stockist'
  AND p.employee_id IS NOT NULL
ON CONFLICT (employee_id, stockist_id) DO NOTHING;
