package cn.qulei.oa.mapper;

import cn.qulei.oa.pojo.Menu;
import org.springframework.stereotype.Repository;

/**
 * MenuMapper继承基类
 */

@Repository
public interface MenuMapper extends MyBatisBaseDao<Menu, Integer, MenuExample> {
}