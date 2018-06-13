package com.fly.springboot.service.impl;

import com.fly.springboot.entity.EsBlog;
import com.fly.springboot.entity.User;
import com.fly.springboot.model.TagVo;
import com.fly.springboot.repository.EsBlogRepository;
import com.fly.springboot.service.interfaces.EsBlogService;
import com.fly.springboot.service.interfaces.UserService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

/**
 * @author fly
 * @date 2018/6/2 22:24
 * @description
 **/
@Service
public class EsBlogServiceImpl implements EsBlogService {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private UserService userService;

    private static final Pageable TOP_5_PAGEABLE = PageRequest.of(0, 5);
    private static final String EMPTY_KEYWORD = "";
    /**
     * 删除Blog
     *
     * @param id
     * @return
     */
    public void removeEsBlog(String id) {
        esBlogRepository.deleteById(id);
    }

    /**
     * 更新 EsBlog
     *
     * @param esBlog
     * @return
     */
    public EsBlog updateEsBlog(EsBlog esBlog) {
        return esBlogRepository.save(esBlog);
    }

    /**
     * 根据id获取Blog
     *
     * @param blogId
     * @return
     */
    public EsBlog getEsBlogByBlogId(Long blogId) {
        return esBlogRepository.findByBlogId(blogId);
    }

    /**
     * 最新博客列表，分页
     *
     * @param keyword
     * @param pageable
     * @return
     */
    public Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable) {
        Page<EsBlog> page = null;
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        if(pageable.getSort() == null){
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        page = esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(keyword, keyword, keyword, keyword, pageable);
        return page;
    }

    /**
     * 最热博客列表，分页
     *
     * @param keyword
     * @param pageable
     * @return
     */
    public Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable) {
        Sort sort = new Sort(Sort.Direction.DESC, "readSize", "commentSize", "voteSize", "createTime");
        if(pageable.getSort() == null){
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(keyword, keyword, keyword, keyword, pageable);
    }

    /**
     * 博客列表，分页
     *
     * @param pageable
     * @return
     */
    public Page<EsBlog> listEsBlogs(Pageable pageable) {
        return esBlogRepository.findAll(pageable);
    }

    /**
     * 最新前5
     *
     * @return
     */
    public List<EsBlog> listTop5NewestEsBlogs() {
        Page<EsBlog> page = this.listNewestEsBlogs(EMPTY_KEYWORD, TOP_5_PAGEABLE);
        return page.getContent();
    }

    /**
     * 最热前5
     *
     * @return
     */
    public List<EsBlog> listTop5HotestEsBlogs() {
        Page<EsBlog> page = this.listHotestEsBlogs(EMPTY_KEYWORD, TOP_5_PAGEABLE);
        return page.getContent();
    }

    /**
     * 最热前 30 标签
     *
     * @return
     */
    public List<TagVo> listTop30Tags() {
        List<TagVo> list = new ArrayList<TagVo>();
        // given
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blog").withTypes("blog")
                .addAggregation(terms("tags").field("tags").order(Terms.Order.count(false)).size(30))
                .build();
        // when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }

        });

        StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("tags");

        Iterator<StringTerms.Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            StringTerms.Bucket actiontypeBucket = modelBucketIt.next();
            list.add(new TagVo(actiontypeBucket.getKey().toString(),
                    actiontypeBucket.getDocCount()));
        }
        return list;
    }

    /**
     * 最热前12用户
     *
     * @return
     */
    public List<User> listTop12Users() {
        List<String> usernamelist = new ArrayList<String>();
        // given
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.QUERY_THEN_FETCH)
                .withIndices("blog").withTypes("blog")
                .addAggregation(terms("users").field("username").order(Terms.Order.count(false)).size(12))
                .build();
        // when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StringTerms modelTerms =  (StringTerms)aggregations.asMap().get("users");

        Iterator<StringTerms.Bucket> modelBucketIt = modelTerms.getBuckets().iterator();
        while (modelBucketIt.hasNext()) {
            StringTerms.Bucket actiontypeBucket = modelBucketIt.next();
            String username = actiontypeBucket.getKey().toString();
            usernamelist.add(username);
        }
        List<User> list = userService.listUsersByUsernames(usernamelist);

        return list;
    }
}
