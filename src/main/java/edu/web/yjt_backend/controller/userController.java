package edu.web.yjt_backend.controller;
/**
 * 用户接口
 */

import edu.web.yjt_backend.common.BaseRespone;
import edu.web.yjt_backend.common.ErrorCode;
import edu.web.yjt_backend.common.ResultUtils;
import edu.web.yjt_backend.exception.BusinessException;
import edu.web.yjt_backend.model.domain.User;
import edu.web.yjt_backend.model.domain.request.DelProblemRequest;
import edu.web.yjt_backend.model.domain.request.DelUserRequest;
import edu.web.yjt_backend.model.domain.request.UserLoginRequest;
import edu.web.yjt_backend.model.domain.request.UserRegisterRequest;
import edu.web.yjt_backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static edu.web.yjt_backend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
//下面这个允许跨域的前端ip需要具体改
@CrossOrigin(origins = "*")
public class userController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseRespone<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "注册请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String inviteCode = userRegisterRequest.getInviteCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, inviteCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "信息填入不全");
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword, inviteCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseRespone<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "登录请求为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户或密码为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseRespone<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "注销请求为空");
        }
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseRespone<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        long userId = currentUser.getId();
        //校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/search")
    public BaseRespone<List<User>> userSearch(String username, HttpServletRequest request) {
        List<User> userList = userService.searchUser(username, request);
        return ResultUtils.success(userList);
    }


    @PostMapping("/delete")
    public BaseRespone<Boolean> userDelete(@RequestBody DelUserRequest delProblemRequest, HttpServletRequest request) {
        long id = delProblemRequest.getId();
        Boolean result = userService.deleteUser(id, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/getAllUser")
    public BaseRespone<List<User>> getAllUser(HttpServletRequest request) {
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
//        }
//        if (!userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员用户无权查看其他用户的信息");
//        }
        return ResultUtils.success(userService.getAllUser());
    }
}