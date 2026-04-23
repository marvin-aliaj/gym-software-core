package com.spring.jdbc.gym.management.dao;

import com.spring.jdbc.gym.management.model.ActiveMembersMetric;
import com.spring.jdbc.gym.management.model.TimeSeriesData;
import com.spring.jdbc.gym.management.model.filter.DashboardFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DashboardDao {
    private final JdbcTemplate jdbcTemplate;

    public DashboardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ActiveMembersMetric getActiveMembersNow(DashboardFilter filter) throws Exception {
        String sql = """
            SELECT 
                COUNT(DISTINCT CASE WHEN al.exit_time IS NULL THEN al.user_id END) as active_now,
                COUNT(DISTINCT ub.user_id) as total_members
            FROM user_businesses ub
            LEFT JOIN attendance_logs al ON al.user_id = ub.user_id 
                AND al.gym_id = ub.business_id 
                AND al.exit_time IS NULL
            WHERE 1=1
            """ + (filter.hasBusinessId() ? " AND ub.business_id = ?::uuid" : "");

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ActiveMembersMetric metric = new ActiveMembersMetric();
                metric.setCurrent(rs.getLong("active_now"));
                metric.setTotal(rs.getLong("total_members"));
                return metric;
            }, filter.hasBusinessId() ? new Object[]{filter.getBusinessId()} : new Object[]{});
        } catch (Exception e) {
            throw new Exception("Error fetching active members: " + e.getMessage());
        }
    }

    public Long getCheckInsForPeriod(DashboardFilter filter, String startDate, String endDate) throws Exception {
        String sql = """
            SELECT COUNT(*) as check_ins
            FROM attendance_logs
            WHERE entry_time::date >= ?::date
                AND entry_time::date <= ?::date
            """ + (filter.hasBusinessId() ? " AND gym_id = ?::uuid" : "");

        try {
            List<Object> params = new ArrayList<>();
            params.add(startDate);
            params.add(endDate);
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            Long result = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return result != null ? result : 0L;
        } catch (Exception e) {
            throw new Exception("Error fetching check-ins: " + e.getMessage());
        }
    }

    public Long getRevenueForPeriod(DashboardFilter filter, String startDate, String endDate) throws Exception {
        String sql = """
            SELECT COALESCE(
                (SELECT SUM(mp.price_cents) 
                 FROM subscriptions s
                 JOIN membership_plans mp ON s.plan_id = mp.id
                 WHERE s.start_date >= ?::date 
                   AND s.start_date <= ?::date
                   """ + (filter.hasBusinessId() ? " AND s.business_id = ?::uuid" : "") + """
                ), 0) +
                COALESCE(
                (SELECT SUM(o.total_amount)
                 FROM orders o
                 WHERE o.cDate::date >= ?::date 
                   AND o.cDate::date <= ?::date
                   """ + (filter.hasBusinessId() ? " AND o.business_id = ?::uuid" : "") + """
                ), 0) as total_revenue
            """;

        try {
            List<Object> params = new ArrayList<>();
            params.add(startDate);
            params.add(endDate);
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }
            params.add(startDate);
            params.add(endDate);
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            Long result = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return result != null ? result : 0L;
        } catch (Exception e) {
            throw new Exception("Error fetching revenue: " + e.getMessage());
        }
    }

    public Long getTotalClientsForPeriod(DashboardFilter filter, String endDate) throws Exception {
        String sql = """
            SELECT COUNT(DISTINCT s.user_id) as total_clients
            FROM subscriptions s
            WHERE s.is_active = true
                AND s.start_date <= ?::date
                AND s.end_date >= ?::date
            """ + (filter.hasBusinessId() ? " AND s.business_id = ?::uuid" : "");

        try {
            List<Object> params = new ArrayList<>();
            params.add(endDate);
            params.add(endDate);
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            Long result = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return result != null ? result : 0L;
        } catch (Exception e) {
            throw new Exception("Error fetching total clients: " + e.getMessage());
        }
    }

    public Long getNewClients(DashboardFilter filter) throws Exception {
        String sql = """
            SELECT COUNT(DISTINCT u.id) as new_clients
            FROM users u
            JOIN user_businesses ub ON u.id = ub.user_id
            WHERE u.enrollment_date >= ?::date
                AND u.enrollment_date <= ?::date
                AND u.role = 'Client'
            """ + (filter.hasBusinessId() ? " AND ub.business_id = ?::uuid" : "");

        try {
            List<Object> params = new ArrayList<>();
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            Long result = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
            return result != null ? result : 0L;
        } catch (Exception e) {
            throw new Exception("Error fetching new clients: " + e.getMessage());
        }
    }

    public List<TimeSeriesData> getRevenueTimeSeries(DashboardFilter filter) throws Exception {
        String sql = """
            WITH date_series AS (
                SELECT generate_series(
                    ?::date,
                    ?::date,
                    '1 day'::interval
                )::date AS date
            ),
            subscription_revenue AS (
                SELECT 
                    s.start_date::date AS date,
                    SUM(mp.price_cents) AS revenue
                FROM subscriptions s
                JOIN membership_plans mp ON s.plan_id = mp.id
                WHERE s.start_date >= ?::date 
                  AND s.start_date <= ?::date
                  """ + (filter.hasBusinessId() ? " AND s.business_id = ?::uuid" : "") + """
                GROUP BY s.start_date::date
            ),
            order_revenue AS (
                SELECT 
                    o.cDate::date AS date,
                    SUM(o.total_amount) AS revenue
                FROM orders o
                WHERE o.cDate::date >= ?::date 
                  AND o.cDate::date <= ?::date
                  """ + (filter.hasBusinessId() ? " AND o.business_id = ?::uuid" : "") + """
                GROUP BY o.cDate::date
            )
            SELECT 
                ds.date::text AS date,
                COALESCE(sr.revenue, 0) + COALESCE(or_rev.revenue, 0) AS value
            FROM date_series ds
            LEFT JOIN subscription_revenue sr ON ds.date = sr.date
            LEFT JOIN order_revenue or_rev ON ds.date = or_rev.date
            ORDER BY ds.date
            """;

        try {
            List<Object> params = new ArrayList<>();
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                TimeSeriesData data = new TimeSeriesData();
                data.setDate(rs.getString("date"));
                data.setValue(rs.getLong("value"));
                return data;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception("Error fetching revenue time series: " + e.getMessage());
        }
    }

    public List<TimeSeriesData> getSubscriptionTimeSeries(DashboardFilter filter) throws Exception {
        String sql = """
            WITH date_series AS (
                SELECT generate_series(
                    ?::date,
                    ?::date,
                    '1 day'::interval
                )::date AS date
            )
            SELECT 
                ds.date::text AS date,
                COALESCE(COUNT(s.id), 0) AS value
            FROM date_series ds
            LEFT JOIN subscriptions s ON s.start_date = ds.date
                """ + (filter.hasBusinessId() ? " AND s.business_id = ?::uuid " : "") + """
            GROUP BY ds.date
            ORDER BY ds.date
            """;

        try {
            List<Object> params = new ArrayList<>();
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                TimeSeriesData data = new TimeSeriesData();
                data.setDate(rs.getString("date"));
                data.setValue(rs.getLong("value"));
                return data;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception("Error fetching subscription time series: " + e.getMessage());
        }
    }

    public List<TimeSeriesData> getRushHours(DashboardFilter filter) throws Exception {
        String sql = """
            SELECT 
                EXTRACT(HOUR FROM entry_time) AS hour,
                COUNT(*) AS check_ins
            FROM attendance_logs
            WHERE entry_time::date >= ?::date
                AND entry_time::date <= ?::date
            """ + (filter.hasBusinessId() ? " AND gym_id = ?::uuid " : "") + """
            GROUP BY EXTRACT(HOUR FROM entry_time)
            ORDER BY hour
            """;

        try {
            List<Object> params = new ArrayList<>();
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                TimeSeriesData data = new TimeSeriesData();
                int hour = rs.getInt("hour");
                data.setLabel(String.format("%02d:00", hour));
                data.setValue(rs.getLong("check_ins"));
                return data;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception("Error fetching rush hours: " + e.getMessage());
        }
    }

    public List<TimeSeriesData> getClientGrowthTimeSeries(DashboardFilter filter) throws Exception {
        String sql = """
            WITH date_series AS (
                SELECT generate_series(
                    ?::date,
                    ?::date,
                    '1 day'::interval
                )::date AS date
            )
            SELECT 
                ds.date::text AS date,
                COALESCE(COUNT(u.id), 0) AS value
            FROM date_series ds
            LEFT JOIN users u ON u.enrollment_date = ds.date AND u.role = 'Client'
            """ + (filter.hasBusinessId() ? 
                " LEFT JOIN user_businesses ub ON u.id = ub.user_id AND ub.business_id = ?::uuid" : "") + """
            """ + (filter.hasBusinessId() ? " WHERE ub.business_id IS NOT NULL " : "") + """
            GROUP BY ds.date
            ORDER BY ds.date
            """;

        try {
            List<Object> params = new ArrayList<>();
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            if (filter.hasBusinessId()) {
                params.add(filter.getBusinessId());
            }

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                TimeSeriesData data = new TimeSeriesData();
                data.setDate(rs.getString("date"));
                data.setValue(rs.getLong("value"));
                return data;
            }, params.toArray());
        } catch (Exception e) {
            throw new Exception("Error fetching client growth: " + e.getMessage());
        }
    }
}
