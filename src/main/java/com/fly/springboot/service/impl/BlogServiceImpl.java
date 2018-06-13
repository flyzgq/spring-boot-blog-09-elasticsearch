package com.fly.springboot.service.impl;

import com.fly.springboot.entity.*;
import com.fly.springboot.repository.BlogRepository;
import com.fly.springboot.service.interfaces.BlogService;
import com.fly.springboot.service.interfaces.EsBlogService;
import com.fly.springboot.service.interfaces.UserService;
import com.fly.springboot.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author fly
 * @date 2018/5/21 0:31
 * @description
 **/
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EsBlogService esBlogService;

    @Transactional
    public Blog saveBlog(Blog blog) {
        Boolean isNew = (blog.getId() == null);
        EsBlog esBlog = null;
        Blog orgBlog = blogRepository.save(blog);
        if(isNew){
            esBlog = new EsBlog(orgBlog);
        }else {
            esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(orgBlog);
        }
        esBlogService.updateEsBlog(esBlog);
        return orgBlog;
    }

    @Transactional
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
        EsBlog esBlog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.removeEsBlog(esBlog.getId());
    }

    public Blog getBlogById(Long id) {
        return blogRepository.getOne(id);
    }

    public Page<Blog> listBlogsByTitleBVote(User user, String title, Pageable pageable) {
        title = (title == null? "%%" : "%" + title + "%");
        String tags = title;
        return blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,
                user,tags, user, pageable);
    }


    public Page<Blog> listBlogsByTitleBVoteAndSort(User user, String title, Pageable pageable) {
        title = (title == null? "%%" : "%" + title + "%");
        return blogRepository.findByUserAndAndTitleLike(user, title, pageable);
    }


    public void readingIncrease(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setReadSize(blog.getReadSize() + 1);
        this.saveBlog(blog);
    }


    public Blog createComment(Long blogId, String commentContent) {
        Blog blog = blogRepository.getOne(blogId);
        User user = userService.getUserByUsername(SecurityUtils.getUser().getUsername());
        Comment comment = new Comment(user, commentContent);
        blog.addComment(comment);
        return this.saveBlog(blog);
    }


    public void removeComment(Long blogId, Long commentId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.removeComment(commentId);
        this.saveBlog(blog);
    }

    public Blog createVote(Long blogId) {
        Blog blog = blogRepository.getOne(blogId);
        User user = userService.getUserByUsername(SecurityUtils.getUser().getUsername());
        Vote vote = new Vote(user);
        if(blog.addvote(vote)){
            throw new IllegalArgumentException("该用户已经点过赞了");
        }
        return this.saveBlog(blog);
    }

    public void removeVote(Long blogId, Long voteId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.removeVote(voteId);
        this.saveBlog(blog);
    }

    public Page<Blog> listBlogByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog, pageable);
    }
}
