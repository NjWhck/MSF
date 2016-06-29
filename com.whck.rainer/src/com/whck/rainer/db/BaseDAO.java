package com.whck.rainer.db;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BaseDAO<T> {

	JDBCUtil conn = new JDBCUtil();
	protected Connection connection = null;

	protected Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public BaseDAO() {
		initConnection();
		// ��ò���������
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		persistentClass = (Class<T>) type.getActualTypeArguments()[0];
	}

	/**
	 * ������ݿ�����
	 */
	public void initConnection() {
		connection = conn.getConnection();
	}

	/**
	 * �ر����ݿ�����
	 */
	public void close() {
		conn.closeConn();
	}

	/**
	 * ����
	 */
	public void save(T entity) throws Exception {
		// SQL���,insert into table name (
		String sql = "insert into " + entity.getClass().getSimpleName().toLowerCase() + "(";

		// ��ô����ַ���get�����з����Ķ���
		List<Method> list = this.matchPojoMethods(entity, "get");

		Iterator<Method> iter = list.iterator();

		// ƴ���ֶ�˳�� insert into table name(id,name,email,
		while (iter.hasNext()) {
			Method method = iter.next();
			sql += method.getName().substring(3).toLowerCase() + ",";
		}

		// ȥ�����һ��,����insert insert into table name(id,name,email) values(
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values(";

		// ƴװԤ����SQL���insert insert into table name(id,name,email) values(?,?,?,
		for (int j = 0; j < list.size(); j++) {
			sql += "?,";
		}

		// ȥ��SQL������һ��,����insert insert into table name(id,name,email)
		// values(?,?,?);
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";

		// ����SQL���ƴ�����,��ӡSQL���
		System.out.println(sql);

		// ���Ԥ������������
		PreparedStatement statement = connection.prepareStatement(sql);

		int i = 0;
		// ��ָ����������һ�е�ָ���Ƶ���һ��.
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			// �˳��жϷ���ֵ������,��Ϊ�������ݿ�ʱ�е��ֶ�ֵ��ʽ��Ҫ�ı�,����String,SQL�����'"+abc+"'
			if (method.getReturnType().getSimpleName().indexOf("String") != -1) {
				statement.setString(++i, this.getString(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("float") != -1) {
				statement.setFloat(++i, this.getFloat(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("double") != -1) {
				statement.setDouble(++i, this.getDouble(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("int") != -1) {
				statement.setInt(++i, this.getInt(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("Date") != -1) {
				statement.setDate(++i, this.getDate(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("InputStream") != -1) {
				statement.setAsciiStream(++i, this.getBlob(method, entity), 1440);
			} else {
				statement.setInt(++i, this.getInt(method, entity));
			}
		}
		// ִ��
		conn.execUpdate(statement);
		statement.close();
	}

	/**
	 * �޸�
	 */
	public void update(T entity) throws Exception {
		String sql = "update " + entity.getClass().getSimpleName().toLowerCase() + " set ";

		// ��ø�������get�������󼯺�
		List<Method> list = this.matchPojoMethods(entity, "get");

		// ��ʱMethod����,�������ʱװmethod����.
		Method tempMethod = null;

		// �����޸�ʱ����Ҫ�޸�ID,���԰�˳��Ӳ�����Ӧ�ð�Id�Ƶ����.
		Method idMethod = null;
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			// ����������д���ID�ַ������ҳ���Ϊ2,����ΪID.
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				// ��ID�ֶεĶ����ŵ�һ��������,Ȼ���ڼ�����ɾ��.
				idMethod = tempMethod;
				iter.remove();
				// ���������ȥ��set/get�ַ����Ժ���pojo + "id"�����(��Сд������),����ΪID
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		// �ѵ���ָ���Ƶ���һλ
		iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			sql += tempMethod.getName().substring(3).toLowerCase() + "= ?,";
		}

		// ȥ�����һ��,����
		sql = sql.substring(0, sql.lastIndexOf(","));

		// �������
		sql += " where " + idMethod.getName().substring(3).toLowerCase() + " = ?";

		// SQLƴ�����,��ӡSQL���
		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);

		int i = 0;
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			// �˳��жϷ���ֵ������,��Ϊ�������ݿ�ʱ�е��ֶ�ֵ��ʽ��Ҫ�ı�,����String,SQL�����'"+abc+"'
			if (method.getReturnType().getSimpleName().indexOf("String") != -1) {
				statement.setString(++i, this.getString(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("Date") != -1) {
				statement.setDate(++i, this.getDate(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("InputStream") != -1) {
				statement.setAsciiStream(++i, this.getBlob(method, entity), 1440);
			} else {
				statement.setInt(++i, this.getInt(method, entity));
			}
		}

		// ΪId�ֶ����ֵ
		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		// ִ��SQL���
		statement.executeUpdate();

		// �ر�Ԥ�������
		statement.close();
	}

	/**
	 * ɾ��
	 */
	public void delete(T entity) throws Exception {
		String sql = "delete from " + entity.getClass().getSimpleName().toLowerCase() + " where ";

		// ����ַ���Ϊ"id"���ֶζ���
		Method idMethod = null;

		// ȡ���ַ���Ϊ"id"���ֶζ���
		List<Method> list = this.matchPojoMethods(entity, "get");
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			// ����������д���ID�ַ������ҳ���Ϊ2,����ΪID.
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				// ��ID�ֶεĶ����ŵ�һ��������,Ȼ���ڼ�����ɾ��.
				idMethod = tempMethod;
				iter.remove();
				// ���������ȥ��set/get�ַ����Ժ���pojo + "id"�����(��Сд������),����ΪID
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		sql += idMethod.getName().substring(3).toLowerCase() + " = ?";

		PreparedStatement statement = this.connection.prepareStatement(sql);

		// ΪId�ֶ����ֵ
		int i = 0;
		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		// ִ��
		conn.execUpdate(statement);
		statement.close();
	}

	/* ͨ�����ơ����ڲ�ѯ */
	public List<T> findByCnd(Object object, Object fromDate, Object toDate) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		// ͨ������Ĺ��캯��,��ò��������͵ľ�������.����BaseDAO<T>Ҳ���ǻ��T�ľ�������
		T entity = persistentClass.newInstance();

		Method nameMethod = null;
		Method timeMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		// ����ȡ��Method����
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Id") != -1) {
				nameMethod = tempMethod;
			} else if (tempMethod.getName().indexOf("Rdate") != -1) {
				timeMethod = tempMethod;
			}
		}
		// ��һ����ĸתΪСд
		sql += (nameMethod.getName().substring(3, 4).toLowerCase() + nameMethod.getName().substring(4)).toLowerCase()
				+ " = ?";
		String fieldStr = (timeMethod.getName().substring(3, 4).toLowerCase() + timeMethod.getName().substring(4))
				.toLowerCase();
		sql += " and " + fieldStr + " between ?  and  ? ";
		// ��װ������,��ӡsql���
		System.out.println(sql);
		// �������
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// �ж�name������
		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}
		if (fromDate instanceof Date) {
			statement.setDate(2, (Date) fromDate);
			statement.setDate(3, (Date) toDate);
		} else if (fromDate instanceof String) {
			statement.setString(2, (String) fromDate);
			statement.setString(3, (String) toDate);
		}

		// ִ��sql,ȡ�ò�ѯ�����.
		ResultSet rs = conn.execQuery(statement);

		// ��ָ��ָ���������һ��
		iter = list.iterator();

		// ��װ
		while (rs.next()) {
			entity = persistentClass.newInstance();
			iter = list.iterator();
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// ����list������,method����ȡ���ķ���˳�������ݿ��ֶ�˳��һ��(����:list�ĵ�һ��������setDate,�����ݿⰴ˳��ȡ����"123"ֵ)
					// �������ݿ��ֶβ������ֶ�Ӧ�ķ�ʽȡ.
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
					this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}

		// �رս����
		rs.close();

		// �ر�Ԥ�������
		statement.close();
		return entities;
	}

	/* ͨ�����Ʋ�ѯ */
	public List<T> findByName(Object object) throws Exception {

		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		// ͨ������Ĺ��캯��,��ò��������͵ľ�������.����BaseDAO<T>Ҳ���ǻ��T�ľ�������
		T entity = persistentClass.newInstance();

		// ���Pojo(�򱻲�����)�����ķ�������
		Method idMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		// ����ȡ��Method����
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Zonename") != -1 && tempMethod.getName().substring(3).length() == 8) {
				idMethod = tempMethod;
			} else if ((entity.getClass().getSimpleName() + "Name")
					.equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
			}
		}
		// ��һ����ĸתΪСд
		sql += (idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4)).toLowerCase()
				+ " = ?";

		// ��װ������,��ӡsql���
		System.out.println(sql);

		// �������
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// �ж�name������
		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		// ִ��sql,ȡ�ò�ѯ�����.
		ResultSet rs = conn.execQuery(statement);

		// ��ָ��ָ���������һ��
		iter = list.iterator();

		// ��װ
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// ����list������,method����ȡ���ķ���˳�������ݿ��ֶ�˳��һ��(����:list�ĵ�һ��������setDate,�����ݿⰴ˳��ȡ����"123"ֵ)
					// �������ݿ��ֶβ������ֶ�Ӧ�ķ�ʽȡ.
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}

		// �رս����
		rs.close();

		// �ر�Ԥ�������
		statement.close();

		return entities;
	}

	/**
	 * ͨ��ID��ѯ
	 */
	public T findById(Object object) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";

		// ͨ������Ĺ��캯��,��ò��������͵ľ�������.����BaseDAO<T>Ҳ���ǻ��T�ľ�������
		T entity = persistentClass.newInstance();

		// ���Pojo(�򱻲�����)�����ķ�������
		Method idMethod = null;
		
		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				idMethod = tempMethod;
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
			}
		}
		// ��һ����ĸתΪСд
		sql += idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4) + " = ?";

		// ��װ������,��ӡsql���
		System.out.println(sql);

		// �������
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// �ж�id������
		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		// ִ��sql,ȡ�ò�ѯ�����.
		ResultSet rs = conn.execQuery(statement);

		// ��ָ��ָ���������һ��
		iter = list.iterator();

		// ��װ
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// ����list������,method����ȡ���ķ���˳�������ݿ��ֶ�˳��һ��(����:list�ĵ�һ��������setDate,�����ݿⰴ˳��ȡ����"123"ֵ)
					// �������ݿ��ֶβ������ֶ�Ӧ�ķ�ʽȡ.
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
		}

		// �رս����
		rs.close();

		// �ر�Ԥ�������
		statement.close();

		return entity;
	}

	/**
	 * 
	 * @param timeunit
	 *            ��ѯ��ֵ��ʱ�䵥λ
	 * @param field Сд
	 *            ��ѯ���ֶ�
	 * @param fromDate
	 *            ��ʾ����
	 * @param toDate
	 *            ��ֹ����
	 * @return
	 * @throws Exception
	 */
	public List<T> findAvg(String timeunit, String field, String fromDate, String toDate) throws Exception {

		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(rdate as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,";
		} else {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,DATEPART(minute, cast(rdate as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		// ͨ������Ĺ��캯��,��ò��������͵ľ�������.����BaseDAO<T>Ҳ���ǻ��T�ľ�������
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			// ����ȡ��Method����
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					targetMethods.add(method.getName().substring(3).toLowerCase());
				}
			}

			for (int i = 0; i < targetMethods.size(); i++) {
				String tField = targetMethods.get(i);
				sql += " AVG(" + tField + ") " + tField + " ";
				if (i != targetMethods.size() - 1) {
					sql += ",";
				}  
			}
		} else {
			sql += " AVG(" + field + ") " + field + " ";
		}

		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where rdate between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(rdate as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) , DAY(cast(rdate as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ";
		} else {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ,DATEPART(minute, cast(rdate as datetime)) ";
		}
		// ��װ������,��ӡsql���
		System.out.println(sql);

		// �������
		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		// ִ��sql,ȡ�ò�ѯ�����.
		ResultSet rs = conn.execQuery(statement);

		list = this.matchPojoInheridMethods(entity, "set");
		// ��ָ��ָ���������һ��
		iter = list.iterator();

		// ��װ
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
					this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
				}else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}
		// �رս����
		rs.close();
		// �ر�Ԥ�������
		statement.close();
		return entities;
	}
	/**
	 * @param id
	 * 			      ��id
	 * @param timeunit
	 *            ��ѯ��ֵ��ʱ�䵥λ
	 * @param field Сд
	 *            ��ѯ���ֶ�
	 * @param fromDate
	 *            ��ʾ����
	 * @param toDate
	 *            ��ֹ����
	 * @return
	 * @throws Exception
	 */
	public List<T> findAvg(int id,String timeunit, String field, String fromDate, String toDate) throws Exception {

		System.out.println("TimeUnit:"+timeunit);
		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(rdate as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,";
		} else {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,DATEPART(minute, cast(rdate as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		// ͨ������Ĺ��캯��,��ò��������͵ľ�������.����BaseDAO<T>Ҳ���ǻ��T�ľ�������
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			// ����ȡ��Method����
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					targetMethods.add(method.getName().substring(3).toLowerCase());
				}
			}

			for (int i = 0; i < targetMethods.size(); i++) {
				String tField = targetMethods.get(i);
				sql += " AVG(" + tField + ") " + tField + " ";
				if (i != targetMethods.size() - 1) {
					sql += ",";
				}  
			}
		} else {
			sql += " AVG(" + field + ") " + field + " ";
		}

		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where id = "+id+" and rdate between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(rdate as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) , DAY(cast(rdate as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ";
		} else {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ,DATEPART(minute, cast(rdate as datetime)) ";
		}
		// ��װ������,��ӡsql���
		System.out.println(sql);

		// �������
		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		// ִ��sql,ȡ�ò�ѯ�����.
		ResultSet rs = conn.execQuery(statement);
		list = this.matchPojoInheridMethods(entity, "set");
		// ��ָ��ָ���������һ��
		iter = list.iterator();

		// ��װ
		while (rs.next()) {
			entity = persistentClass.newInstance();
			while (iter.hasNext()) {
				Method method = iter.next();
				try{
					rs.findColumn(method.getName().substring(3).toLowerCase());
					if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
						this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
					} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
						this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
					}
					else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
						this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
					}
				}catch(Exception e){
					
				}
			}
			entities.add(entity);
			iter = list.iterator();
		}
		// �رս����
		rs.close();
		// �ر�Ԥ�������
		statement.close();
		return entities;
	}

	/**
	 * ���˵�ǰPojo�����д������ַ�����Method����,����List����.
	 */
	protected List<Method> matchPojoMethods(T entity, String methodName) {
		// ��õ�ǰPojo���з�������
		Method[] methods = entity.getClass().getDeclaredMethods();

		// List����������д�get�ַ�����Method����
		List<Method> list = new ArrayList<Method>();

		// ���˵�ǰPojo�����д�get�ַ�����Method����,����List����
		for (int index = 0; index < methods.length; index++) {
			if (methods[index].getName().indexOf(methodName) != -1) {
				list.add(methods[index]);
			}
		}
		return list;
	}
	
	/**
	 * ���˵�ǰPojo�༰�������д������ַ�����Method����,����List����.
	 */
	protected List<Method> matchPojoInheridMethods(T entity, String methodName) {
		// ��õ�ǰPojo���з�������
		List<Method> methods =getDeclaredMethods(entity);

		// List����������д�get�ַ�����Method����
		List<Method> list = new ArrayList<Method>();

		// ���˵�ǰPojo�����д�get�ַ�����Method����,����List����
		for(Iterator<Method> it=methods.iterator();it.hasNext();){
			Method m=it.next();
			if (m.getName().indexOf(methodName) != -1) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * ������������Ϊint��Integer����ʱ,���ص�SQL���ֵ.��Ӧget
	 */
	public Integer getInt(Method method, T entity) throws Exception {
		return (Integer) method.invoke(entity, new Object[] {});
	}

	/**
	 * ������������Ϊfloat����ʱ,���ص�SQL���ֵ.��Ӧget
	 */
	public float getFloat(Method method, T entity) throws Exception {
		return (float) method.invoke(entity, new Object[] {});
	}

	/**
	 * ������������Ϊdouble����ʱ,���ص�SQL���ֵ.��Ӧget
	 */
	public double getDouble(Method method, T entity) throws Exception {
		return (double) method.invoke(entity, new Object[] {});
	}

	/**
	 * ������������ΪStringʱ,���ص�SQL���ƴװֵ.����'abc',��Ӧget
	 */
	public String getString(Method method, T entity) throws Exception {
		return (String) method.invoke(entity, new Object[] {});
	}

	/**
	 * ������������ΪBlobʱ,���ص�SQL���ƴװֵ.��Ӧget
	 */
	public InputStream getBlob(Method method, T entity) throws Exception {
		return (InputStream) method.invoke(entity, new Object[] {});
	}

	/**
	 * ������������ΪDateʱ,���ص�SQL���ƴװֵ,��Ӧget
	 */
	public Date getDate(Method method, T entity) throws Exception {
		return (Date) method.invoke(entity, new Object[] {});
	}

	/**
	 * ��������ΪInteger��intʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setInt(Method method, T entity, Integer arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * ��������Ϊfloatʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setFloat(Method method, T entity, float arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * ��������Ϊdoubleʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setDouble(Method method, T entity, double arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * ��������ΪStringʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setString(Method method, T entity, String arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * ��������ΪInputStreamʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setBlob(Method method, T entity, InputStream arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * ��������ΪDateʱ,Ϊentity�ֶ����ò���,��Ӧset
	 */
	public void setDate(Method method, T entity, Date arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}
	
	/** 
     * ѭ������ת��, ��ȡ����� DeclaredMethod 
     * @param object : ������� 
     * @param methodName : �����еķ����� 
     * @param parameterTypes : �����еķ����������� 
     * @return �����еķ������� 
     */  
      
    protected  List<Method> getDeclaredMethods(T entity){  
        List<Method> methods = new ArrayList<>() ;  
          
        for(Class<?> clazz = entity.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
            try {  
            	Method[] tempMethods=clazz.getDeclaredMethods() ;;
                methods.addAll(Arrays.asList(tempMethods));  
            } catch (Exception e) {  
                //������ô����Ҫ��������������쳣��������д�������׳�ȥ��  
                //���������쳣��ӡ���������ף���Ͳ���ִ��clazz = clazz.getSuperclass(),���Ͳ�����뵽��������  
              
            }  
        }  
          
        return methods;  
    }  
}
