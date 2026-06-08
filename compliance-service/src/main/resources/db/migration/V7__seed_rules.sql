INSERT INTO compliance_schema.compliance_rules
VALUES (

gen_random_uuid(),

'OVERDUE',

'due_date < now()'

);