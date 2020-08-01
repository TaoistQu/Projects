package cn.qulei.oa.controller;

import cn.qulei.oa.pojo.Account;
import cn.qulei.oa.pojo.Config;
import cn.qulei.oa.service.AccountService;
import cn.qulei.oa.util.MD5Util;
import cn.qulei.oa.util.RespState;
import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 用户账号相关的模块
 * * @Author:      QuLei
 * @CreateDate: 2019-07-13 00:34
 * @Version: 1.0
 */
@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    Config config;
    @Autowired
    FastFileStorageClient fc;


    @GetMapping("login")
    public String login(Model model) {
        String systemName = config.getSystemName();
        model.addAttribute("config", systemName);
        return "account/login";
    }

    @RequestMapping("validataAccount")
    @ResponseBody
    public String validataAccount(String loginName, String password, HttpSession session) {
        //加密密码
        password = MD5Util.md5(password);
        //返回查询回来的对象
        Account account = accountService.findByLoginNameAndPassword(loginName, password);
        System.out.println(account);
        if (account == null) {
            return "false";
        } else {
            session.setAttribute("account", account);
            return "success";
        }
    }

    /**
     * @return
     * @throws
     * @Description: 返回注册页面
     * @author QuLei
     * @date 2019-07-14 20:22
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register() {
        return "/account/register";
    }

    /**
     * 账户注册
     *
     * @param filename
     * @param account
     * @param request
     * @param model
     * @return
     */
    @PostMapping("register")
    public String register(MultipartFile filename, @ModelAttribute Account account,
                           HttpServletRequest request, Model model) {
        //将头像上传，并将头像的名称属性设定
        Account account1 = upload(filename, request, account);
        if (account1.getFileName().isEmpty()) {
            account1.setFileName("default.jpg");
        }
        // 存入数据库中
        RespState state = accountService.register(account1);
        if (state.getCode() == 200) {
            return "/account/login";
        }
        model.addAttribute("msg", state.getMsg());
        return "/account/register";
    }

    /**
     * 退出登陆
     *
     * @param session
     * @return
     */
    @RequestMapping("logOut")
    public String logOut(HttpSession session) {
        session.removeAttribute("account");
        return "index";
    }


    @RequestMapping("deleteById")
    @ResponseBody
    public RespState deleteById(int id, HttpSession session) {
        //标记一下是否删除成功
        Account account = (Account) session.getAttribute("account");
        String role = account.getRole();
        RespState state = null;
        if ("admin".equals(role)) {
            state = accountService.deleteById(id);
            return state;
        } else {
            state = RespState.build(500, "无权限！！！");
            return state;
        }

    }

    /**
     * 查询账户
     *
     * @param pageNum
     * @param pageSize
     * @param model
     * @return
     */
    @RequestMapping("list")
    public String list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "10") int pageSize, Model model) {
        PageInfo<Account> pageInfo = accountService.findByPage(pageNum, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        return "account/list";
    }

    /**
     * 返回修改账户信息页面，并携带账户信息给前端
     *
     * @param model
     * @param session
     * @return
     */
    @GetMapping("modify")
    public String modify(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("account");
        model.addAttribute("account", account);
        return "account/modify";
    }

    @PostMapping("modify")
    public String modify(MultipartFile filename, @ModelAttribute Account account,
                         HttpServletRequest request, Model model) {
        //将头像上传，并将头像的名称属性设定
        Account account1 = upload(filename, request, account);
        //拿出session中的
        Account oldAcc = (Account) request.getSession().getAttribute("account");
        if (account.getFileName().isEmpty()) {
            account.setFileName(oldAcc.getFileName());
        }
        // 存入数据库中
        RespState state = accountService.modify(account1);
        if (state.getCode() == 200) {
            request.getSession().setAttribute("account", account1);
            return "redirect:/account/list";
        }
        model.addAttribute("msg", state.getMsg());
        return "account/modify";
    }

    /**
     * 处理文件上传逻辑，并将头像名称存数据库
     *
     * @param filename
     * @param request
     * @param account
     */
    private Account upload(MultipartFile filename, HttpServletRequest request, Account account) {
        String name = "";
        if (filename != null) {
            name = filename.getOriginalFilename();
            if (!name.isEmpty()) {
                File path = null;
                try {
                    path = new File(ResourceUtils.getURL("classpath:").getPath());
                    File upload = new File(path.getAbsolutePath(), "static/uploads");
                    System.out.println(upload.getPath());
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    //文件转存
                    // filename.transferTo(new File(upload+"/"+name));

                    Set<MetaData> metaDataSet = new HashSet<>();
                    metaDataSet.add(new MetaData("Author", "qulei"));
                    metaDataSet.add(new MetaData("CreateDate", "2019-10-25"));


                    try {
                        StorePath uploadFile = null;
                        uploadFile = fc.uploadFile(filename.getInputStream(), filename.getSize(), FilenameUtils.getExtension(filename.getOriginalFilename()), metaDataSet);

                        account.setLocation(uploadFile.getPath());
                        System.out.println(uploadFile.getPath());
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //此处的name可能为""
        account.setFileName(name);
        return account;
    }

}
