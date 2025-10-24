package org.example.replicapractice.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		// ThreadLocal에서 명시적으로 설정된 DataSource 확인
		DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();

		if (dataSourceType != null) {
			String type = dataSourceType == DataSourceType.MASTER ? "master" : "slave";
			System.out.println("Current DataSource (explicitly set): " + type);
			return type;
		}

		// 명시적 설정이 없으면 트랜잭션 readOnly 여부로 판단
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		String type = isReadOnly ? "slave" : "master";
		System.out.println("Current DataSource (by transaction): " + type);

		return type;
	}
}
