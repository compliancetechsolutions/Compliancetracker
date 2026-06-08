CREATE VIEW compliance_schema.dashboard_summary
AS

SELECT

status,

COUNT(*) total

FROM compliance_schema.compliance_records

GROUP BY status;