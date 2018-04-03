package com.test.org.Controller;

import com.test.org.Dao.UserMapper;
import com.test.org.Service.UserService;
import com.test.org.model.User;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private UserMapper userMapper;

    @ResponseBody
    @RequestMapping(value = "/add", produces = {"application/json;charset=UTF-8"})
    public int addUser(User user) {
        return userService.addUser(user);
    }

    @ResponseBody
    @RequestMapping(value = "/all/{pageNum}/{pageSize}", produces = {"application/json;charset=UTF-8"})
    public Object findAllUser(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {

        return userService.findAllUser(pageNum, pageSize);
    }

    @RequestMapping(value = "/forInsertUser")
    public String forInsertUser() {
        logger.debug("==================开始循环插入user");
        Long startTimeMills = System.currentTimeMillis();
        User user;
        for (int i = 1; i < 100; i++) {
            user = new User();
            user.setUserName(i + "");
            user.setPassword("123456");
            user.setPhone("13118181818");
            userService.addUser(user);
        }

        Long endTimeMills = System.currentTimeMillis();
        logger.debug("==================循环插入user结束");
        logger.debug("==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒");
        return "==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒";
    }


    @RequestMapping(value = "/batchInsertUser")
    public String batchInsertUser() {
        logger.debug("==================开始循环插入user");
        Long startTimeMills = System.currentTimeMillis();
//        User user;
//        List<User> userList = new ArrayList<>();
//        for(int i = 0;i<100;i++){
//            user = new User();
//            user.setUserName(i + "b");
//            user.setPassword("123456");
//            user.setPhone("13218181818");
//            userList.add(user);
//        }
        int result = 1;
        SqlSession batchSqlSession = null;
        batchSqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);// 获取批量方式的sqlsession
//            int batchCount = 1000;// 每批commit的个数
//            int batchLastIndex = batchCount;// 每批最后一个的下标
//            for (int index = 0; index < userList.size();) {
//                if (batchLastIndex >= userList.size()) {
//                    batchLastIndex = userList.size();
//                    result = result * batchSqlSession.insert("UserMapper.insert",userList.subList(index, batchLastIndex));
//                    batchSqlSession.commit();
//                    System.out.println("index:" + index+ " batchLastIndex:" + batchLastIndex);
//                    break;// 数据插入完毕，退出循环
//                } else {
//                    result = result * batchSqlSession.insert("UserMapper.insert",userList.subList(index, batchLastIndex));
//                    batchSqlSession.commit();
//                    System.out.println("index:" + index+ " batchLastIndex:" + batchLastIndex);
//                    index = batchLastIndex;// 设置下一批下标
//                    batchLastIndex = index + (batchCount - 1);
//                }
//            }
//            batchSqlSession.commit();
//        }
//        finally {
//            batchSqlSession.close();
//        }


//通过新的session获取mapper
        UserMapper userMapper = batchSqlSession.getMapper(UserMapper.class);
        int size = 100;
        try {
            for (int i = 0; i < size; i++) {
                User user = new User();
                user.setUserName(i + "b");
                user.setPassword("123456");
                user.setPhone("13218181818");
                userMapper.insert(user);
                if (i % 1000 == 0 || i == size - 1) {
//手动每1000个一提交，提交后无法回滚
                    batchSqlSession.commit();
//清理缓存，防止溢出
                    batchSqlSession.clearCache();
                }
            }
        } catch (Exception e) {
//没有提交的数据可以回滚
            batchSqlSession.rollback();
        } finally {
            batchSqlSession.close();
        }
        Long endTimeMills = System.currentTimeMillis();
        logger.debug("==================循环插入user结束");
        logger.debug("==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒");
        return "==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒";
    }

    @RequestMapping(value = "/insertListUser")
    public String insertListUser() {
        logger.debug("==================开始循环插入user");
        Long startTimeMills = System.currentTimeMillis();
        User user;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            user = new User();
            user.setUserName(i + "b");
            user.setPassword("123456");
            user.setPhone("13218181818");
            userList.add(user);
        }
//        userMapper.insertListUser(userList);
        userService.insertListUser(userList);
        Long endTimeMills = System.currentTimeMillis();
        logger.debug("==================循环插入user结束");
        logger.debug("==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒");
        return "==================耗时:     " + (endTimeMills - startTimeMills) + "毫秒";
    }
}