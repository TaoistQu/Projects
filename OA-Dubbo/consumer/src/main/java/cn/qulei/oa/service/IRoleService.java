package cn.qulei.oa.service;

import cn.qulei.oa.pojo.Role;
import com.github.pagehelper.PageInfo;

/**
 * 角色服务接口
 *
 * @author QuLei
 */
public interface IRoleService {
    /**
     * 分页查询
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return 分页查询的结果
     */
    PageInfo<Role> findByPage(int pageNum, int pageSize);

    /**
     * 通过Id查询
     *
     * @param id 编号Id
     * @return 返回查询的角色对象
     */
    Role findById(int id);

    /**
     * 给角色授权
     *
     * @param id          对应的角色Id
     * @param permissions 权限Id数组
     */
    void addPermission(int id, Integer[] permissions);
}
