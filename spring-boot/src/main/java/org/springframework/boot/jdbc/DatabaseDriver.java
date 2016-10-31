/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.jdbc;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Enumeration of common database drivers.
 *
 * @author Phillip Webb
 * @author Maciej Walkowiak
 * @author Marten Deinum
 * @author Stephane Nicoll
 * @since 1.4.0
 */
public enum DatabaseDriver {

	/**
	 * Unknown type.
	 */
	UNKNOWN("unknown", null, null),

	/**
	 * Apache Derby.
	 */
	DERBY("derby", "Apache Derby", "org.apache.derby.jdbc.EmbeddedDriver",
			"org.apache.derby.jdbc.EmbeddedXADataSource",
			"SELECT 1 FROM SYSIBM.SYSDUMMY1"),

	/**
	 * H2.
	 */
	H2("h2", "H2", "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", "SELECT 1"),

	/**
	 * HyperSQL DataBase.
	 */
	HSQLDB("hsqldb", "HSQL Database Engine", "org.hsqldb.jdbc.JDBCDriver",
			"org.hsqldb.jdbc.pool.JDBCXADataSource",
			"SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS"),

	/**
	 * SQL Lite.
	 */
	SQLITE("sqlite", "SQLite", "org.sqlite.JDBC"),

	/**
	 * MySQL.
	 */
	MYSQL("mysql", "MySQL", "com.mysql.jdbc.Driver",
			"com.mysql.jdbc.jdbc2.optional.MysqlXADataSource", "SELECT 1"),

	/**
	 * Maria DB.
	 */
	MARIADB("mariadb", "MySQL", "org.mariadb.jdbc.Driver",
			"org.mariadb.jdbc.MariaDbDataSource", "SELECT 1"),

	/**
	 * Google App Engine.
	 */
	GAE("gae", null, "com.google.appengine.api.rdbms.AppEngineDriver"),

	/**
	 * Oracle.
	 */
	ORACLE("oracle", "Oracle", "oracle.jdbc.OracleDriver",
			"oracle.jdbc.xa.client.OracleXADataSource", "SELECT 'Hello' from DUAL"),

	/**
	 * Postgres.
	 */
	POSTGRESQL("postgresql", "PostgreSQL", "org.postgresql.Driver",
			"org.postgresql.xa.PGXADataSource", "SELECT 1"),

	/**
	 * jTDS. As it can be used for several databases, there isn't a single product name we
	 * could rely on.
	 */
	JTDS("jtds", null, "net.sourceforge.jtds.jdbc.Driver"),

	/**
	 * SQL Server.
	 */
	SQLSERVER("sqlserver", "SQL SERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
			"com.microsoft.sqlserver.jdbc.SQLServerXADataSource", "SELECT 1"),

	/**
	 * Firebird.
	 */
	FIREBIRD("firebird", "Firebird", "org.firebirdsql.jdbc.FBDriver",
			"org.firebirdsql.pool.FBConnectionPoolDataSource",
			"SELECT 1 FROM RDB$DATABASE") {

		@Override
		protected boolean matchProductName(String productName) {
			return super.matchProductName(productName)
					|| productName.toLowerCase().startsWith("firebird");
		}
	},

	/**
	 * DB2 Server.
	 */
	DB2("db2", "DB2", "com.ibm.db2.jcc.DB2Driver", "com.ibm.db2.jcc.DB2XADataSource",
			"SELECT 1 FROM SYSIBM.SYSDUMMY1") {

		@Override
		protected boolean matchProductName(String productName) {
			return super.matchProductName(productName)
					|| productName.toLowerCase().startsWith("db2/");
		}
	},

	/**
	 * DB2 AS400 Server.
	 */
	DB2_AS400("db2", "DB2 UDB for AS/400", "com.ibm.as400.access.AS400JDBCDriver",
			"com.ibm.as400.access.AS400JDBCXADataSource",
			"SELECT 1 FROM SYSIBM.SYSDUMMY1") {

		@Override
		protected boolean matchProductName(String productName) {
			return super.matchProductName(productName)
					|| productName.toLowerCase().contains("as/400");
		}
	},

	/**
	 * Teradata.
	 */
	TERADATA("teradata", "Teradata", "com.teradata.jdbc.TeraDriver"),

	/**
	 * Informix.
	 */
	INFORMIX("informix", "Informix Dynamic Server", "com.informix.jdbc.IfxDriver", null,
			"select count(*) from systables");

	private final String id;

	private final String productName;

	private final String driverClassName;

	private final String xaDataSourceClassName;

	private final String validationQuery;

	DatabaseDriver(String id, String name, String driverClassName) {
		this(id, name, driverClassName, null);
	}

	DatabaseDriver(String id, String name, String driverClassName,
			String xaDataSourceClassName) {
		this(id, name, driverClassName, xaDataSourceClassName, null);
	}

	DatabaseDriver(String id, String productName, String driverClassName,
			String xaDataSourceClassName, String validationQuery) {
		this.id = id;
		this.productName = productName;
		this.driverClassName = driverClassName;
		this.xaDataSourceClassName = xaDataSourceClassName;
		this.validationQuery = validationQuery;
	}

	/**
	 * Return the identifier of this driver.
	 * @return the identifier
	 */
	public String getId() {
		return this.id;
	}

	protected boolean matchProductName(String productName) {
		return this.productName != null && this.productName.equalsIgnoreCase(productName);
	}

	/**
	 * Return the driver class name.
	 * @return the class name or {@code null}
	 */
	public String getDriverClassName() {
		return this.driverClassName;
	}

	/**
	 * Return the XA driver source class name.
	 * @return the class name or {@code null}
	 */
	public String getXaDataSourceClassName() {
		return this.xaDataSourceClassName;
	}

	/**
	 * Return the validation query.
	 * @return the validation query or {@code null}
	 */
	public String getValidationQuery() {
		return this.validationQuery;
	}

	/**
	 * Find a {@link DatabaseDriver} for the given URL.
	 * @param url JDBC URL
	 * @return the database driver or {@link #UNKNOWN} if not found
	 */
	public static DatabaseDriver fromJdbcUrl(String url) {
		if (StringUtils.hasLength(url)) {
			Assert.isTrue(url.startsWith("jdbc"), "URL must start with 'jdbc'");
			String urlWithoutPrefix = url.substring("jdbc".length()).toLowerCase();
			for (DatabaseDriver driver : values()) {
				String prefix = ":" + driver.name().toLowerCase() + ":";
				if (driver != UNKNOWN && urlWithoutPrefix.startsWith(prefix)) {
					return driver;
				}
			}
		}
		return UNKNOWN;
	}

	/**
	 * Find a {@link DatabaseDriver} for the given product name.
	 * @param productName product name
	 * @return the database driver or {@link #UNKNOWN} if not found
	 */
	public static DatabaseDriver fromProductName(String productName) {
		if (StringUtils.hasLength(productName)) {
			for (DatabaseDriver candidate : values()) {
				if (candidate.matchProductName(productName)) {
					return candidate;
				}
			}
		}
		return UNKNOWN;
	}

}
