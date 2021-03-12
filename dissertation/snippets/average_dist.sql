SELECT COUNT(*)
FROM (
    SELECT AVG(est_distance) AS avg_val
    FROM interaction AS i, rssi AS r
    WHERE i.receiver = :receiver AND i.receiver = r.receiver_ref AND  i.sender = r.sender_ref AND i.start_time = r.start_time_ref
    GROUP BY i.sender, i.receiver, i.start_time
)
WHERE avg_val >= :start_range AND avg_val < :end_range