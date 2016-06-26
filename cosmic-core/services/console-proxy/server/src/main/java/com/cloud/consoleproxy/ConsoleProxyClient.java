package com.cloud.consoleproxy;

import java.awt.Image;
import java.util.List;

/**
 * ConsoleProxyClient defines an standard interface that a console client should implement,
 * <p>
 * ConsoleProxyClient maintains a session towards the target host, it glues the session
 * to a AJAX front-end viewer
 */
public interface ConsoleProxyClient {
    int getClientId();

    //
    // Quick status
    //
    boolean isHostConnected();

    boolean isFrontEndAlive();

    //
    // AJAX viewer
    //
    long getAjaxSessionId();

    AjaxFIFOImageCache getAjaxImageCache();

    Image getClientScaledImage(int width, int height);                  // client thumbnail support

    String onAjaxClientStart(String title, List<String> languages, String guest);

    String onAjaxClientUpdate();

    String onAjaxClientKickoff();

    //
    // Input handling
    //
    void sendClientRawKeyboardEvent(InputEventType event, int code, int modifiers);

    void sendClientMouseEvent(InputEventType event, int x, int y, int code, int modifiers);

    //
    // Info/Stats
    //
    long getClientCreateTime();

    long getClientLastFrontEndActivityTime();

    String getClientHostAddress();

    int getClientHostPort();

    String getClientHostPassword();

    String getClientTag();

    //
    // Setup/house-keeping
    //
    void initClient(ConsoleProxyClientParam param);

    void closeClient();
}
