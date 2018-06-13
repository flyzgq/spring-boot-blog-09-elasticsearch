package com.fly.springboot.repository;

import com.fly.springboot.entity.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author fly
 * @date 2018/6/2 21:52
 * @description
 **/
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog, String> {

    /**
     * 模糊查询
     * @param title     标题
     * @param summary   摘要
     * @param content
     * @param tags
     * @param pageable
     * @return
     */
    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String summary, String content, String tags, Pageable pageable);

    /**
     *
     * @param blogId
     * @return
     */
    EsBlog findByBlogId(Long blogId);
}
