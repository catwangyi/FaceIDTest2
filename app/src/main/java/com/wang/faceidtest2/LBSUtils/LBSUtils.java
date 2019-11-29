package com.wang.faceidtest2.LBSUtils;

import com.baidu.location.BDLocation;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class LBSUtils {
    public static String locationUpdates(BDLocation location){
        if (location != null){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("您的位置是：\n")
                    .append("经度：")
                    .append(location.getLongitude())
                    .append("\n")
                    .append("纬度：")
                    .append(location.getLatitude());
            return stringBuilder.toString();
        }else {
            return "没有GPS信息";
        }
    }
}
