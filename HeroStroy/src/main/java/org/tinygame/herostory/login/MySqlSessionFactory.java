package org.tinygame.herostory.login;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * MySql会话工厂
 *
 * @author QuLei
 */
public final class MySqlSessionFactory {
    /**
     * MyBatis Sql 会话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化构造器
     */
    private MySqlSessionFactory() {
    }

    public static void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MyBatisConfig.xml")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory尚未初始化");
        }
        return _sqlSessionFactory.openSession(true);
    }
}
