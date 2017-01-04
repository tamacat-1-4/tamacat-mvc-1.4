package org.tamacat.mvc.util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.tamacat.dao.meta.Column;
import org.tamacat.dao.meta.DefaultColumn;
import org.tamacat.dao.meta.DefaultTable;
import org.tamacat.dao.orm.MapBasedORMappingBean;

public class BeanUtilsTest {

	@Test
	public void testBindORMappingSupportHttpServletRequestColumnArray() {
		Bean bean = new Bean();
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("name", "ABCDEFG");
		req.addParameter("date", "2015-01-01 00:00:00");
		req.addParameter("number", "0123456789");
		//req.addParameter("note", "");
		
		BeanUtils.bind(bean, req, Bean.TABLE.columns());
		assertEquals("ABCDEFG", bean.val(Bean.NAME));
		assertEquals("2015-01-01 00:00:00", bean.val(Bean.DATE));
		assertEquals("0123456789", bean.val(Bean.NUMBER));
		assertNull(bean.val(Bean.NOTE));
	}

	@Test
	public void testBindORMappingSupportHttpServletRequestColumnString() {
		Bean bean = new Bean();
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("n", "ABCDEFG");
		req.addParameter("d", "2015-01-01 00:00:00");
		req.addParameter("num", "0123456789");
		//req.addParameter("note", "");
		
		BeanUtils.bind(bean, req, Bean.NAME, "n");
		BeanUtils.bind(bean, req, Bean.DATE, "d");
		BeanUtils.bind(bean, req, Bean.NUMBER, "num");
		BeanUtils.bind(bean, req, Bean.NOTE, "note");
		
		assertEquals("ABCDEFG", bean.val(Bean.NAME));
		assertEquals("2015-01-01 00:00:00", bean.val(Bean.DATE));
		assertEquals("0123456789", bean.val(Bean.NUMBER));
		assertNull(bean.val(Bean.NOTE));
	}
	
	@Test
	public void testBindJson() throws Exception {
		Bean test = new Bean();
		test.val(Bean.NAME, "ABCDEFG");
		//test.val(Bean.DATE, DateUtils.parse("2015-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		test.val(Bean.DATE, "2015-01-01 00:00:00");
		test.val(Bean.NUMBER, 1234567890);
		
		String json = test.toJson(Bean.TABLE.columns()).build().toString();
		
		Bean bean = new Bean();
		TestMockHttpServletRequest req = new TestMockHttpServletRequest();
		req.setContentType("application/json");
		ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		req.setInputStream(in);
		
		BeanUtils.bindJson(bean, req, Bean.TABLE.columns());
		
		assertEquals("ABCDEFG", bean.val(Bean.NAME));
		assertEquals("2015-01-01 00:00:00", bean.val(Bean.DATE));
		assertEquals("1234567890", bean.val(Bean.NUMBER));
		assertNull(bean.val(Bean.NOTE));
	}
	
	static class Bean extends MapBasedORMappingBean<Bean> {
		private static final long serialVersionUID = 1L;
		static final DefaultTable TABLE = new DefaultTable("bean");
		static final Column NAME = new DefaultColumn("name");
		static final Column DATE = new DefaultColumn("date");
		static final Column NUMBER = new DefaultColumn("number");
		static final Column NOTE = new DefaultColumn("note");
		
		static {
			TABLE.registerColumn(NAME,DATE,NUMBER,NOTE);
		}
	}
	
	static class TestMockHttpServletRequest extends MockHttpServletRequest {
		BufferedReader reader;
		InputStream in;
		
		public void setReader(BufferedReader reader) {
			this.reader = reader;
		}
		
		public void setInputStream(InputStream in) {
			this.in = in;
		}
		
		@Override
		public BufferedReader getReader() {
			if (reader != null) {
				return reader;
			} else if (in != null) {
				return new BufferedReader(new InputStreamReader(in));
			}
			return null;
		}
	}
}
