package fruitylicious.config

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class DataSourceConfig {

    // -------------------------------------------------------------------------
    // LOCAL (SQLite) — Primary datasource
    // -------------------------------------------------------------------------

    @Primary
    @Bean(name = ["localDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.local")
    fun localDataSource(): DataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Primary
    @Bean(name = ["localEntityManagerFactory"])
    fun localEntityManagerFactory(
        @Qualifier("localDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("Fruitylicous.entity")
        em.persistenceUnitName = "local"

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter

        val props = HashMap<String, Any>()
        props["hibernate.hbm2ddl.auto"] = "update"
        props["hibernate.dialect"] = "org.hibernate.community.dialect.SQLiteDialect"
        props["hibernate.show_sql"] = "false"
        props["hibernate.format_sql"] = "true"
        props["hibernate.jdbc.time_zone"] = "UTC"
        em.setJpaPropertyMap(props)

        return em
    }

    @Primary
    @Bean(name = ["localTransactionManager"])
    fun localTransactionManager(
        @Qualifier("localEntityManagerFactory") emf: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }

    // -------------------------------------------------------------------------
    // ORACLE — Sync-only datasource
    // -------------------------------------------------------------------------

    @Bean(name = ["oracleDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    fun oracleDataSource(): DataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(name = ["oracleEntityManagerFactory"])
    fun oracleEntityManagerFactory(
        @Qualifier("oracleDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("Fruitylicous.entity")
        em.persistenceUnitName = "oracle"

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter

        val props = HashMap<String, Any>()
        props["hibernate.hbm2ddl.auto"] = "update"
        props["hibernate.dialect"] = "org.hibernate.dialect.OracleDialect"
        props["hibernate.show_sql"] = "false"
        props["hibernate.format_sql"] = "true"
        props["hibernate.jdbc.time_zone"] = "UTC"
        em.setJpaPropertyMap(props)

        return em
    }

    @Bean(name = ["oracleTransactionManager"])
    fun oracleTransactionManager(
        @Qualifier("oracleEntityManagerFactory") emf: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }
}

@Configuration
@EnableJpaRepositories(
    basePackages = ["Fruitylicous.repository.local"],
    entityManagerFactoryRef = "localEntityManagerFactory",
    transactionManagerRef = "localTransactionManager"
)
class LocalJpaConfig

@Configuration
@EnableJpaRepositories(
    basePackages = ["Fruitylicous.repository.oracle"],
    entityManagerFactoryRef = "oracleEntityManagerFactory",
    transactionManagerRef = "oracleTransactionManager"
)
class OracleJpaConfig