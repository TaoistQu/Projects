package cn.qulei.oa.service.impl;

import cn.qulei.oa.mapper.AccountExample;
import cn.qulei.oa.mapper.AccountMapper;
import cn.qulei.oa.pojo.Account;
import cn.qulei.oa.service.IAccountService;
import cn.qulei.oa.util.MD5Util;
import cn.qulei.oa.util.RespState;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现分页查询
 *
 * @author QuLei
 */
@Service
public class AccountServiceImpl implements IAccountService {
    @Autowired
    AccountMapper mapper;

    /**
     * 根据用户名和密码查询用户
     *
     * @param loginName 用户名
     * @param password  密码
     * @return 返回的用户对象
     */
    @Override
    public Account findByLoginNameAndPassword(String loginName, String password) {
        /*AccountExample example = new AccountExample();
        example.createCriteria().andLoginNameEqualTo(loginName)
                .andPasswordEqualTo(password);*/
        return mapper.findByLoginNameAndPassword(loginName, password);
    }

    /**
     * 实现分页查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        AccountExample example = new AccountExample();
        List<Account> list = mapper.selectByExample(example);
        List<Account> accountList = mapper.selectByPer();
        return new PageInfo<>(list);
    }

    @Override
    public RespState deleteById(int id) {
        int row = mapper.deleteByPrimaryKey(id);
        if (row == 1) {
            return RespState.build(200);
        }

        return RespState.build(500, "删除失败！！！");
    }

    @Override
    public RespState register(Account account) {
        account.setPassword(MD5Util.md5(account.getPassword()));
        account.setRole("user");
        try {
            int index = mapper.insert(account);
            if (index == 1) {
                return RespState.build(200);
            }
            return RespState.build(500, "注册失败！！！");
        } catch (Exception e) {
            return RespState.build(500, "注册失败！！！");
        }


    }

    @Override
    public RespState modify(Account account) {
        try {
            int index = mapper.updateByPrimaryKeySelective(account);
            if (index == 1) {
                return RespState.build(200);
            }
            return RespState.build(500, "修改失败！！！");
        } catch (Exception e) {
            return RespState.build(500, "修改失败！！！");
        }
    }
}
