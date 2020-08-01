package cn.qulei.oa.controller;

import cn.qulei.oa.pojo.Account;
import cn.qulei.oa.pojo.Permission;
import cn.qulei.oa.pojo.Role;
import cn.qulei.oa.service.AccountService;
import cn.qulei.oa.service.PermissionService;
import cn.qulei.oa.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("accountList")
    public String accountList(@RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "5")int pageSize, Model model){
        PageInfo<Account> pageInfo = accountService.findByPage(pageNum, pageSize);
        model.addAttribute("pageInfo",pageInfo);
        return "/manager/accountList";
    }

    @RequestMapping("roleList")
    public String roleList(@RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "5")int pageSize, Model model){
        PageInfo<Role> pageInfo = roleService.findByPage(pageNum, pageSize);
        model.addAttribute("pageInfo",pageInfo);
        return "/manager/roleList";
    }

    @RequestMapping("permissionList")
    public String permissionList(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "5")int pageSize, Model model){
        PageInfo<Permission> pageInfo = permissionService.findByPage(pageNum, pageSize);
        model.addAttribute("pageInfo",pageInfo);
        return "/manager/permissionList";
    }

    /**
     * 修改权限
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/permissionModif")
    public String permissionModif( int id, Model model){
        Permission permission = permissionService.findById(id);
        model.addAttribute("per",permission);
        return "/manager/permissionModif";
    }

    /**
     * 返回添加权限的页面
     * @return
     */
    @GetMapping("/permissionAdd")
    public String permissionAdd(){
        return "/manager/permissionModif";
    }

    /**
     * 角色授权
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("rolePermission/{id}")
    public String rolePermission(@PathVariable int id,Model model){
        Role role = roleService.findById(id);
        List<Permission> perList = permissionService.findAll();
        model.addAttribute("perList",perList);
        model.addAttribute("role",role);
        return "manager/rolePermission";
    }




}
