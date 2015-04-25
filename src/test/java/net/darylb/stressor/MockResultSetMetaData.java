package net.darylb.stressor;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MockResultSetMetaData implements ResultSetMetaData {

	private int length;

	public MockResultSetMetaData(int length) {
		this.length = length;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return length;
	}

	////////////////
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public int getScale(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getTableName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		throw new RuntimeException("Not implemented!");
	}

}
