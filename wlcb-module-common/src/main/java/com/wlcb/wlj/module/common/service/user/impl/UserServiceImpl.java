package com.wlcb.wlj.module.common.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wlcb.wlj.module.base.vo.ResponseData;
import com.wlcb.wlj.module.common.service.user.UserService;
import com.wlcb.wlj.module.common.utils.JWTUtils;
import com.wlcb.wlj.module.common.utils.ReturnJsonUtil;
import com.wlcb.wlj.module.dbs.dao.user.UserMapper;
import com.wlcb.wlj.module.dbs.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mr.gmac
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper mapper;

    /**
     * @Author 郭丁志
     * @Description //TODO token过期时间
     * @Date 23:56 2020-03-05
     * @return
     **/
    private Long tokenExpired = 40 * 60 * 1000L;

    /**
     * @Author 郭丁志
     * @Description //TODO 登录验证
     * @Date 23:11 2020-03-05
     * @Param [username, password]
     * @return com.wlcb.wlj.module.base.vo.ResponseData
     **/
    @Override
    public ResponseData login(User user) {
        try {
            List<String> list = null;

            if (user.getRole() == 1) {
                list = mapper.selectAllRole();
            } else {
                list = mapper.selectMenuByRole(user.getRole());
            }

            JSONObject json = JSON.parseObject(JSON.toJSONString(user));
            json.put("menu",list);

            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("userId", user.getId());
            String token = JWTUtils.createJWT(JSON.toJSONString(user),payload,tokenExpired);

            json.put("token",token);

            logger.info("登录成功，用户名={},id={}",user.getUser(),user.getId());

            return ReturnJsonUtil.printJson(200,"登录成功",json,false);
        }catch (Exception e){
            logger.error("登录出错：{}",e.getMessage());
            return ReturnJsonUtil.printJson(500,"登录失败",false);
        }
    }

    @Override
    public User selectByUserName(String username) {
        return mapper.selectByUser(username);
    }
}
