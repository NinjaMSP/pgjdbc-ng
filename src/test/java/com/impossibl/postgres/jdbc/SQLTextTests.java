package com.impossibl.postgres.jdbc;

import static com.impossibl.postgres.jdbc.SQLTextUtils.getProtocolSQLText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;


public class SQLTextTests {

	String[][] sqlTransformTests = new String[][] {
		new String[] {
				
				//Input
				"select \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	from\n" +
				"		test\n" +
				"	where\n" +
				"		'a string with a ?' =  ?",
				
				//Output
				"select \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	from\n" +
				"		test\n" +
				"	where\n" +
				"		'a string with a ?' =  $1"
		},			
		new String[] {
	
				//Input
				"insert into \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	(a, \"b\", \"c\", \"d\")\n" +
				"	values /* a nested\n" +
				" /* comment with  */ a ? */" +
				"	(?,'a string with a ?', \"another ?\", ?, ?)",
			
				//Output
				"insert into \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	(a, \"b\", \"c\", \"d\")\n" +
				"	values /* a nested\n" +
				" /* comment with  */ a ? */" +
				"	($1,'a string with a ?', \"another ?\", $2, $3)",
		},
		new String[] {
				
				//Input
				"insert into \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	(a, \"b\", \"c\", \"d\")\n" +
				"	values /* a nested\n" +
				" /* comment with  */ a ? */" +
				"	(?,'a string with a ?', \"another  \"\" ?\", {fn concat('{fn '' some()}', {fn char(?)})}, ?)",
			
				//Output
				"insert into \"somthing\" -- This is a SQL comment ?WTF?\n" +
				"	(a, \"b\", \"c\", \"d\")\n" +
				"	values /* a nested\n" +
				" /* comment with  */ a ? */" +
				"	($1,'a string with a ?', \"another \"\" ?\", concat('{fn '' some()}', insert($2)), $3), $4)",
		},
		new String[] {
				"select {fn abs(-10)} as absval, {fn user()}, {fn concat(x,y)} as val from {oj tblA left outer join tblB on x=y}",
				"select abs(-10) as absval, concat(x,y) as val from tblA left outer join tblB ON (x=y)",
		}
	};

	/**
	 * Tests transforming JDBC SQL input into PostgreSQL's wire
	 * protocol format. 
	 * @throws SQLException 
	 * 
	 * @see SQLTextUtils.getProtocolSQLText 
	 */
	@Test
	public void testPostgreSQLText() throws SQLException {

		for(String[] test : sqlTransformTests) {
			
			String expected = test[1];
			String output = getProtocolSQLText(test[0], true, null);
			
			assertThat(output, is(equalTo(expected)));
		}
	}
	
	@Test
	public void testParse() throws SQLException {
		
		
		for(String[] test : sqlTransformTests) {
			
			SQLText text = new SQLText(test[0]);
			
			SQLTextEscapes.processEscapes(text, null);
			
			System.out.println(text);
		}
	}

}