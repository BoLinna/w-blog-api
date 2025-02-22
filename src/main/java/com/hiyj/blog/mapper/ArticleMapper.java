package com.hiyj.blog.mapper;

import com.hiyj.blog.object.Article;
import com.hiyj.blog.object.ArticleLabel;
import com.hiyj.blog.object.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ArticleMapper {

    /**
     * 获取用户文章数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    int getArticleCountByUserId(@Param("userId") int userId);

    /**
     * 获取用户所有访问量
     *
     * @param userId 用户ID
     * @return 访问量
     */
    int getVisitsAllCountByUserId(@Param("userId") int userId);

    /**
     * 逆序分页获取文章
     *
     * @param limit  限制量
     * @param offset 偏移量
     * @param sort   排序方式 默认-id,
     * @return 文章列表
     */
    List<Article> getArticlesByPage(@Param("limit") int limit, @Param("offset") int offset, @Param("sort") String sort, @Param("status") Article.Status status);

    /**
     * 分页获取文章ID
     *
     * @param limit  限制量
     * @param offset 偏移量
     * @param sort   排序方式 默认-id,
     * @param status 文章状态
     * @return List<Integer>
     */
    List<Integer> getArticleIdByPage(@Param("limit") int limit, @Param("offset") int offset, @Param("sort") String sort, @Param("status") Article.Status status);

    /**
     * 根据ID排序，第一个为所属分类
     *
     * @param article_id 文章ID
     * @return 标签列表
     */
    List<ArticleLabel> getArticleMapLabels(@Param("article_id") int article_id);

    /**
     * 根据文章ID查找文章
     *
     * @param article_id 文章ID
     * @return 文章
     */
    Article findArticleId(@Param("article_id") int article_id);

    /**
     * 查找文章作者
     *
     * @param article_id 文章ID
     * @return User
     */
    @Select("select * from (SELECT user_id FROM user_map_article uma where uma.article_id =#{article_id}) uid, `user` u where u.id =uid.user_id ")
    User findArticleAuthor(@Param("article_id") int article_id);

    /**
     * 获取标签所属文章
     *
     * @param id     标签ID
     * @param limit  限制数
     * @param offset 偏移量量
     * @return 文章列表
     */
    List<Article> getLabelArticlePage(@Param("label_id") int id, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 获取所属分类文章
     *
     * @param type   分类ID
     * @param status 文章状态，默认published，all为全部文章类型
     * @return List Article
     */
    List<Article> getArticlesByType(@Param("label_id") int type, @Param("status") Article.Status status);

    /**
     * 访问量加一
     *
     * @param target_id 目标ID
     * @param type      目标类型
     * @return 影响row
     */
    @Update("UPDATE visits_count SET count=count+1 WHERE target_id=#{target_id} AND `type`=#{type} AND TIME= CURDATE();")
    int addVisitsCount(@Param("target_id") int target_id, @Param("type") int type);

    /**
     * 添加新的访问量行
     *
     * @param target_id 目标ID
     * @param type      类型
     */
    @Insert("INSERT INTO visits_count(`target_id`, `type`, `time`) VALUES(#{target_id}, #{type}, CURDATE());")
    void addVisitsRow(@Param("target_id") int target_id, @Param("type") int type);

    /**
     * 获取访问总量和趋势数据
     *
     * @param userId 用户ID
     * @return List<Map < String, Object>>, 日期数值键值对{total=int, day_time=String}
     */
    List<Map<String, Object>> getVisitLogByDay(@Param("userId") int userId);

    /**
     * 获取文章创建历史
     *
     * @param userId 用户ID
     * @return List<Map < String, Object>>, 日期数值键值对{total=int, day_time=week_time}
     */
    List<Map<String, Object>> getArticleCreateLogByWeek(@Param("userId") int userId);

    /**
     * 添加文章，不含标签和文章所属用户映射
     *
     * @param article 文章对象
     * @return 插入条
     */
    int addArticle(@Param("article") Article article);

    /**
     * 添加用户——文章映射
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     */
    void addUserMapArticle(@Param("userId") int userId, @Param("articleId") int articleId);

    /**
     * 添加文章——标签映射
     *
     * @param articleId     文章ID
     * @param articleLabels 文章标签对象列表
     */
    void addArticleMapLabels(@Param("articleId") int articleId, @Param("articleLabels") List<ArticleLabel> articleLabels);

    /**
     * 获取访问最多的文章
     *
     * @param limit 获取截取
     * @return 文章列表
     */
    List<Article> getMostVisits(@Param("limit") int limit);

    /**
     * 修改文章
     *
     * @param article 文章ID
     */
    void updateArticle(@Param("article") Article article);

    /**
     * 设置文章状态
     *
     * @param articleId 文章ID
     * @param status    状态
     */
    @Update("UPDATE article SET status=#{status} WHERE id=#{articleId}")
    void setStatus(@Param("articleId") int articleId, @Param("status") Article.Status status);

    /**
     * 根据文章ID删除文章
     *
     * @param articleId 文章ID
     */
    @Delete("DELETE FROM article WHERE id=#{articleId};")
    void delArticle(@Param("articleId") int articleId);

    /**
     * 获取该文章的下一篇文章
     *
     * @param articleId 当前文章ID
     * @return 文章对象
     */
    @Select("select * from article a where a.id > #{articleId} limit 1")
    Article getNextArticle(@Param("articleId") int articleId);

    /**
     * 获取该文章的上一篇文章
     *
     * @param articleId 当前文章ID
     * @return 文章对象
     */
    @Select("select * from article a where a.id < #{articleId} order by a.id desc limit 1")
    Article getPreArticle(@Param("articleId") int articleId);
}
