package org.example.replicapractice.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		String dataSourceType = isReadOnly ? "slave" : "master";

		System.out.println("Current DataSource: " + dataSourceType);

		return dataSourceType;
	}
}
