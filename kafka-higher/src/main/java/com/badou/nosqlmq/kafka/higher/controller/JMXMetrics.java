package com.badou.mqnosql.kafka.higher.controller;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXMetrics {

	private String iport;
	private String jmxURL;
	private MBeanServerConnection conn;

	public JMXMetrics(String iport) {
		this.iport = iport;
		this.init();
	}

	private void init() {
		this.jmxURL = "service:jmx:rmi:///jndi/rmi://" + iport + "/jmxrmi";
		try {
			JMXServiceURL serviceURL = new JMXServiceURL(this.jmxURL);
			JMXConnector connector = JMXConnectorFactory.connect(serviceURL, null);
			this.conn = connector.getMBeanServerConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getMessagesInPerSecCount() throws Exception {
		if (this.conn == null) {
			return -1;
		}

		ObjectName objectName = new ObjectName("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec,topic=heima");
		Object count = this.conn.getAttribute(objectName, "Count");
		return Integer.parseInt(count.toString());
	}

	public static void main(String[] args) throws Exception {
		JMXMetrics connection = new JMXMetrics("192.168.2.202:9999");
		System.out.println(connection.getMessagesInPerSecCount());
	}

}
