package com.god.dragon.utils.builder;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.utils.Builder
 * @date 2021/11/5 9:49
 */
public class BuilderTest {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        userDao.setUsername("HXD");
        userDao.setAge("16");
        userDao.setSalary("10000");
        UserDao2 userDao2 = Builder.of(UserDao2::new)
                .with(UserDao2::setUsername, userDao.getUsername())
                .with(UserDao2::setAge, userDao.getAge() )
                .with(UserDao2::setSalary, userDao.getSalary()).build();
        System.out.println(userDao2);
    }
}
