/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.sfd2015.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import scribe.thrift.LogEntry;
import scribe.thrift.scribe.Client;
import sg.sfd2015.demo.utils.AppParams;
import sg.sfd2015.demo.utils.scribe.ScribeClient;

/**
 *
 * @author hungnguyen
 */
public class ApiHandler extends HttpServlet {

    private static final org.slf4j.Logger _logger = LoggerFactory.getLogger(ApiHandler.class);
    private final static ScribeClient myInstance = ScribeClient.getInstance("127.0.0.1", 1463, 1000);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Client client = null;
        int num = 0;
        String action = AppParams.getStrParams(req, "act").toLowerCase();
        if (action.equalsIgnoreCase("put")) {
            num = AppParams.getIntParams(req, "num");
            try {
                num += new Random().nextInt(99) + 1;
                resp.getWriter().println("I got " + num);

            } catch (IOException ex) {
                _logger.error("I can't get anything");
            }
        } else if (action.equalsIgnoreCase("get")) {
            num = new Random().nextInt(99) + 1;
            resp.getWriter().println("I give you " + num);

        }
        String msg = new StringBuffer().append(action).append("\t").append(num).toString();
        try {
            ArrayList<LogEntry> arrayList = new ArrayList<LogEntry>();
            arrayList.add(new LogEntry("SFD", msg));
            client = myInstance.getClient();
            client.Log(arrayList);
        } catch (Exception ex) {
            _logger.error("I can't give anything");
        } finally {
            if (client != null) {
                myInstance.returnClient(client);
            }
        }
    }
}
