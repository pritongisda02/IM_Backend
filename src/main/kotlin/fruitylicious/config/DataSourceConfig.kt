package fruitylicious.config

import oracle.jdbc.pool.OracleDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Value("\${datasource.url}")
    private lateinit var url: String

    @Value("\${datasource.username}")
    private lateinit var username: String

    @Value("\${datasource.password}")
    private lateinit var password: String

    @Primary
    @Bean
    fun dataSource(): DataSource {
        val ds = OracleDataSource()
        ds.url = url
        ds.user = username
        ds.setPassword(password)
        return ds
    }
}