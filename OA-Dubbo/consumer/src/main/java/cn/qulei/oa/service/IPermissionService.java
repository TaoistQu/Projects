package cn.qulei.oa.service;

import cn.qulei.oa.pojo.Permission;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 权限管理服务接口
 *
 * @author QuLei
 */
public interface IPermissionService {
    PageInfo<Permission> findByPage(int pageNum, int pageSize);

    Permission findById(int id);

    void update(Permission p);

    void add(Permission p);

    List<Permission> findAll();
}
