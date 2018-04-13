package utils;

import dao.LogMonitorDao;
import entity.*;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorHandler {

    //key=appId
    private static Map<String, List<Rule>> ruleMap;
    //key=appId
    private static Map<String, List<User>> userMap;
    private static List<App> appList;
    private static List<User> userList;
    private static LogMonitorDao dao;

    private static boolean reloaded = false;

    static {
        try {
            dao = new LogMonitorDao();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            System.exit(1);
        }
        load();
    }

    public static Message parser(String line) {
        String[] messageArr = line.split("\\$\\$\\$\\$\\$");

        if(messageArr.length != 2)
            return null;

        if(StringUtils.isBlank(messageArr[0])||StringUtils.isBlank(messageArr[1]))
            return null;

        if(appIdIsValid(messageArr[0])) {
            Message message = new Message();
            message.setAppId(messageArr[0].trim());
            message.setLine(messageArr[1]);
            return message;
        }
        return null;
    }

    public static boolean trigger(Message message) {
        if(ruleMap == null) {
            load();
        }

        List<Rule> rules = ruleMap.get(message.getAppId());
        for(Rule rule : rules) {
            if(message.getLine().contains(rule.getKeyword())) {
                message.setRuleId(Integer.toString(rule.getId()));
                message.setKeyword(rule.getKeyword());
                return true;
            }
        }
        return false;
    }

    public static void notify(String appId, Message message) {
        List<User> users = getUserIdsByAppId(appId);

        if(sendEmail(appId, users, message)) {
            message.setIsEmail(1);
        }

        if(sendSms(appId, users, message)) {
            message.setIsPhone(1);
        }
    }

    public static void scheduleLoad() {
        String date = DateUtils.getDateTime();
        int now = Integer.parseInt(date.split(":")[1]);
        if(now % 10 == 0) {
            reloadDataModel();
        } else {
            reloaded = true;
        }
    }

    public static synchronized void reloadDataModel() {
        if(reloaded) {
            long start = System.currentTimeMillis();
            userList = loadUserList();
            appList = loadAppList();
            ruleMap = loadRuleMap();
            userMap = loadUserMap();
            reloaded = false;
        }
    }

    public static void save(Record record) {
        dao.saveRecord(record);
    }

    private static boolean appIdIsValid(String appId) {
        for(App app : appList) {
            if(Integer.toString(app.getId()).equals(appId)){
                return app.getIsOnline() == 1;
            }
        }
        return false;
    }

    private static synchronized void load() {
        if(userList == null) {
            userList = loadUserList();
        }

        if(appList == null) {
            appList = loadAppList();
        }

        if(ruleMap == null) {
            ruleMap = loadRuleMap();
        }

        if(userMap == null) {
            userMap = loadUserMap();
        }
    }

    private static List<User> loadUserList() {
        return dao.getUserList();
    }

    private static List<App> loadAppList() {
        return dao.getAppList();
    }

    private static Map<String,List<Rule>> loadRuleMap() {
        Map<String, List<Rule>> map = new HashMap<>();
        List<Rule> ruleList = dao.getRuleList();

        for(Rule rule : ruleList) {
            List<Rule> ruleListByAppId = map.get(Integer.toString(rule.getAppId()));
            if(ruleListByAppId == null) {
                ruleListByAppId = new ArrayList<>();
                map.put(Integer.toString(rule.getAppId()), ruleListByAppId);
            }
            ruleListByAppId.add(rule);
        }
        return map;
    }

    private static Map<String,List<User>> loadUserMap() {
        Map<String, List<User>> map = new HashMap<>();
        for(App app : appList) {
            String userIds = app.getUserId();
            List<User> userListInApp = map.get(Integer.toString(app.getId()));
            if(userListInApp == null ){
                userListInApp = new ArrayList<>();
                map.put(Integer.toString(app.getId()), userListInApp);
            }

            String[] userIdArr = userIds.split(",");
            for(String userId : userIdArr) {
                User user = queryUserById(userId);
                if(user != null)
                    userListInApp.add(user);
            }
        }
        return map;
    }

    private static List<User> getUserIdsByAppId(String appId) {
        return userMap.get(appId);
    }

    private static User queryUserById(String userId) {
        for(User user : userList) {
            if(Long.toString(user.getId()).equals(userId ))
                return user;
        }
        return null;
    }

    private static boolean sendEmail(String appId, List<User> users, Message message) {
        System.out.println("send email " + message.getLine());
        return true;
    }

    private static boolean sendSms(String appId, List<User> users, Message message) {
        System.out.println("send sms " + message.getLine());
        return true;
    }

}
