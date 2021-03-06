package com.zwo.pls.modules.system.web;

import com.zwo.pls.core.service.IBaseService;
import com.zwo.pls.core.web.BaseController;
import com.zwo.pls.modules.system.domain.User;
import com.zwo.pls.modules.system.domain.UserCriteria;
import com.zwo.pls.modules.system.service.IUserService;
import com.zwo.pls.modules.system.vo.UserVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 一句话描述该类功能：
 * Created by Tony(黄记新) in 2019/4/30
 */
@Api(value = "user", tags="用户模块增删改查")
@RestController
@RequestMapping("user")
public class UserController extends BaseController<User> {

    // 用户命名空间
    private  static  final  String USER_CACHE_NAMESPACE = "system-manager:system:user";

    @Autowired
    @Lazy(value=true)
    CompositeCacheManager cacheManger;

//    @Resource(name="ehcacheCacheManager")
//    @Lazy(value=true)
//    CacheManager cacheManger1;

    @Autowired
    private IUserService userService;

    @Override
    protected IBaseService getBaseService() {
        return userService;
    }

    @GetMapping("test")
    public User test(){
        User user = null;
        Cache cache = null;

        if (cacheManger != null) {
            cache = cacheManger.getCache("USER_CACHE");
            if (cache != null) {
                Cache.ValueWrapper wrapper = cache.get("1");
                if (wrapper != null) {
                    user = (User) wrapper.get();
                }

                if(user  == null){
                    user = userService.selectByPrimaryKey("1");
                    cache.put("1",user);
                }
            }
        }else{
            user = userService.selectByPrimaryKey("1");
        }

        return  user;
    }

    @PreAuthorize("hasAnyAuthority('*', 'pls:system:user:test')")
    @GetMapping("testoauth")
    public String testoauth(){
        User user = userService.selectByPrimaryKey("1");
        return  "你已经成功进入受保护的方法，得到user："+user.toString();
    }

    @PreAuthorize("hasAnyAuthority('*', 'pls:system:user:getLoginName')")
    @GetMapping("login-name")
    public String getLoginName(){
       String loginName = super.getLoginUser();
        return  loginName;
    }

    @PreAuthorize("hasAnyAuthority('*', 'pls:system:user:getAuthorities')")
    @GetMapping("authorities")
    public Collection<SimpleGrantedAuthority> getAuthorities(){
        Collection<SimpleGrantedAuthority> authorities = super.getAuthorities();
        return  authorities;
    }

    @GetMapping("claims")
    public String getClaims(@RequestParam String token){
        Jwt jwt = JwtHelper.decode(token);
        String claims = jwt.getClaims();
        return  claims;
    }

    @GetMapping("username")
    public UserVo selectByUserName(@RequestParam String loginName) {
        UserVo userVo = null;

        // 使用缓存
        if(cacheManger != null){
            Cache cache = null;
            try {
                cache = cacheManger.getCache("UserVo");
            } catch (Exception e) {

            }
            if(cache != null){
                Cache.ValueWrapper wrapper =  cache.get(loginName);
                if(wrapper!=null){
                    userVo = (UserVo) wrapper.get();
                }

                UserVo uVo = cache.get(loginName,UserVo.class);
                System.out.println(uVo == null);
            }

        }

        if (userVo == null) {
            userVo = this.userService.selectByUserName(loginName);
            // 使用缓存
            if(cacheManger != null && userVo != null){
                Cache cache = null;
                try {
                    cache = cacheManger.getCache("UserVo");
                } catch (Exception e) {

                }
                if(cache != null)
                    cache.put(loginName,userVo);
            }
        }
        return userVo;
    }

    @GetMapping("insert-batch")
    public int insertBatch(){
        int result = -1;
        List<User> list = new ArrayList<User>();
        User user = null;
        for (int i=0;i<10000;i++){
            user = new User();
            user.setId(UUID.randomUUID().toString().replaceAll("-",""));
            user.setLoginName(UUID.randomUUID().toString().replaceAll("-",""));
            user.setPassword("$10$jre6mb1sNNUqUGbZTiUMUe9aBz774m777nWcABBEj0feARudyIUuu");
        }
        result = this.userService.insertBatch(list);
        return  result;
    }

    @GetMapping("example-page")
    List<UserVo> selectByExamplePage(HttpServletRequest request, @RequestParam(required = false,defaultValue = "0") Integer start, @RequestParam(required = false,defaultValue = "10")Integer size){
        UserCriteria example = this.getCriteria(request);
        List<UserVo> list = this.userService.selectByExamplePage(example,start,size);
        return list;
    }

    private UserCriteria getCriteria(HttpServletRequest request) {
        UserCriteria example = null;
        if (request.getParameterNames().hasMoreElements()) {
            example = new UserCriteria();
            UserCriteria.Criteria criteria = example.createCriteria();
            String loginName = request.getParameter("loginName");
            if (!StringUtils.isEmpty(loginName)) {
                criteria.andLoginNameLike("%"+loginName+"%");
            }
//            Date createTime = request.getParameter("createTime");
//            if (StringUtils.isNotEmpty(loginName)) {
//                criteria.andLoginNameLike("%"+loginName+"%");
//            }
        }
        return example;
    }

    @GetMapping("testUser")
    public UserVo testUser(){
        UserVo userVo = new UserVo();
        userVo.setCreateTime(new Date());
        userVo.setUpdateTime(new Date());
        return  userVo;
    }

    @GetMapping("test-change")
    public String testChange(){
        return  "A";
    }

    /**
     * 刷新密钥
     *
     * @param authorization 原密钥
     * @return 新密钥
     * @throws AuthenticationException 错误信息
     */
//    @GetMapping(value = "/refreshToken")
//    public String refreshToken(@RequestHeader String authorization) throws AuthenticationException {
//        return userService.refreshToken(authorization);
//    }
}
