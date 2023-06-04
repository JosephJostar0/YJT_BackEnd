package edu.web.yjt_backend.controller;

import edu.web.yjt_backend.common.BaseRespone;
import edu.web.yjt_backend.common.ErrorCode;
import edu.web.yjt_backend.common.ResultUtils;
import edu.web.yjt_backend.exception.BusinessException;
import edu.web.yjt_backend.model.domain.Paper;
import edu.web.yjt_backend.model.domain.Problem;
import edu.web.yjt_backend.model.domain.User;
import edu.web.yjt_backend.model.domain.request.AddPaperRequest;
import edu.web.yjt_backend.model.domain.request.DelPaperRequest;
import edu.web.yjt_backend.model.domain.request.PagePaperRequest;
import edu.web.yjt_backend.model.domain.request.PaperProblemRequest;
import edu.web.yjt_backend.model.domain.response.PagePaperResponse;
import edu.web.yjt_backend.service.PaperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static edu.web.yjt_backend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 试卷接口
 */

@RestController
@RequestMapping("/paper")
@CrossOrigin(origins = "*")
public class paperController {
    @Resource
    private PaperService paperService;

    @PostMapping("/getPaper")
    public BaseRespone<List<Paper>> getPaper(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取问题的请求为空");
        }
        List<Paper> paperList = paperService.getPaper();
        return ResultUtils.success(paperList);
    }

    @PostMapping("/getPaperProblem")
    public BaseRespone<List<Problem>> getPaperProblem(@RequestBody PaperProblemRequest paperProblemRequest, HttpServletRequest request) {
        if (paperProblemRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "增加试卷请求为空");
        }
        long id = paperProblemRequest.getPaperId();
        List<Problem> problemList = paperService.paperProblem(id);
        return ResultUtils.success(problemList);
    }

    @PostMapping("/addPaper")
    public BaseRespone<Long> addPaper(@RequestBody AddPaperRequest addPaperRequest, HttpServletRequest request) {
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        long author = currentUser.getId();
        if (addPaperRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "增加试卷请求为空");
        }
        String name = addPaperRequest.getName();
        String des = addPaperRequest.getDescription();
        List<Long> ques = addPaperRequest.getQuestions();
        long author = addPaperRequest.getUserId();
        byte isPub = addPaperRequest.getIsPublic();

        if (ques.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "信息填入不全");
        }
        Long result = paperService.addPaper(name, author, des, isPub, ques);
        return ResultUtils.success(result);
    }

    @PostMapping("/delPaper")
    public BaseRespone<Long> delPaper(@RequestBody DelPaperRequest delPaperRequest, HttpServletRequest request) {
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        long uid = currentUser.getId();
        if (delPaperRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "删除试卷请求为空");
        }
        long paperId = delPaperRequest.getPaperId();
        long uid = delPaperRequest.getUserId();
        if (!paperService.delPaper(paperId, uid)) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(1L);
    }

    @PostMapping("/pagePaper")
    public BaseRespone<PagePaperResponse> pagePaper(@RequestBody PagePaperRequest pagePaperRequest, HttpServletRequest request) {
        if (pagePaperRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "请求为空");
        }
        List<Paper> result = paperService.pagePaper(pagePaperRequest.getCurrent(), pagePaperRequest.getPageSize());
        long size = paperService.count();
        return ResultUtils.success(new PagePaperResponse(result, size));
    }

    @PostMapping("/searchPaper")
    public BaseRespone<List<Paper>> searchPaper(@RequestParam Optional<Integer> id, @RequestParam Optional<String> name, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取问题的请求为空");
        }
        List<Paper> problemList = paperService.searchPaper(id.orElse(null), name.orElse(null));
        return ResultUtils.success(problemList);
    }
}
