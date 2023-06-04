package edu.web.yjt_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.web.yjt_backend.common.ErrorCode;
import edu.web.yjt_backend.exception.BusinessException;
import edu.web.yjt_backend.mapper.ProblemMapper;
import edu.web.yjt_backend.model.domain.Paper;
import edu.web.yjt_backend.model.domain.Problem;
import edu.web.yjt_backend.model.domain.User;
import edu.web.yjt_backend.service.PaperService;
import edu.web.yjt_backend.service.ProblemService;
import edu.web.yjt_backend.mapper.PaperMapper;
import edu.web.yjt_backend.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SydZh
 * @description 针对表【paper(试卷)】的数据库操作Service实现
 * @createDate 2023-05-30 20:37:33
 */
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper>
        implements PaperService {
    @Resource
    private PaperMapper paperMapper;
    @Resource
    private ProblemService problemService;
    @Resource
    private UserService userService;

    @Override
    public List<Paper> getPaper() {
        return this.list();
    }

    @Override
    public List<Problem> paperProblem(long id) {
        Paper paper = this.getById(id);
        String que = paper.getQuestions();
        String[] ques = que.split(",");
        List<Long> queIds = new ArrayList<>();
        for (String s : ques) {
            queIds.add(Long.parseLong(s));
        }
        List<Problem> questions = new ArrayList<>();
        for (long qid : queIds) {
            questions.add(problemService.getById(qid));
        }
        return questions;
    }

    @Override
    public Long addPaper(String name, long author, String des, byte isPub, List<Long> ques) {
        if (name.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称长度大于100");
        }

        String queStr = ques.stream().map(Object::toString).collect(Collectors.joining(","));

        Paper paper = new Paper();
        paper.setName(name);
        paper.setAuthor(author);
        paper.setDescription(des);
        paper.setIsPublic(Integer.reverseBytes(isPub));
        paper.setQuestions(queStr);
        if (!this.save(paper)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据插入失败");
        }
        return 1L;
    }

    @Override
    public boolean delPaper(long pid, long uid) {
        Paper paper = this.getById(pid);
        User user = userService.getById(uid);
        if (!user.getUserRole().equals(1) && !user.getId().equals(paper.getAuthor())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "您无权删除该试卷，仅管理员和试卷所有者可删除");
        }
        return this.removeById(pid);
    }

    @Override
    public List<Paper> pagePaper(int current, int size) {
        Page<Paper> page = new Page<>(current, size);
        Page<Paper> result = this.page(page);
        return result.getRecords();
    }

    @Override
    public List<Paper> searchPaper(Integer id, String name) {
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (!name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        return this.list(queryWrapper);
    }
}




