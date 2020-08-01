package cn.qulei.oa.mapper;

import cn.qulei.oa.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * PermissionMapper继承基类
 */
@Mapper
public interface PermissionMapper extends MyBatisBaseDao<Permission, Integer, PermissionExample> {
}