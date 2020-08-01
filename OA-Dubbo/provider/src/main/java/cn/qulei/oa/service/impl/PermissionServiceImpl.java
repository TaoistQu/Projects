package cn.qulei.oa.service;

import cn.qulei.oa.mapper.PermissionExample;
import cn.qulei.oa.mapper.PermissionMapper;
import cn.qulei.oa.pojo.Permission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务
 *
 * @author QuLei
 */
@Service
public class PermissionServiceImpl {
    @Autowired
    PermissionMapper mapper;

    public PageInfo<Permission> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PermissionExample example = new PermissionExample();
        List<Permission> list = mapper.selectByExample(example);
        return new PageInfo(list);
    }

    public Permission findById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    public void update(Permission p) {
        //PermissionExample example = new PermissionExample();
        mapper.updateByPrimaryKeySelective(p);
    }

    public void add(Permission p) {
        //PermissionExample example = new PermissionExample();
        mapper.insert(p);
    }

    public List<Permission> findAll() {
        PermissionExample example = new PermissionExample();
        return mapper.selectByExample(example);
    }
}
