/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * A {@link BasicBatchConfigurer} tailored for JPA.
 *
 * @author Stephane Nicoll
 * @since 2.0.0
 */
public class JpaBatchConfigurer extends BasicBatchConfigurer {

	private static final Log logger = LogFactory.getLog(JpaBatchConfigurer.class);

	private final EntityManagerFactory entityManagerFactory;

	private final String isolationLevelForCreate;

	/**
	 * Create a new {@link BasicBatchConfigurer} instance.
	 * @param properties the batch properties
	 * @param dataSource the underlying data source
	 * @param transactionManagerCustomizers transaction manager customizers (or
	 * {@code null})
	 * @param entityManagerFactory the entity manager factory (or {@code null})
	 */
	protected JpaBatchConfigurer(BatchProperties properties, DataSource dataSource,
			TransactionManagerCustomizers transactionManagerCustomizers, EntityManagerFactory entityManagerFactory) {
		super(properties, dataSource, transactionManagerCustomizers);
		this.entityManagerFactory = entityManagerFactory;
		this.isolationLevelForCreate = properties.getJdbc().getIsolationLevelForCreate();
	}

	@Override
	protected String determineIsolationLevel() {
		if (this.isolationLevelForCreate == null) {
			logger.warn(
					"JPA does not support custom isolation levels, so locks may not be taken when launching Jobs. Define spring.batch.jdbc.isolation-level-for-create property to force a custom isolation level.");
			return "ISOLATION_DEFAULT";
		}

		return this.isolationLevelForCreate;
	}

	@Override
	protected PlatformTransactionManager createTransactionManager() {
		return new JpaTransactionManager(this.entityManagerFactory);
	}

}
