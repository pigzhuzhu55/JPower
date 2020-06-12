package com.wlcb.jpower.core.user.controller.function;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.wlcb.jpower.module.base.vo.ResponseData;
import com.wlcb.jpower.module.common.controller.BaseController;
import com.wlcb.jpower.module.common.service.core.user.CoreFunctionService;
import com.wlcb.jpower.module.common.utils.BeanUtil;
import com.wlcb.jpower.module.common.utils.ReturnJsonUtil;
import com.wlcb.jpower.module.common.utils.constants.ConstantsReturn;
import com.wlcb.jpower.module.dbs.entity.core.function.TbCoreFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName RoleController
 * @Description TODO 菜单相关
 * @Author 郭丁志
 * @Date 2020-02-13 14:10
 * @Version 1.0
 *
 */
@RestController
@RequestMapping("/core/function")
public class FunctionController extends BaseController {

    @Resource
    private CoreFunctionService coreFunctionService;

    /**
     * @Author 郭丁志
     * @Description //TODO 根据父节点查询子节点菜单
     * @Date 15:13 2020-05-20
     * @Param [coreFunction]
     * @return com.wlcb.jpower.module.base.vo.ResponseData
     **/
    @RequestMapping(value = "/listByParent",method = {RequestMethod.GET,RequestMethod.POST},produces="application/json")
    public ResponseData list(TbCoreFunction coreFunction){
        List<TbCoreFunction> list = coreFunctionService.listByParent(coreFunction);
        return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_SUCCESS,"获取成功", JSON.toJSON(new PageInfo<>(list)),true);
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 新增菜单
     * @Date 15:31 2020-05-20
     * @Param [coreFunction]
     * @return com.wlcb.jpower.module.base.vo.ResponseData
     **/
    @RequestMapping(value = "/add",method = {RequestMethod.POST},produces="application/json")
    public ResponseData add(TbCoreFunction coreFunction){

        ResponseData responseData = BeanUtil.allFieldIsNULL(coreFunction,
                "createUser","functionName","code", "url", "isMenu");

        if (responseData.getCode() == ConstantsReturn.RECODE_NULL){
            return responseData;
        }

        TbCoreFunction function = coreFunctionService.selectFunctionByCode(coreFunction.getCode());
        if (function != null){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"该菜单已存在", false);
        }

        function = coreFunctionService.selectFunctionByUrl(coreFunction.getUrl());
        if (function != null){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"该资源URL已存在", false);
        }

        Integer count = coreFunctionService.add(coreFunction);

        if (count > 0){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_SUCCESS,"新增成功", true);
        }else {
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_FAIL,"新增失败", false);
        }
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 删除菜单
     * @Date 15:37 2020-05-20
     * @Param [ids]
     * @return com.wlcb.jpower.module.base.vo.ResponseData
     **/
    @RequestMapping(value = "/delete",method = {RequestMethod.DELETE},produces="application/json")
    public ResponseData delete(String ids){

        if (StringUtils.isBlank(ids)){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"ID不可为空", false);
        }

        Integer c = coreFunctionService.listByPids(ids);
        if (c > 0){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"该菜单存在下级菜单，请先删除下级菜单", false);
        }

        Integer count = coreFunctionService.delete(ids);

        if (count > 0){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_SUCCESS,"删除成功", true);
        }else {
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_FAIL,"删除失败", false);
        }
    }

    /**
     * @Author 郭丁志
     * @Description //TODO 更新菜单
     * @Date 15:43 2020-05-20
     * @Param [coreRole]
     * @return com.wlcb.jpower.module.base.vo.ResponseData
     **/
    @RequestMapping(value = "/update",method = {RequestMethod.PUT},produces="application/json")
    public ResponseData update(TbCoreFunction coreFunction){

        ResponseData responseData = BeanUtil.allFieldIsNULL(coreFunction,
                "updateUser","id");

        if (responseData.getCode() == ConstantsReturn.RECODE_NULL){
            return responseData;
        }

        if (StringUtils.isNotBlank(coreFunction.getCode())){
            TbCoreFunction function = coreFunctionService.selectFunctionByCode(coreFunction.getCode());
            if (function != null && !StringUtils.equals(function.getId(),function.getId())){
                return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"该菜单已存在", false);
            }
        }

        if (StringUtils.isNotBlank(coreFunction.getUrl())){
            TbCoreFunction function = coreFunctionService.selectFunctionByUrl(coreFunction.getUrl());
            if (function != null && !StringUtils.equals(function.getId(),function.getId())){
                return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_BUSINESS,"该URL已存在", false);
            }
        }

        Integer count = coreFunctionService.update(coreFunction);

        if (count > 0){
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_SUCCESS,"修改成功", true);
        }else {
            return ReturnJsonUtil.printJson(ConstantsReturn.RECODE_FAIL,"修改失败", false);
        }
    }
}