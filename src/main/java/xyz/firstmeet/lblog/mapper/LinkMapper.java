package xyz.firstmeet.lblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.firstmeet.lblog.object.FriendLink;

import java.util.List;

@Mapper
public interface LinkMapper {
    /**
     * 获取友链列表
     *
     * @param status 友链状态
     * @return List<FriendLink>
     */
    List<FriendLink> getFriendLinks(@Param("status") FriendLink.Status status);

    /**
     * 申请友链
     *
     * @param friendLink 友链对象
     */
    void applyFriendLink(@Param("friendLink") FriendLink friendLink);


    /**
     * 修改友链 状态
     *
     * @param id     友链对应ID
     * @param status 状态
     */
    void setFriendLinkStatus(@Param("id") int id, @Param("status") FriendLink.Status status);
}
