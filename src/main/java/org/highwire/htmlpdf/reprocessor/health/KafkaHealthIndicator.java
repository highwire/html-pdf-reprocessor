package org.highwire.htmlpdf.reprocessor.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

	private final AdminClient kafkaAdminClient;

	public KafkaHealthIndicator(AdminClient kafkaAdminClient) {
		this.kafkaAdminClient = kafkaAdminClient;
	}

	 @Override
	    public Health health() {
	        try {
	            // Lightweight check: Verify if brokers are reachable
	            DescribeClusterResult clusterResult = kafkaAdminClient.describeCluster();
	            int nodeCount = clusterResult.nodes().get(5, TimeUnit.SECONDS).size();
	            String clusterId = clusterResult.clusterId().get(5, TimeUnit.SECONDS);

	            return Health.up()
	                    .withDetail("clusterId", clusterId)
	                    .withDetail("brokerCount", nodeCount)
	                    .build();

	        } catch (Exception e) {
	            // If any exception occurs, mark Kafka as DOWN
	            return Health.down()
	                    .withDetail("error", e.getMessage())
	                    .build();
	        }
	    }

}
