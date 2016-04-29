package edu.purdue.cs490.server;


import edu.purdue.cs490.server.api.status.Status;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class LabWatcher implements Runnable{
    private static final Logger log = Logger.getLogger(Status.class.getName());

    public void run() {
        while (true) {

            try {
                List<String> computers =  Server.getInstance().getSQLData().getNonRespondingLinux();
                computers.stream().forEach(c -> startScript(c));

            } catch (SQLException e) {
                log.warning("Unable to fetch non responding linux computers.");
            }



            try {
                Thread.sleep(10 * 60000);
            } catch (InterruptedException e) {
                log.warning("Warning! Red alert!");
            }
        }
    }

    private void startScript(String address) {
        log.info("Restarting script on: " + address);
        try {
            Runtime.getRuntime().exec("ssh " + address +
                    " python3 ~/projects/CS490-Lab-Broadcast-Server/scripts/linux_stat_daemon.py -u mc15 -p 5000");

        } catch (IOException e) {
            log.warning("Unable to restart script on: " + address);
        }
    }
}
