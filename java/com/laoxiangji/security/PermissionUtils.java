package com.laoxiangji.security;

import com.laoxiangji.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PermissionUtils {

    /**
     * 判断当前用户是否可以管理目标用户
     * @param currentUser 当前用户
     * @param targetUser 目标用户
     * @return 是否有管理权限
     */
    public boolean canManageUser(User currentUser, User targetUser) {
        // 用户可以查看和编辑自己的信息
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }

        String currentLevel = currentUser.getLevel();
        String targetLevel = targetUser.getLevel();

        // 获取用户级别的优先级
        int currentPriority = getLevelPriority(currentLevel);
        int targetPriority = getLevelPriority(targetLevel);

        // 市级用户（优先级1）不能管理其他人
        if (currentPriority == 1) {
            return false;
        }

        // 只能管理比自己级别低的用户
        if (currentPriority <= targetPriority) {
            return false;
        }

        // 根据不同级别进行权限判断
        switch (currentLevel) {
            case "全国":
            case "管理员":
                // 全国级可以管理所有非全国级用户
                return !"全国".equals(targetLevel) && !"管理员".equals(targetLevel);

            case "战区":
                // 战区级可以管理本战区的省级和市级用户
                if ("省".equals(targetLevel) || "市".equals(targetLevel)) {
                    return currentUser.getZone() != null &&
                            currentUser.getZone().equals(targetUser.getZone());
                }
                return false;

            case "省":
                // 省级可以管理本省的市级用户
                if ("市".equals(targetLevel)) {
                    return currentUser.getProvince() != null &&
                            currentUser.getProvince().equals(targetUser.getProvince());
                }
                return false;

            default:
                return false;
        }
    }

    /**
     * 获取用户级别的优先级（数字越大权限越高）
     * @param level 用户级别
     * @return 优先级
     */
    public int getLevelPriority(String level) {
        switch (level) {
            case "全国":
            case "管理员":
                return 4;
            case "战区":
                return 3;
            case "省":
                return 2;
            case "市":
            default:
                return 1;
        }
    }

    /**
     * 判断用户是否有进入人员管理界面的权限
     * @param user 用户
     * @return 是否有权限
     */
    public boolean canAccessPersonnelManagement(User user) {
        // 市级用户无权进入人员管理界面
        return getLevelPriority(user.getLevel()) > 1;
    }
}