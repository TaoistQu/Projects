package cn.qulei.oa.service;

import cn.qulei.oa.mapper.RoleExample;
import cn.qulei.oa.mapper.RoleMapper;
import cn.qulei.oa.pojo.Role;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务
 *
 * @author QuLei
 */
@Service
public class RoleServiceImpl {
    @Autowired
    RoleMapper roleMapper;

    public PageInfo<Role> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        RoleExample example = new RoleExample();
        List<Role> list = roleMapper.selectByExample(example);
        return new PageInfo(list);
    }

    public Role findById(int id) {
        return roleMapper.findById(id);
    }

    public void addPermission(int id, Integer[] permissions) {
        roleMapper.addPermission(id, permissions);
    }
}
