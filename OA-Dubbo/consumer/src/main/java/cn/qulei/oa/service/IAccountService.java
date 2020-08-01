package cn.qulei.oa.service;

import cn.qulei.oa.pojo.Account;
import cn.qulei.oa.util.RespState;
import com.github.pagehelper.PageInfo;

/**
 * 用户服务接口
 *
 * @author QuLei
 */
public interface IAccountService {
    /**
     * 通过用户名和密码查询用户
     *
     * @param loginName
     * @param password
     * @return
     */
    Account findByLoginNameAndPassword(String loginName, String password);

    PageInfo<Account> findByPage(int pageNum, int pageSize);

    RespState deleteById(int id);

    RespState register(Account account);

    RespState modify(Account account);
}
