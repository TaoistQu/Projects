package cn.qulei.oa.controller.rest;

import cn.qulei.oa.service.RoleService;
import cn.qulei.oa.util.RespState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/role")
public class RoleManagerRestController {
    @Autowired
    RoleService roleService;

    /**
     * 角色授权
     * @param permissions
     * @param id
     * @return
     */
    @PostMapping("permission/add")
    @ResponseBody
    public RespState permissionadd(@RequestParam Integer permissions [],
                                   @RequestParam int id){
        roleService.addPermission(id,permissions);
        return RespState.build(200);
    }

}
