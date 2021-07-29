package xyz.firstmeet.lblog.services;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import xyz.firstmeet.lblog.object.Article;
import xyz.firstmeet.lblog.object.Msg;
import xyz.firstmeet.lblog.services.base.ArticleService;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service("articleJsonService")
public class ArticleJsonService extends ArticleService {
    /**
     * 分页获取文章列表
     *
     * @param userId 用户ID
     * @param page   开始条
     * @param limit  结束条
     * @param sort   排序方式
     * @param status 文章状态，默认published，all为全部文章类型
     * @return Msg 内含文章列表
     */
    public String getArticlesByPage(int userId, int page, int limit, String sort, String status) {
        if (page <= 0) {
            return Msg.getFailMsg();
        }
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        for (Article article : articleMapper.getArticlesByPage((page - 1) * limit, page * limit, sort, status)) {
            arrayList.add(getDetailById(article));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("items", arrayList);
        jsonObject.put("total", articleMapper.getArticleCountByUserId(userId));
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, jsonObject);
    }

    public String getAllLabelsJson() {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, getAllLabels());
    }

    public String getHotLabelsJson() {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, getHotLabels());
    }

    public String findArticleJson(int articleId) {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, getDetailById(articleId));
    }

    public String findArticleAuthorJson(int article_id) {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, findArticleAuthor(article_id));
    }

    /**
     * 获取标签所属文章
     *
     * @param id    标签ID
     * @param start 起始
     * @param end   终
     * @return Msg
     */
    public String getLabelArticlePageJson(int id, int start, int end) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        for (Article article : getLabelArticlePage(id, start, end)) {
            arrayList.add(getDetailById(article));
        }
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, arrayList);
    }

    /**
     * 获取文章所有分类
     *
     * @return Msg
     */
    public String getAllTypeJson() {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, articleMapper.getAllTypes());
    }

    /**
     * 获取用户所有分类信息
     *
     * @param userId 用户ID
     * @return Msg
     */
    public String getAllTypeByUserIdJson(int userId) {
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, articleMapper.getAllTypeByUserId(userId));
    }

    /**
     * 获取所属分类文章
     *
     * @param type   分类ID
     * @param status 文章状态，默认published，all为全部文章类型
     * @return Msg
     */
    public String getArticleByTypeJson(int type, String status) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        for (Article article : articleMapper.getArticlesByType(type, status)) {
            arrayList.add(getDetailById(article));
        }
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, arrayList);
    }

    /**
     * 获取文章创建历史,转化为前端需要的格式
     *
     * @param userId 用户ID
     * @return Msg[{total=int, week_time=String}]
     */
    public String getAddArticleLogByWeekJson(int userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "创作篇");
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        final List<Object> addArticleLogByWeek = getAddArticleLogByWeek(userId);
        for (Object temp : addArticleLogByWeek) {
            x.add((String) ((HashMap) temp).get("week_time"));
            y.add(((Long) ((HashMap) temp).get("total")).intValue());
        }
        //图从左至右，数据应逆序
        Collections.reverse(x);
        Collections.reverse(y);
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("articleAllCount", getArticleCountByUserId(userId));
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, jsonObject);
    }

    /**
     * 添加文章
     *
     * @param article 文章对象
     * @param userId  文章所属用户用户ID
     * @return Msg添加状态
     */
    public String createArticleJson(Article article, int userId) {
        createArticle(article, userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("articleId", article.getId());
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, "发布成功", jsonObject);
    }

    /**
     * 更新文章
     *
     * @param article 文章对象
     * @param userId  文章所属用户用户ID
     * @return Msg更新状态
     */
    @Transient
    public String updateArticleJson(Article article, int userId) {
        if (article.getId() < 1) {
            return Msg.makeJsonMsg(Msg.CODE_FAIL, "文章不存在", null);
        }
        int actualUserId = findArticleAuthor(article.getId()).getId();
        if (actualUserId != userId) {
            return Msg.makeJsonMsg(Msg.CODE_FAIL, "文章所属权异常", null);
        }
        updateArticle(article);
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, "编辑成功", null);
    }


    /**
     * 获取访问最多的文章
     *
     * @param limit  获取截取
     * @param status 文章状态，默认published，all为全部文章类型
     * @return 文章列表Msg
     */
    public String getMostVisitsJson(int limit, String status) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        for (Article article : getMostVisits(limit, status)) {
            arrayList.add(getDetailById(article));
        }
        return Msg.makeJsonMsg(Msg.CODE_SUCCESS, Msg.MSG_SUCCESS, arrayList);
    }

    /**
     * 设置文章状态
     *
     * @param articleId 文章ID
     * @param status    状态
     * @param userId    用户ID
     * @return Msg状态
     */
    public String setStatusJson(int userId, int articleId, Article.Status status) {
        if (userId == findArticleAuthor(articleId).getId()) {
            setStatus(articleId, status);
            return Msg.getSuccessMsg();
        }
        return Msg.getFailMsg();
    }

    /**
     * 删除文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return Msg状态
     */
    public String delArticleJson(int userId, int articleId) {
        if (userId == findArticleAuthor(articleId).getId()) {
            delArticle(articleId);
            return Msg.getSuccessMsg();
        }
        return Msg.getFailMsg();
    }
}
