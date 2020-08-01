package cn.qulei.oa.mapper;

import cn.qulei.oa.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * RoleMapper继承基类
 */
@Mapper
public interface RoleMapper extends MyBatisBaseDao<Role, Integer, RoleExample> {
    void addPermission(Integer id, Integer[] permissions);

    Role findById(int id);
}