package com.fly.springboot.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author fly
 * @date 2018/6/2 14:09
 * @description
 **/
@Document(indexName = "blog", type = "blog")
public class EsBlog implements Serializable {

    private static final long serialVersionUID = 5751613584885918185L;

    @Id
    private String id;

    @Field(index = false )
    private Long blogId;

    private String title;

    private String summary;

    private String content;

    @Field(fielddata = true)
    private String username;

    @Field(index = false, fielddata = true)
    private String avatar;

    @Field(index = false, fielddata = true)
    private Timestamp createTime;

    @Field(index = false, fielddata = true)
    private Integer readSize;

    @Field(index = false, fielddata = true)
    private Integer commentSize;

    @Field(index = false, fielddata = true)
    private Integer voteSize;

    @MultiField(mainField = @Field(type = FieldType.Text))
    private String tags;

    protected EsBlog(){

    }

    public EsBlog(String title, String comment) {
        this.title = title;
        this.content = comment;
    }

    public EsBlog(Long blogId, String title, String summary, String comment, String username, String avatar, Timestamp createTime, Integer readSize, Integer commentSize, Integer voteSize, String tags) {
        this.blogId = blogId;
        this.title = title;
        this.summary = summary;
        this.content = comment;
        this.username = username;
        this.avatar = avatar;
        this.createTime = createTime;
        this.readSize = readSize;
        this.commentSize = commentSize;
        this.voteSize = voteSize;
        this.tags = tags;
    }

    public EsBlog(Blog blog) {
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize =blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.tags = blog.getTags();
    }

    public void update(Blog blog) {
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize =blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.tags = blog.getTags();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Integer voteSize) {
        this.voteSize = voteSize;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
