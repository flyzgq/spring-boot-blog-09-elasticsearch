package com.fly.springboot.controller;

import com.fly.springboot.entity.EsBlog;
import com.fly.springboot.entity.User;
import com.fly.springboot.model.TagVo;
import com.fly.springboot.service.interfaces.EsBlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author fly
 * @date 2018/5/13 16:22
 * @description Blog控制器
 **/
@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private EsBlogService esBlogService;

    public static final String HOT = String.valueOf("hot");

    public static final String NEW = String.valueOf("new");
    /**
     * 日志记录
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(BlogController.class);
    /**
     * 获取博客列表
     * @param order     排序规则
     * @param keyword   关键字
     * @param async     同步
     * @param pageIndex 开始页码
     * @param pageSize  每页的条数
     * @param model     返给前端的模型数据
     * @return
     */
    @GetMapping
    public String lisEstBlogs(@RequestParam(value = "order", required = false, defaultValue = "new") String order,
                              @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                              @RequestParam(value = "async", required = false) boolean async,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              Model model){
        Page<EsBlog> page = null;
        List<EsBlog> list = null;
        boolean isEmpty = true;

        try {
            if(HOT.equals(order)){
                Sort sort = new Sort(Sort.Direction.DESC, "readSize", "commentSize", "voteSize", "createTime");
                Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
                page = esBlogService.listHotestEsBlogs(keyword, pageable);
            }else if(NEW.equals(order)){
                Sort sort = new Sort(Sort.Direction.DESC, "createTime");
                Pageable pageable= PageRequest.of(pageIndex, pageSize, sort);
                page = esBlogService.listNewestEsBlogs(keyword, pageable);
            }
            isEmpty = false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = esBlogService.listEsBlogs(pageable);
        }

        assert page != null;
        list = page.getContent();

        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);

        //首次访问页面才加载
        if(!async && !isEmpty){
            List<EsBlog> newestEsBlogs = esBlogService.listTop5NewestEsBlogs();
            model.addAttribute("newest", newestEsBlogs);

            List<EsBlog> hotestEsBlogs = esBlogService.listTop5HotestEsBlogs();
            model.addAttribute("hotest", hotestEsBlogs);

            List<TagVo> tags = esBlogService.listTop30Tags();
            model.addAttribute("tags", tags);

            List<User> users = esBlogService.listTop12Users();
            model.addAttribute("users", users);
        }

        return (async == true? "/index :: #mainContainerRepleace" : "/index");
    }

}
