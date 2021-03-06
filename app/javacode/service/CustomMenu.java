package javacode.service;

import javacode.entity.Token;
import javacode.entity.jsonentity.Menu;
import javacode.entity.xmlentity.*;
import javacode.util.menu.CommonUtil;
import javacode.util.javacode.AssembleMenu;
import javacode.util.menu.MenuUtil;
import javacode.util.xml.XStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/4.
 */
public class CustomMenu {
    private static Logger log = LoggerFactory.getLogger(CustomMenu.class);

    static Class[] classes={MenuEntity.class,ClickButtons.class,ClickButton.class,ViewButtons.class,ViewButton.class,ComplexButtons.class,ComplexButton.class};
    public static XStreamUtils xml = new XStreamUtils(classes);
    public static String configFile = "/menu-config.xml";
    public static MenuEntity menu;
    public static void main(String[] args) throws IOException {
        menu = xml.fromXmlStream(CustomMenu.class.getResourceAsStream(configFile), MenuEntity.class);
        Menu jsonmenu= AssembleMenu.getMenu(menu);

        // 第三方用户唯一凭证
        String appId = "wx965ea0ed89b5dfb9";
        // 第三方用户唯一凭证密钥
        String appSecret = "7489b5604771c16e43f299780b3999cc";

        // 调用接口获取凭证
        Token token =CommonUtil.getToken(appId, appSecret);
        if (null != token) {
            // 创建菜单
            boolean result = MenuUtil.createMenu(jsonmenu, token.getAccess_token());

            // 判断菜单创建结果
            if (result) {
                log.info("菜单创建成功！");
            }
            else
                log.info("菜单创建失败！");
        }
    }
}
