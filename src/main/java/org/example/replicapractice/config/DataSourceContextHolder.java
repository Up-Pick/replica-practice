package org.example.replicapractice.config;

public class DataSourceContextHolder {

	private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

	public static void setDataSourceType(DataSourceType dataSourceType) {
		CONTEXT.set(dataSourceType);
	}

	public static DataSourceType getDataSourceType() {
		return CONTEXT.get();
	}

	public static void clearDataSourceType() {
		CONTEXT.remove();
	}
}
