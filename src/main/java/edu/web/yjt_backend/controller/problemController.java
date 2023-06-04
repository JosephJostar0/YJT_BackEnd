package edu.web.yjt_backend.controller;

import edu.web.yjt_backend.common.BaseRespone;
import edu.web.yjt_backend.common.ErrorCode;
import edu.web.yjt_backend.common.ResultUtils;
import edu.web.yjt_backend.exception.BusinessException;
import edu.web.yjt_backend.model.domain.Problem;
import edu.web.yjt_backend.model.domain.request.AddProblemRequest;
import edu.web.yjt_backend.model.domain.request.DelProblemRequest;
import edu.web.yjt_backend.model.domain.request.GetProblemRequest;
import edu.web.yjt_backend.model.domain.request.PageProblemRequest;
import edu.web.yjt_backend.model.domain.response.PagePaperResponse;
import edu.web.yjt_backend.model.domain.response.PageProblemResponse;
import edu.web.yjt_backend.service.ProblemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/problem")
//下面这个允许跨域的前端ip需要具体改
@CrossOrigin(origins = "*")
public class problemController {
    @Resource
    private ProblemService problemService;

    @GetMapping("/getProblem")
    public BaseRespone<List<Problem>> getProblem(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取问题的请求为空");
        }
        List<Problem> problemList = problemService.getProblem();
        return ResultUtils.success(problemList);
    }

    @PostMapping("/searchProblem")
    public BaseRespone<List<Problem>> searchProblem(@RequestParam Optional<Integer> id, @RequestParam Optional<String> title, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取问题的请求为空");
        }
        List<Problem> problemList;
        if (id.isPresent() && title.isPresent()) {
            problemList = problemService.searchProblem(id.get(), title.get());
        } else if (id.isPresent()) {
            problemList = problemService.searchProblem(id.get(), null);
        } else if (title.isPresent()) {
            problemList = problemService.searchProblem(null, title.get());
        } else {
            problemList = problemService.searchProblem(null, null);
        }
        return ResultUtils.success(problemList);
    }

    @PostMapping("/pageProblem")
    public BaseRespone<PageProblemResponse> pageProblem(@RequestBody PageProblemRequest pageProblemRequest, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取问题的请求为空");
        }
        List<Problem> problemList = problemService.pageProblem(pageProblemRequest.getCurrent(), pageProblemRequest.getPageSize());
        long total = problemService.count();
        return ResultUtils.success(new PageProblemResponse(problemList, total));
    }

    @PostMapping("/addProblem")
    public BaseRespone<Long> addProblem(@RequestBody AddProblemRequest addProblemRequest) {
        if (addProblemRequest == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR, "增加试卷请求为空");
        }
        String title = addProblemRequest.getTitle();
        String author = addProblemRequest.getAuthor();
        String content = addProblemRequest.getContent();
        String answer = addProblemRequest.getAnswer();
        int label = addProblemRequest.getLabel();
        if (StringUtils.isAnyBlank(author, title, content, answer)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "信息填入不全");
        }
        Long result = problemService.addProblem(title, author, content, answer, label);
        return ResultUtils.success(result);
    }


    @PostMapping("/deleteProblem")
    public BaseRespone<Boolean> deleteProblem(@RequestBody DelProblemRequest delProblemRequest, HttpServletRequest request) {
        Boolean result = problemService.deleteProblem(delProblemRequest.getProblemId(), request);
        return ResultUtils.success(result);
    }
}
