package org.tamacat.mvc.util;

import static org.junit.Assert.*;

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
	
	static class Bean extends MapBasedORMappingBean {
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
}
