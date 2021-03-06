package cn.hutool.db.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.db.ActiveEntity;
import cn.hutool.db.Entity;
import cn.hutool.db.SqlRunner;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Condition.LikeType;
import cn.hutool.db.test.pojo.User;

/**
 * 增删改查测试
 * 
 * @author looly
 *
 */
public class CRUDTest {

	private static SqlRunner runner = SqlRunner.create("test");

	@Test
	public void findIsNullTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", "is null"));
		Assert.assertEquals(0, results.size());
	}
	
	@Test
	public void findIsNullTest2() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", "= null"));
		Assert.assertEquals(0, results.size());
	}
	
	@Test
	public void findIsNullTest3() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", null));
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void findBetweenTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", "between '18' and '40'"));
		Assert.assertEquals(1, results.size());
	}
	
	@Test
	public void findByBigIntegerTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", new BigInteger("12")));
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void findByBigDecimalTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("age", new BigDecimal("12")));
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void findLikeTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("name", "like \"%三%\""));
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void findLikeTest2() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("name", new Condition("name", "三", LikeType.Contains)));
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void findLikeTest3() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("name", new Condition("name", null, LikeType.Contains)));
		Assert.assertEquals(0, results.size());
	}
	
	@Test
	public void findInTest() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("id", "in 1,2,3"));
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void findInTest2() throws SQLException {
		List<Entity> results = runner.findAll(Entity.create("user").set("id", new Condition("id", new long[] {1,2,3})));
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void findAllTest() throws SQLException {
		List<Entity> results = runner.findAll("user");
		Assert.assertEquals(3, results.size());
	}

	@Test
	public void findTest() throws SQLException {
		List<Entity> find = runner.find(CollUtil.newArrayList("name AS name2"), Entity.create("user"), new EntityListHandler());
		Assert.assertFalse(find.isEmpty());
	}
	
	@Test
	public void findActiveTest() throws SQLException {
		ActiveEntity entity = new ActiveEntity(runner, "user");
		entity.setFieldNames("name AS name2").load();
		Assert.assertEquals("user", entity.getTableName());
		Assert.assertFalse(entity.isEmpty());
	}
	
	/**
	 * 对增删改查做单元测试
	 * 
	 * @throws SQLException
	 */
	@Test
	@Ignore
	public void crudTest() throws SQLException {

		// 增
		Long id = runner.insertForGeneratedKey(Entity.create("user").set("name", "unitTestUser").set("age", 66));
		Assert.assertTrue(id > 0);
		Entity result = runner.get("user", "name", "unitTestUser");
		Assert.assertSame(66, (int) result.getInt("age"));

		// 改
		int update = runner.update(Entity.create().set("age", 88), Entity.create("user").set("name", "unitTestUser"));
		Assert.assertTrue(update > 0);
		Entity result2 = runner.get("user", "name", "unitTestUser");
		Assert.assertSame(88, (int) result2.getInt("age"));

		// 删
		int del = runner.del("user", "name", "unitTestUser");
		Assert.assertTrue(del > 0);
		Entity result3 = runner.get("user", "name", "unitTestUser");
		Assert.assertNull(result3);
	}

	@Test
	@Ignore
	public void insertBatchTest() throws SQLException {
		User user1 = new User();
		user1.setName("张三");
		user1.setAge(12);
		user1.setBirthday("19900112");
		user1.setGender(true);

		User user2 = new User();
		user2.setName("李四");
		user2.setAge(12);
		user2.setBirthday("19890512");
		user2.setGender(false);

		Entity data1 = Entity.parse(user1);
		Entity data2 = Entity.parse(user2);

		Console.log(data1);
		Console.log(data2);

		int[] result = runner.insert(CollUtil.newArrayList(data1, data2));
		Console.log(result);
	}

	@Test
	@Ignore
	public void insertBatchOneTest() throws SQLException {
		User user1 = new User();
		user1.setName("张三");
		user1.setAge(12);
		user1.setBirthday("19900112");
		user1.setGender(true);

		Entity data1 = Entity.parse(user1);

		Console.log(data1);

		int[] result = runner.insert(CollUtil.newArrayList(data1));
		Console.log(result);
	}
}
