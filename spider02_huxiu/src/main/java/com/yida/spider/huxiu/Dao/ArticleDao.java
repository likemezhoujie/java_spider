package com.yida.spider.huxiu.Dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yida.spider.huxiu.pojo.Article;


/**
 * JdbcTemplate 指定数据源
 * 		drivermanagersource 数据源bug 
 * 		c3p0,druid
 * 		ComboPooledDataSource
 * @author zhoujie
 *
 */
public class ArticleDao extends JdbcTemplate{
	//通过构造方法加载数据源
	public ArticleDao() {
		// 创建C3P0的datasource 1.配置 2.代码
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		// 1.url
		// 2.driver
		// 3.username&password
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/spider?characterEncoding=utf-8");
		setDataSource(dataSource);
	}
	public void save(Article article) {
		String sql = "INSERT INTO `spider`.`huxiu_article` (`id`, `title`, `author`, `createTime`, `zan`, `pl`, `sc`, `content`, `url` ) VALUES( ?,?,?,?,?,?,?,?,?)";
		update(sql, article.getId(),article.getTitle(),article.getAuthor(),article.getCreateTime(),article.getZan(),article.getPl(),article.getSc(),article.getContent(),article.getUrl());
	}
}
