package com.mkfree.blog.action;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mkfree.blog.domain.Article;
import com.mkfree.blog.service.ArticleService;
import com.mkfree.framework.common.page.Pagination;

public class ArticleController {

	private static ApplicationContext app;
	private static ArticleService articleService;

	@Test
	public void listArticle() {
		Pagination<Article> page = articleService.getPageArticle(2, 10);
		System.out.println(page.getTotalCount());
	}

	@Test
	public void save() {
		for (int i = 1; i < 21; i++) {
			Article a = new Article();
			a.setTitle("mongodb开始实战" + i);
			a.setContent("mongodb开始实战..内容" + i);
			Article aa = articleService.save(a);
			System.out.println(aa.getId());
		}
	}

	@Test
	public void findArticle() {
		Article a = articleService.findByid("5902e0604b28f61bbf16b348");
		System.out.println(a);
	}

	@Test
	public void update() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("title", "修改内容...dsds");
		articleService.update("5902e0604b28f61bbf16b348", params);
	}
	
	@BeforeClass
	public static void initSpring() {
		app = new ClassPathXmlApplicationContext(new String[] { "classpath:spring/framework-context.xml",
				"classpath:spring/mongodb.xml" });
		articleService = (ArticleService) app.getBean("articleService");
	}
}
