package xyz.firstmeet.lblog.services.base;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.firstmeet.lblog.mapper.SysConfigMapper;
import xyz.firstmeet.lblog.mapper.UserMapper;
import xyz.firstmeet.lblog.object.SystemConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("sysConfigService")
public class SysConfigService {
    protected SysConfigMapper sysConfigMapper;
    protected UserMapper userMapper;

    @Autowired
    public void setSystemConfigMapper(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取用户配置
     *
     * @param user_id 用户ID
     * @return 配置对象
     */
    public SystemConfig getConfigByUserId(int user_id) {
        final List<JSONObject> sysUiConfigByUserId = sysConfigMapper.getUiConfigByUserId(user_id);
        HashMap<String, String> rs = new HashMap<>();
        for (JSONObject json : sysUiConfigByUserId) {
            rs.put(json.getString("item"), json.getString("value"));
        }
        Map<String, String> sysUiConfig = getSysConfig();
        rs.put("filing_icp", sysUiConfig.getOrDefault("filing_icp", ""));
        rs.put("filing_security", sysUiConfig.getOrDefault("filing_security", ""));
        rs.put("admin_url", sysUiConfig.getOrDefault("admin_url", ""));
        if (sysUiConfigByUserId.size() > 0) {
            rs.put("user_id", sysUiConfigByUserId.get(0).getString("user_id"));
        }
        //oss配置不可轻易发送至前台
        return JSONObject.parseObject(JSONObject.toJSONString(rs), SystemConfig.class);
    }

    /**
     * 获取用户UI配置
     *
     * @param user_id 用户ID
     * @return 配置表
     */
    public Map<String, String> getUiConfigByUserId(int user_id) {
        final List<JSONObject> sysUiConfigByUserId = sysConfigMapper.getUiConfigByUserId(user_id);
        HashMap<String, String> rs = new HashMap<>();
        for (JSONObject json : sysUiConfigByUserId) {
            rs.put(json.getString("item"), json.getString("value"));
        }
        rs.remove("about");
        return rs;
    }

    /**
     * 获取系统默认配置
     *
     * @return 系统配置表
     */
    public Map<String, String> getSysConfig() {
        List<JSONObject> sysUiConfig = sysConfigMapper.getSysConfig();
        Map<String, String> rs = new HashMap<>();
        for (JSONObject temp : sysUiConfig) {
            rs.put(temp.getString("item"), temp.getString("value"));
        }
        return rs;
    }

    /**
     * 设置系统配置
     *
     * @param configMap 配置表
     */
    public void setSysConfig(Map<String, String> configMap) {
        sysConfigMapper.setSysConfig(configMap);
    }

    /**
     * 设置某个用户的UI配置
     *
     * @param user_id   用户ID
     * @param configMap 配置表
     */
    public void setUiConfigByUserId(int user_id, Map<String, String> configMap) {
        sysConfigMapper.setUiConfigByUserId(user_id, configMap);
    }

    /**
     * 获取系统存储配置文件
     *
     * @return SystemConfig
     */
    public JSONObject getStorageConfig() {
        return JSONObject.parseObject(sysConfigMapper.getOSSConfig(), Feature.OrderedField);
    }

    /**
     * 设置系统存储配置文件
     *
     * @param storage 存储设置Json对象
     */
    public void setStorageConfig(JSONObject storage) {
        JSONObject storageConfig = getStorageConfig();
        storageConfig.put("storage", storage);
        sysConfigMapper.setStorageConfig(storageConfig.toJSONString());
    }
}
