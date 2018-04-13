package dao;

import entity.App;
import entity.Record;
import entity.Rule;
import entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.DataSourceUtil;

import java.beans.PropertyVetoException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LogMonitorDao {

    private JdbcTemplate jdbcTemplate;


    public LogMonitorDao() throws PropertyVetoException {
        jdbcTemplate = new JdbcTemplate(DataSourceUtil.getDataSource());
    }

    public List<Rule> getRuleList() {
        String sql = "select * from rule where isValid = 1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Rule>(Rule.class));
    }

    public List<App> getAppList() {
        String sql = "select * from application where isOnline = 1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<App>(App.class));
    }

    public List<User> getUserList() {
        String sql ="select * from user where isValid = 1";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
    }

    public void saveRecord(Record record) {
        String sql = "INSERT INTO rule_record" +
                "(id,appId,ruleId,isEmail,isPhone,isComplete,noticeInfo,updateDate)" +
                " VALUES (?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(sql, UUID.randomUUID().toString(),
                record.getAppId(),
                record.getRuleId(),
                record.getIsEmail(),
                record.getIsPhone(),
                0,
                record.getNoticeInfo(),
                new Date());
    }

    public static void main(String[] args) throws Exception {
        LogMonitorDao dao = new LogMonitorDao();
        System.out.println(dao.getRuleList());
        System.out.println(dao.getAppList());
        System.out.println(dao.getUserList());
        Record record = new Record();
        record.setAppId(123);
        record.setRuleId(456);
        record.setNoticeInfo("测试");
        dao.saveRecord(record);
    }
}
