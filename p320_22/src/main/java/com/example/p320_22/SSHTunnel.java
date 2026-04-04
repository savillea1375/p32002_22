package com.example.p320_22;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import io.github.cdimascio.dotenv.Dotenv;

public class SSHTunnel {
	private static Session session;

	/** Start SSH tunneling to RIT starbug server so that
	 *  connecting to DB is actually possible
	 */
    public static void startTunnel() {
		if (session != null && session.isConnected()) {
            return;
        }

		Dotenv dotenv = Dotenv.load();

		String user = dotenv.get("DB_USERNAME");
		String password = dotenv.get("PASSWORD");
        String sshHost = "starbug.cs.rit.edu";
        int sshPort = 22;

        String dbHost = "127.0.0.1";
        int dbPort = 5432;
        int localPort = 5433;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, sshHost, sshPort);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            session.setPortForwardingL(localPort, dbHost, dbPort);

			System.out.println("SSH tunnel established on port " + localPort);
        } catch (Exception e) {
			System.err.println("Couldn't start SSH tunnel");
            e.printStackTrace();
        }
    }
}