/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.sfd2015.demo.utils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author hungnguyen
 */
public class AppParams {

    public static int getIntParams(HttpServletRequest req, String param) {
        int rs = 0;
        try {
            if (req.getParameter(param) != null) {
                rs = Integer.valueOf(req.getParameter(param).toString());
            } else if (req.getAttribute(param) != null) {
                rs = Integer.valueOf(req.getAttribute(param).toString());
            }
        } catch (NumberFormatException nex) {
        }
        return rs;
    }

    public static String getStrParams(HttpServletRequest req, String param) {
        String rs = "";
        if (req.getParameter(param) != null) {
            rs = req.getParameter(param).toString();
        } else if (req.getAttribute(param) != null) {
            rs = req.getAttribute(param).toString();
        }
        return rs;
    }

    public static long getLongParams(HttpServletRequest req, String param) {
        long rs = 0;
        if (req.getParameter(param) != null) {
            rs = Long.valueOf(req.getParameter(param).toString());
        } else if (req.getAttribute(param) != null) {
            rs = Long.valueOf(req.getAttribute(param).toString());
        }
        return rs;
    }
}
